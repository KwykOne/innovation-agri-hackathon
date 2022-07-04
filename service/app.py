from flask import Flask,jsonify,request
from flask_cors import CORS, cross_origin
import tensorflow as tf
import numpy as np
import pandas as pd 
import math


app = Flask(__name__)
CORS(app, support_credentials=True)

day = 24*60*60
month = 12*day
year = (365.2425)*day

label_columns = ['modal_beans', 'min_beans', 'max_beans','modal_ragi',
       'min_ragi', 'max_ragi','modal_brinjal', 'min_brinjal',
       'max_brinjal', 'Arrivals_beans', 'Arrivals_brinjal', 'Arrivals_ragi']
multi_label_columns = ['modal_beans', 'min_beans', 'max_beans', 'Arrivals_beans', 'modal_ragi',
       'min_ragi', 'max_ragi', 'Arrivals_ragi', 'modal_brinjal', 'min_brinjal',
       'max_brinjal', 'Arrivals_brinjal', 'sunHour', 'uvIndex', 'DewPointC',
       'cloudcover', 'humidity', 'tempC', 'precipMM', 'pressure', 'Wx', 'Wy',
       'Month sin', 'Month cos', 'Year sin', 'Year cos']

std= pd.read_csv('../dataset/std', index_col=0)
mean= pd.read_csv('../dataset/mean', index_col=0)
trainDf = pd.read_csv('../dataset/final_dataset', index_col=0)

# RNN
lstm_model = tf.keras.models.load_model('../model/price_lstm1')
# Multioutput RNN
lstm_multi_model = tf.keras.models.load_model('../model/price_multi_lstm')
# CNN
conv_model = tf.keras.models.load_model('../model/price_conv')

trainDf.index = pd.to_datetime(trainDf.index, format='%Y.%m.%d %H:%M:%S')
timestamp_s = trainDf.index.map(pd.Timestamp.timestamp)

trainDf['Month sin'] = np.sin(timestamp_s * (2 * np.pi / month))
trainDf['Month cos'] = np.cos(timestamp_s * (2 * np.pi / month))
trainDf['Year sin'] = np.sin(timestamp_s * (2 * np.pi / year))
trainDf['Year cos'] = np.cos(timestamp_s * (2 * np.pi / year))

wv = trainDf.pop('windspeedKmph')
wd_rad = trainDf.pop('winddirDegree')*np.pi / 180

# Calculate the wind x and y components.
trainDf['Wx'] = wv*np.cos(wd_rad)
trainDf['Wy'] = wv*np.sin(wd_rad)

train_mean = trainDf.mean()
train_std = trainDf.std()

# trainDf = (trainDf - train_mean) / train_std

# trainDf = (trainDf - mean) / std

@app.route("/cropdata", methods = ['POST'])
@cross_origin(supports_credentials=True)
def singlestep():
    cropdata = {}
    if request.method == 'POST':
        requestBody = request.json
        cropData = requestBody["cropData"]
        timeLineData = requestBody["timeLineData"]

        # type conversion
        if timeLineData.isnumeric():
            timeLineData = int(timeLineData)
        else: 
            timeLineData = 1

        # latest available price
        currentPrice = 0
        if len(trainDf.columns.values[trainDf.columns.values == 'modal_'+cropData]) > 0:
            currentPrice = (trainDf.loc[trainDf.index.max(),'modal_'+cropData]) / trainDf.loc[trainDf.index.max(),'Arrivals_'+cropData]
        
        response = {
            "name":requestBody["cropData"],
            "currentPrice":currentPrice,
            "currentDate": trainDf.index.max().strftime("%Y-%m-%d")
        }

        ip = np.array(trainDf[trainDf.shape[0]-(timeLineData+1):])
        ip = np.expand_dims(ip, axis = 0)

        pred = lstm_model.predict(ip)

        s = pd.Series(pred[0][pred.shape[1]-1])
        Arrivals = 1
        # load latest data
        for i, o in s.items():
            if label_columns[i].find(cropData) > -1:
                iStd = std.loc[label_columns[i]][0]
                iMean = mean.loc[label_columns[i]][0]
                if label_columns[i].find('modal') > -1:
                    response['Modal'] = ((o * iStd) + iMean)
                elif label_columns[i].find('min') > -1:
                    response['Min'] = ((o * iStd) + iMean)
                # elif label_columns[i].find('Arrivals') > -1:
                #     Arrivals = ((o * iStd) + iMean)
                elif label_columns[i].find('max') > -1:
                    response['Max'] = ((o * iStd) + iMean)
    
        response['Modal'] = response['Modal'] / Arrivals
        response['Min'] = response['Min'] / Arrivals
        response['Max'] = response['Max'] / Arrivals
        response['inc_dec'] = ((response['Modal'] - currentPrice) / (response['Modal'] + currentPrice)) * 100

    return jsonify(response)

@app.route("/multicropdata", methods = ['POST'])
@cross_origin(supports_credentials=True)
def multistep():
    cropdata = {}
    if request.method == 'POST':
        requestBody = request.json
        cropData = requestBody["cropData"]
        # timeLineData = requestBody["timeLineData"]
        timeLineData = 11

        # latest available price
        currentPrice = 0
        if len(trainDf.columns.values[trainDf.columns.values == 'modal_'+cropData]) > 0:
            currentPrice = (trainDf.loc[trainDf.index.max(),'modal_'+cropData])
        
        # initialize response
        response = {
            "name":requestBody["cropData"],
            "currentPrice":currentPrice,
            "currentDate": trainDf.index.max().strftime("%Y-%m-%d"),
            'max_forcast': {}, 'min_forcast': {}, 'model_forcast':{},
            "history":{}
        }

        # read history data
        day = 1
        for val in trainDf['modal_'+cropData][trainDf.shape[0]-9:]:
            response['history']['Day'+str(day)] = val
            day+=1
        # while day <=90:
        #     dIndex = trainDf.index.max() - pd.Timedelta(1, unit='D')
        #     response['history']['Day'+str(day)] = trainDf.loc[dIndex,'modal_'+cropData]
        #     day+=1
        
        # predict
        ip = np.array(trainDf[trainDf.shape[0]-(timeLineData+1):])
        ip = np.expand_dims(ip, axis = 0)

        pred = lstm_model.predict(ip)

        multi_label_columns = label_columns 
        # consturct response
        i = 0
        for s in pred[0]:
            i+=1
            Arrivals = 1
            for j, o in pd.Series(s).items():
                if multi_label_columns[j].find(cropData) > -1:
                    iStd = std.loc[multi_label_columns[j]][0]
                    iMean = mean.loc[multi_label_columns[j]][0]
                    if multi_label_columns[j].find('modal') > -1:
                        response['max_forcast']['Day'+str(i)] = math.floor((o * iStd) + iMean)
                    elif multi_label_columns[j].find('min') > -1:
                        response['min_forcast']['Day'+str(i)] = math.floor((o * iStd) + iMean)
                    elif multi_label_columns[j].find('max') > -1:
                        response['model_forcast']['Day'+str(i)] = math.floor((o * iStd) + iMean)
                    elif multi_label_columns[j].find('Arrivals') > -1:
                        Arrivals = abs((o * iStd) + iMean)

    return jsonify(response)

if __name__ == "__main__":
  app.run(host='0.0.0.0', port=8000, debug=True)
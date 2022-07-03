#!/usr/bin/env python
# coding: utf-8

# In[869]:


#Demo Video: https://drive.google.com/drive/u/0/folders/1KJ4uRCkRKo4m6ci315nCTNUWG1tPXqp9
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
get_ipython().run_line_magic('matplotlib', 'inline')


# In[870]:


#Source1: https://github.com/Open-network-for-digital-commerce/innovation-agri-hackathon/blob/main/Data%20Sets/Mandi_Prices.xlsx
#Source2: http://www.nhb.gov.in/OnlineClient/MonthwiseAnnualPriceandArrivalReport.aspx
#Product and Center Encoding Details
###Product 1.Pinapple 2. Onion
#	Center_Num	Center_Name
#	1	AHMEDABAD / अहमदाबाद
#	2	AMRITSAR / अमृतसर
#	3	BANGALURU / बेंगलुरू
#	4	BARAUT / बड़ौत
#	5	BHOPAL / भोपाल
#	6	BHUBANESHWAR / भुबनेश्वर
#	7	CHANDIGARH / चंडीगढ़
#	8	CHENNAI / चेन्नई
#	9	DEHRADUN / देहरादून
#	10	DELHI / दिल्ली
#	11	GANGATOK / गंगटोक
#	12	GUWAHATI / गुवाहाटी
#	13	HYDERABAD / हैदराबाद
#	14	JAIPUR / जयपुर
#	15	JAMMU / जम्मू
#	16	KOLKATA / कोलकाता
#	17	LUCKNOW / लखनऊ
#	18	MUMBAI / मुंबई
#	19	NAGPUR / नागपुर
#	20	NASIK / नाशिक
#	21	PATNA / पटना
#	22	PUNE / पुणे
#	23	RAIPUR / रायपुर
#	24	RANCHI / रांची
#	25	SHIMLA / शिमला
#	26	SRINAGAR / श्रीनगर
#	27	TRIVENDRUM / त्रिवेन्द्रम
#	28	VARANASI / वाराणसी
#	29	VIJAYAWADA / विजयवाड़ा

dataset = pd.read_csv('/Users/vinothsunder/Downloads/Onion_Pinapple_Price_2yrs_normalized.csv')


# In[872]:


dataset.head(50)


# In[874]:


dataset.describe()


# In[875]:


# X is independent variables(Features used for prediction) and y is dependnet variable the value we want to predict. We can predict retail and Wholesale prices. 
X = dataset[['Total Arrival','Center_Num','Month_Num','Year','Product']]
y = dataset['Retail Avg. Price'] # Predicting retail Price
#y = dataset['W.sale Avg.Price'] # Predicting Wholesale Price


# In[876]:


# using Random Forest Regressir for feature imprortance
import numpy as np
import sklearn as sk
import sklearn.datasets as skd
import sklearn.ensemble as ske
import matplotlib.pyplot as plt
import pandas as pd
get_ipython().run_line_magic('matplotlib', 'inline')
reg = ske.RandomForestRegressor()
reg.fit(X, y)


# In[878]:


# For Choosing the correct features from our dataset, If the feature improtance score is low we can skip the feature
# We can add more features in future like Weather,perishable or any other market data. Since month and weather are related I dint use it here.

fet_ind = np.argsort(reg.feature_importances_)[::-1]
fet_imp = reg.feature_importances_[fet_ind]
fig, ax = plt.subplots(1, 1, figsize=(8, 3))
labels = dataset.columns[fet_ind]
ax=pd.Series(fet_imp, index=labels).plot.bar(x='fet_imp', y='index', rot=0)
ax.set_title('Features importance')
pd.Series(fet_imp,labels)


# In[879]:


dataset_P1 = dataset[dataset['Product'] < 2]
dataset_P2 = dataset[dataset['Product'] > 1]


# In[881]:


# price on different sales Centers
dataset_P1.plot(x='Center_Num', y='Retail Avg. Price', style='o')
plt.title('Pinapple Center_Num vs Retail Avg. Price')
plt.xlabel('Center_Num')
plt.ylabel('Retail Avg. Price')
plt.show()
dataset_P2.plot(x='Center_Num', y='Retail Avg. Price', style='o')
plt.title('Onion Center_Num vs Retail Avg. Price')
plt.xlabel('Center_Num')
plt.ylabel('W.sale Avg.Price')
plt.show()
#Observation Hydrabad has teh lowest price for Pinapple


# In[882]:


# price on different  Accumulated Months Number 1-Jan 2020 30-Jun2022
dataset_P1.plot(x='Acc_Month', y='Retail Avg. Price', style='o')
plt.title('Pinapple Acc_Month vs Retail Avg. Price')
plt.xlabel('Acc_Month')
plt.ylabel('Retail Avg. Price')
plt.show()
dataset_P2.plot(x='Acc_Month', y='Retail Avg. Price', style='o')
plt.title('Onion Acc_Month vs Retail Avg. Price')
plt.xlabel('Acc_Month')
plt.ylabel('Retail Avg. Price')
plt.show()
# Observation Onion Price is Oct, Nov, Dec, Jan, Feb due to Rains Every Year 2020 jan Was the highest


# In[883]:


# price on different Quantities Arrived in Market
dataset_P1.plot(x='Total Arrival', y='Retail Avg. Price', style='o')
plt.title('Pinapple Total Arrival vs Retail Avg. Price')
plt.xlabel('Total Arrival')
plt.ylabel('Retail Avg. Price')
plt.show()
dataset.plot(x='Total Arrival', y='Retail Avg. Price', style='o')
plt.title('Onion Total Arrival vs Retail Avg. Price')
plt.xlabel('Total Arrival')
plt.ylabel('Retail Avg. Price')
plt.show()
# Observation: Lower teh arrival Quantity Higher teh Price


# In[884]:


# price on different  Months Number 1-Jan - 12 Dec
dataset_P1.plot(x='Month_Num', y='Retail Avg. Price', style='o')
plt.title('Pinapple Month_Num vs Retail Avg. Price')
plt.xlabel('Month_Num')
plt.ylabel('Retail Avg. Price')
plt.show()
dataset_P2.plot(x='Month_Num', y='Retail Avg. Price', style='o')
plt.title('Onion Month_Num vs Retail Avg. Price')
plt.xlabel('Month_Num')
plt.ylabel('Retail Avg. Price')
plt.show()
# Observation Onion Price is Oct, Nov, Dec, Jan, Feb due to Rains, Pinapple price is higher in June


# In[885]:


# 80/20 Split of existing data to test and train
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=0)


# In[887]:


#Viewing Test Train set
X_train, X_test, y_train, y_test


# In[888]:


# Running Multivariable Linear Regression on the data set
from sklearn.linear_model import LinearRegression
regressor = LinearRegression()
regressor.fit(X_train, y_train)


# In[889]:


# Getting regression Coeff 
coeff_df = pd.DataFrame(regressor.coef_, X.columns, columns=['Coefficient'])
coeff_df


# In[890]:


y_pred = regressor.predict(X_test)


# In[892]:


#Comparing Predicted and Actual Value in Test dataset
df = pd.DataFrame({'Actual': y_test, 'Predicted': y_pred})
df.head(50)


# In[893]:


y_pred = regressor.predict(X_test)


# In[894]:


from sklearn import metrics
from sklearn.metrics import accuracy_score
print('Mean Absolute Error:', metrics.mean_absolute_error(y_test, y_pred))
print('Mean Squared Error:', metrics.mean_squared_error(y_test, y_pred))
print('R Squared:', metrics.r2_score(y_test, y_pred))
print('Root Mean Squared Error:', np.sqrt(metrics.mean_squared_error(y_test, y_pred)))


# In[895]:


#Viewing Actual vs Predicted points More closer to Diagonal means Predicted value is approximately equal to Actual Value
f, ax = plt.subplots(figsize=(6, 6))
ax.scatter(y_test.values, y_pred)
ax.plot([0, 8000], [0, 8000], ls="--", c=".3")
ax.set(xlim=(0, 8000), ylim=(0, 8000))
plt.title('Actual vs Predicted Price')
plt.xlabel('Actual Price')
plt.ylabel('Predicted')
plt.show()


# In[896]:


#Validation Set: PREDICTING the future PRICE on AUG 2022 Assuming supply is 450-500 on all centers, 
#If you add to below datafile and run you will get future market value.
test = pd.read_csv('/Users/vinothsunder/Downloads/Pinapple_Onion_Test.csv')


# In[897]:


# Validation dataset
test[['Total Arrival','Center_Num','Month_Num','Year','Product']]


# In[898]:


X_test = test[['Total Arrival','Center_Num','Month_Num','Year','Product']]
y_test = test['Retail Avg. Price']
y_pred = regressor.predict(X_test)


# In[899]:


f, ax = plt.subplots(figsize=(6, 6))
ax.scatter(y_test.values, y_pred)
ax.plot([0, 8000], [0, 8000], ls="--", c=".3")
ax.set(xlim=(0, 8000), ylim=(0, 8000))
plt.title('Actual vs Predicted Price')
plt.xlabel('Actual Price')
plt.ylabel('Predicted')
plt.show()


# In[900]:


#TEST DATA There is no Actual Price for Aug only predicted rice
X_test, y_test


# In[901]:


#Predicted vs Actial in Test data CSV AUG doesnot have Actual Price for Product 1.Pinapple 2. Onion
df = pd.DataFrame({'Actual': y_test, 'Predicted': y_pred})
df.head(50)


# In[ ]:





# In[ ]:





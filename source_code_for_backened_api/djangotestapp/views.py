from django.shortcuts import render
from django.http import HttpResponse
from django.http import FileResponse
from django.core.files.storage import FileSystemStorage, default_storage
from django.core.files.base import ContentFile 
import requests
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .serializer import PostSerializer,TestSerializer
from rest_framework import viewsets
import io, os
# from PIL import Image, ImageDraw
import PIL
import cv2
import logging
import psycopg2
import sys
import pytz
from datetime import datetime
# import glob

import numpy as np

# import torch
# import torch.nn as nn
# from torchvision import datasets, models, transforms

# deep learning libraries
import tensorflow as tf
import keras
from keras.utils import load_img, img_to_array
from keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras import applications
from keras.models import Sequential, load_model
from keras.layers import Conv2D, MaxPooling2D, GlobalAveragePooling2D, Flatten, Dense, Dropout
from keras.preprocessing import image
import models
# from sklearn.decomposition import PCA
# from sklearn import svm
from rest_framework import generics
import boto3
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
import qrcode
import base64
from PIL import Image
from base64 import decodestring
from io import BytesIO
import pickle
from django.shortcuts import render
from .models import CaptureImage


object_defect = open("models/model_defect.pkl", "rb")
object_size = open("models/model_size.pkl", "rb")


model_defect = pickle.load(object_defect)
model_size = pickle.load(object_size)

# print(model_defect,model_size)


####################################################
####################################################
####################################################


def test(request):
    return HttpResponse("This is test api")

def sendData(request):
    return JsonResponse({'foo': 'sendDataDone'})


class TestView(viewsets.ViewSet):
    queryset = CaptureImage.objects.all()
    serializer_class = TestSerializer


    def form(self, request):
        # print('i am in views')
        img=CaptureImage(img=request.data['img'])
        img.save()
        print(img.timestamp)
        qr = qrcode.QRCode(version = 1,
                   box_size = 10,
                   border = 5)

        if request.data['commodity_defect_type']!=0:
            rank = model_defect[request.data['commodity_name']][request.data['commodity_defect_type']][request.data['commodity_defect_value']]
        else:
            print(model_size)
            rank = model_size[request.data['commodity_name']][request.data['commodity_size']]

        if rank == 1:
                 shelflife = "3 Days"
        elif rank == 2:
                 shelflife = "2 Days"           
        else:
                shelflife = "1 Day"           

        category = request.data['category']
        commodity_name = request.data['commodity_name']
        commodity_variety = request.data['commodity_variety']
        commodity_size = request.data['commodity_size']
        commodity_defect_type = request.data['commodity_defect_type']
        commodity_defect_value = request.data['commodity_defect_value']
        img_id = img.img_id
        # timestamp = img.timestamp
        # print(timestamp)


        print( commodity_defect_value)
            # Adding data to the instance 'qr'
        qr.add_data(f'http://ec2-3-110-203-9.ap-south-1.compute.amazonaws.com:8000/webpage/?id={img_id}&category={category}&commodity_name={commodity_name}&commodity_variety={commodity_variety}&commodity_size={commodity_size}&commodity_defect_type={commodity_defect_type}&commodity_defect_value={commodity_defect_value}&rank={rank}&shelflife={shelflife}')
 
        qr.make(fit = True)
        qrimg = qr.make_image(fill_color = 'black',
                    back_color = 'white')
           
            # print(uri,type(img),type(uri))
        buffered = BytesIO()
        qrimg.save(buffered, format="PNG")
        img_str = base64.b64encode(buffered.getvalue())
            # print(img_str)
            
        if request.data['commodity_defect_type']!=0:
            rank = model_defect[request.data['commodity_name']][request.data['commodity_defect_type']][request.data['commodity_defect_value']]
        else:
            print(model_size)
            rank = model_size[request.data['commodity_name']][request.data['commodity_size']]

            # image = Image.fromstring('RGB',(width,height),decodestring(imagestr))
            # image.save("foo.png")
 
            # qrimg.save('MyQRCode2.png')  

        #     return Response({"data":serializer.data},status=status.HTTP_200_OK)
        # else:
        #     return Response({"error"},status=status.HTTP_400_BAD_REQUEST) 
        return Response({"data":request.data,"rank":rank,"shelflife":shelflife,"img_str":img_str},status=status.HTTP_200_OK)

   

class FormView(viewsets.ViewSet):
    queryset = CaptureImage.objects.all()
    serializer_class = PostSerializer



    def form(self, request):
        print('i am in views')
        # request.data['img'] = "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" 
        serializer = PostSerializer(data=request.data)
      
        if serializer.is_valid(): 
            # serializer.save()
            qr = qrcode.QRCode(version = 1,
                   box_size = 10,
                   border = 5)

            if serializer.data['commodity_defect_type']!=0:
                rank = model_defect[serializer.data['commodity_name']][serializer.data['commodity_defect_type']][serializer.data['commodity_defect_value']]
            else:
                print(model_size)
                rank = model_size[serializer.data['commodity_name']][serializer.data['commodity_size']]
            
            

            if rank == 1:
                shelflife = "3"
            elif rank == 2:
                shelflife = "2"           
            else:
                shelflife = "1"           

            category = serializer.data['category']
            commodity_name = serializer.data['commodity_name']
            commodity_variety = serializer.data['commodity_variety']
            commodity_size = serializer.data['commodity_size']
            commodity_defect_type = serializer.data['commodity_defect_type']
            commodity_defect_value = serializer.data['commodity_defect_value']
            

            print( commodity_defect_value)
            # Adding data to the instance 'qr'
            qr.add_data(f'http://ec2-3-110-203-9.ap-south-1.compute.amazonaws.com:8000/webpage/?category={category}&commodity_name={commodity_name}&commodity_variety={commodity_variety}&commodity_size={commodity_size}&commodity_defect_type={commodity_defect_type}&commodity_defect_value={commodity_defect_value}&rank={rank}&shelflife={shelflife}')
 
            qr.make(fit = True)
            img = qr.make_image(fill_color = 'black',
                    back_color = 'white')
           
            # print(uri,type(img),type(uri))
            buffered = BytesIO()
            img.save(buffered, format="PNG")
            img_str = base64.b64encode(buffered.getvalue())
            # print(img_str)
            
            if serializer.data['commodity_defect_type']!=0:
                rank = model_defect[serializer.data['commodity_name']][serializer.data['commodity_defect_type']][serializer.data['commodity_defect_value']]
            else:
                print(model_size)
                rank = model_size[serializer.data['commodity_name']][serializer.data['commodity_size']]

            # image = Image.fromstring('RGB',(width,height),decodestring(imagestr))
            # image.save("foo.png")
 
            # img.save('MyQRCode2.png')  

            return Response({"data":serializer.data,"rank":rank,"shelflife":shelflife,"img_str":img_str},status=status.HTTP_200_OK)
        else:
            return Response(serializer.error,status=status.HTTP_400_BAD_REQUEST) 


def webpage(request):
    id = request.GET.get('id')
    print(id)
    img = CaptureImage.objects.get(img_id=id)
    # timestamp = CaptureImage.objects.get(timestamp=id)
    # print(img.img)
    context = {"image":img.img,"timestamp":img.timestamp}
    return render(request,'webpage.html',context)


#!/usr/bin/python
# -*- coding: UTF-8 -*-
from urllib2 import Request

import requests
import json
from requests import exceptions, Response
import request_search
import time
import sys
reload(sys)
sys.setdefaultencoding('utf8')

token = ""
isOK = 1

#鉴权获取token
def getToken():
    url = 'https://lbs.sgmap.cn/authentication/login/sysLogin'
    data = {"appKey": "bf2c0c0bb1fc325ba4bbd7db237de2e2", "appSecret": "b966367b0b963b55aaea8723df7e1563",
            "appSha1": "E7:E4:5A:00:07:21:9B:26:5C:63:40:4B:E5:12:33:CA:00:A6:41:E3",
            "appPackageName": "com.epgis.epgisapp", "deviceModel": "EVA-AL10", "deviceImei": "9e6d8b5243e7368a",
            "deviceOs": "Android", "deviceOsVersion": "8.0.0"}
    response = requests.post(url, data)
    global token
    token = json.loads(response.text).get('resultValue').get('token').get('accessToken')
    print(token)

#发起网络请求
def request(fun, token):
    global isOK
    try:
        response = fun(token)
        print(response.text)
#        success = json.loads(response.text).get('success')
#        if success == 0:
#            isOK = 0
#            print('请求错误：success == false')
    except exceptions.Timeout as e:
        print('请求超时：' + str(e.message))
        isOK = 0
        # raise
    except exceptions.HTTPError as e:
        print('http请求错误:' + str(e.message))
        isOK = 0
        # raise
    else:
        # 通过status_code判断请求结果是否正确
        if response.status_code == 200:
            print(str(response.status_code), response.url)
            return
        else:
            print('请求错误：' + str(response.status_code) + ',' + str(response.reason))
            isOK = 0
            # raise


def isOk():
    if isOK == 0:
        raise

getToken()
time.sleep(3)
request(request_search.regeocode, token)
request(request_search.regeocode_gd, token)
request(request_search.geocode, token)
request(request_search.geocode_gd, token)
request(request_search.placeSearch, token)
request(request_search.placeSearch_gd, token)
request(request_search.inputtipsSearch, token)
request(request_search.inputtipsSearch_gd, token)

request(request_search.powerGridSprite, token)
isOk()

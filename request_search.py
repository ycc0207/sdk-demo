#!/usr/bin/python
# -*- coding: UTF-8 -*-


from urllib2 import Request
import requests
import sys
reload(sys)
sys.setdefaultencoding('utf8')

#逆地理
def regeocode(token):
    url = 'https://lbs.sgmap.cn/geocode/v2/regeo'
    data = {"location": "118.181089,24.48704", "access_token": token}  # 参数
    response = requests.get(url, data)
    return response

#逆地理高德服务
def regeocode_gd(token):
    url = 'https://lbs.sgmap.cn/geocode_gd/v2/regeo'
    data = {"location": "118.181089,24.48704", "access_token": token}  # 参数
    response = requests.get(url, data)
    return response

#地理编码
def geocode(token):
    url = 'https://lbs.sgmap.cn/geocode/v1/geo'
    data = {"address": "厦门大学", "access_token": token}  # 参数
    response = requests.get(url, data)
    return response

#地理编码高德服务
def geocode_gd(token):
    url = 'https://lbs.sgmap.cn/geocode_gd/v1/geo'
    data = {"address": "厦门大学", "access_token": token}  # 参数
    response = requests.get(url, data)
    return response

#关键字搜索
def placeSearch(token):
    url = 'https://lbs.sgmap.cn/place/v1/search'
    data = {"query": "火车站","region": "厦门市","cityLimit":"true","limit":"2", "access_token": token}  # 参数
    response = requests.get(url, data)
    return response

#关键字搜索高德服务
def placeSearch_gd(token):
    url = 'https://lbs.sgmap.cn/place_gd/v1/search'
    data = {"query": "火车站","region": "厦门市","cityLimit":"true","limit":"2", "access_token": token}  # 参数
    response = requests.get(url, data)
    return response

#自动补全搜索
def inputtipsSearch(token):
    url = 'https://lbs.sgmap.cn/place/v1/suggestion'
    data = {"query": "火车站","region": "厦门市","cityLimit":"true","access_token": token}  # 参数
    response = requests.get(url, data)
    return response

#自动补全搜索高德服务
def inputtipsSearch_gd(token):
    url = 'https://lbs.sgmap.cn/place_gd/v1/suggestion'
    data = {"query": "火车站","region": "厦门市","cityLimit":"true","access_token": token}  # 参数
    response = requests.get(url, data)
    return response

#电网雪碧图请求
def powerGridSprite(token):
    url = 'https://lbs.sgmap.cn/styles/v1/powergrid-xm/Geography/sprite@2x.png'
    data = {"access_token": token}  # 参数
    response = requests.get(url,data)
    return response


#!/usr/bin/python
# -*- coding: UTF-8 -*-

import zipfile
import os
import sys
import shutil

basepath = "/var/lib/jenkins/workspace/aegis-sdk-custom";

def zipDir(dirpath,outFullName):
    """
    压缩指定文件夹
    :param dirpath: 目标文件夹路径
    :param outFullName: 压缩文件保存路径+xxxx.zip
    :return: 无
    """
    zip = zipfile.ZipFile(outFullName,"w",zipfile.ZIP_DEFLATED)
    for path,dirnames,filenames in os.walk(dirpath):
        # 去掉目标跟路径，只对目标文件夹下边的文件及文件夹进行压缩
        fpath = path.replace(dirpath,'')

        for filename in filenames:
            zip.write(os.path.join(path,filename),os.path.join(fpath,filename))
    zip.close()


if __name__ == '__main__':
    print("basepath:"+basepath)
    if(len(basepath)>0):
        basepath = basepath + "/"
    parent_path = os.path.dirname(os.path.dirname(basepath))
    print("parent_path:" + parent_path)
    zipDir(basepath, parent_path + "/demo.zip")
    shutil.copy(parent_path + "/demo.zip", basepath + "aegis-sdk-demo/sdk")
    shutil.copy(basepath + "/build_config.txt", basepath + "aegis-sdk-demo/sdk")
    os.unlink(parent_path + "/demo.zip")

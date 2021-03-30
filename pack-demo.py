#!/usr/bin/python
# -*- coding: UTF-8 -*-

import os
import shutil
import zipfile
import io
import sys

# 根路径


basepath = os.path.dirname(sys.argv[0]);
alltypes = [0,1,2,3,4,5,7,8,9]



# 删除模块
def create_select_demo(types):

    #  整理出 copy 目录下的文件夹集合
    deltypes = alltypes;
    selcttypes = types;

    for type in selcttypes:
        deltypes.remove(type)

    print(deltypes)

    for type in deltypes:
        if type == 1 :
            file = "location";
        elif type == 2:
            file = "navisdk";
        elif type == 3:
            file = "offline";
        elif type == 4:
            file = "search";
        elif type == 5:
            file = "thirdnavi";
        elif type == 7:
            file = "ukey";
                # if(os.path.exists(basepath + "aegis-sdk-demo/sdk/ukey-debug.aar")):
                #     os.remove(basepath + "aegis-sdk-demo/sdk/ukey-debug.aar")

            path=basepath+"aegis-sdk-demo/sdk/";
            files=os.listdir(path)
            for f in files:
                print (f)
                if "ukey-sdk" in f and f.endswith(".aar"):
                    os.remove(os.path.join(path,f))
                    continue
            pass
        elif type == 8:
            file = "powergrid";
        elif type == 9:
                file = "routeplugin";

        srcpath = os.path.join(basepath + "aegis-sdk-demo/src/main/java/com/epgis/", file)
        respath = os.path.join(basepath + "aegis-sdk-demo/src/main/", "res-" + file)

        print("dele  srcpath="+srcpath);
        print("dele  respath=" + respath);

        if(os.path.exists(srcpath)):
            shutil.rmtree(srcpath)
        if(os.path.exists(respath)):
            shutil.rmtree(respath)

    # 删除无用的文件
    delete_unuse_file();

    # 删除module
    delete_module();

    # 更新setting文件
    udpate_setting_file();

    # 更新gradle文件
    update_gradle_file();

    # 更新 manifest文件
    update_manifest_file(deltypes)


    # 打包
    # dirpath = basepath;
    # outFullName = basepath + "new_dev_1.0.zip";
    # zipDir(dirpath, outFullName);

def delete_unuse_file():
    if(os.path.exists(basepath + ".gitignore")):
        os.remove(basepath + ".gitignore")
    if(os.path.exists(basepath + "aegis-sdk-demo/.gitignore")):
        os.remove(basepath + "aegis-sdk-demo/.gitignore")
    if(os.path.exists(basepath + ".gradle")):
        shutil.rmtree(basepath + ".gradle")
    if(os.path.exists(basepath + ".idea")):
        shutil.rmtree(basepath + ".idea")
    if(os.path.exists(basepath + ".git")):
        shutil.rmtree(basepath + ".git")
    if(os.path.exists(basepath + "build")):
        shutil.rmtree(basepath + "build")
    if(os.path.exists(basepath + "aegis-sdk-demo/build")):
        shutil.rmtree(basepath + "aegis-sdk-demo/build")



def delete_module():
    shutil.rmtree(basepath + "aegis-module-auth")
    shutil.rmtree(basepath + "aegis-module-common")
    shutil.rmtree(basepath + "aegis-module-data")
    shutil.rmtree(basepath + "aegis-module-location")
    shutil.rmtree(basepath + "aegis-module-log")
    shutil.rmtree(basepath + "aegis-module-offline")
    shutil.rmtree(basepath + "aegis-module-service")

    shutil.rmtree(basepath + "aegis-module-navi3rd")
    shutil.rmtree(basepath + "aegis-plugin-powergrid")
    shutil.rmtree(basepath + "aegis-plugin-route")

    shutil.rmtree(basepath + "aegis-sdk-map")
    shutil.rmtree(basepath + "aegis-sdk-navigation")


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


def udpate_setting_file():


    file = os.path.join(basepath, "settings.gradle")

    print("udpate_setting_file()  file = " + file)

    with io.open(file, "r", encoding="utf-8") as f:
        lines = f.readlines()
        # print(lines)
    with io.open(file, "w", encoding="utf-8") as f_w:
        for line in lines:
            if "aegis-sdk-demo" in line:
                f_w.write(line)

def update_gradle_file():

    file =  os.path.join(basepath,"aegis-sdk-demo/build.gradle")

    print("update_gradle_file()  file = "+file)
    with io.open(file, "r", encoding="utf-8") as f:
        lines = f.readlines()
        # print(lines)
    with io.open(file, "w", encoding="utf-8") as f_w:
        for line in lines:
            if "path:" in line:
                continue
            if "ukey-sdk" in line:
                continue
            if "implementation fileTree" in line:
                f_w.write(line)
                f_w.write("    implementation fileTree(dir: 'sdk', include: ['*.aar'])".decode('unicode_escape') )
                f_w.write("\n".decode('unicode_escape') )
                continue
            f_w.write(line)


    # implementation fileTree(dir: 'sdk', include: ['*.aar'])

    pass

def update_manifest_file(deltypes):

    file = os.path.join(basepath, "aegis-sdk-demo/src/main/AndroidManifest.xml")

    print("update_manifest_file()  file = " + file)

    tmpdeltypes = deltypes;

    # print("update_manifest_file()  deltypes="+tmpdeltypes)
    print(tmpdeltypes)
    with io.open(file, "r", encoding="utf-8") as f:
        lines = f.readlines()
        # print(lines)
    with io.open(file, "w", encoding="utf-8") as f_w:

        islock = False;

        for line in lines:

            for type in tmpdeltypes:

                if type == 1:
                    file = "location";
                elif type == 2:
                    file = "navisdk";
                elif type == 3:
                    file = "offline";
                elif type == 4:
                    file = "search";
                elif type == 5:
                    file = "thirdnavi";
                elif type == 7:
                    file = "ukey";
                elif type == 8:
                   file = "powergrid";
                elif type == 9:
                   file = "routeplugin";

                startmsg = file+" module start"
                endmsg = file+" module end"

                if startmsg in line:
                    islock = True;
                    continue
                elif endmsg in line:
                    islock = False;
                    continue
            if islock :
                pass
            else:
                f_w.write(line)


    pass

def setType():
    file =  os.path.join(basepath,"build_config.txt")
    with io.open(file, "r", encoding="utf-8") as f:
        lines = f.readline()
    str = lines
    strlist = str.split(',')	# 用逗号分割str字符串，并保存到列表
    for value in strlist:	# 循环输出列表值
        # print int(value)
        if value.isdigit():
            types.append(int(value))


# /Users/saiwei/PycharmProjects/SplitSDK/data/copy_dev_1.0/aegis-sdk-demo/src/main/java/com/epgis
# /Users/saiwei/PycharmProjects/SplitSDK/data/copy_dev_1.0/aegis-sdk-demo/src/main/res-location



# 用于sdk demo  1.0  分支
# 将工程量的代码分模块处理
# type = 0 必须  不用处理，默认都有
# type = 1 location  res-location
# type = 2 navisdk  res-navisdk
# type = 3 offline  res-offline
# type = 4 search   res-search
# type = 5 thirdnavi  res-thirdnavi
if __name__ == '__main__':
    print("test2 by saiwei")

    if len(basepath) > 0:
        basepath = basepath + "/"

    # types = [0, 1, 2, 3, 4]
    # types = [0,1,2,3,4,5,6]
    types = [0]

    # types = [0, 3, 6]
    setType();
    print types
    create_select_demo(types);

    # udpate_setting_file();
    # update_gradle_file();
    # update_manifest_file();
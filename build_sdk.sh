#!/bin/bash

path=`cd $(dirname $0);pwd -P`
cd $path

./gradlew aBuildSdk
./gradlew aBuildSdk2

#python navi_aar.py
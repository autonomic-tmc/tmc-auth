#!/bin/bash
set -o errexit

if [ $# -eq 0 ]
  then
    ./gradlew run
  else
    ./gradlew run -PtmcAuthVersion=$1
fi

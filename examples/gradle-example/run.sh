#!/bin/bash
set -o errexit

if [ $# -eq 0 ]; then
  ./gradlew run
else
  ./gradlew run -Ptmc-auth.version="$1"
fi

#!/bin/bash
set -eo errexit

if [ $# -eq 0 ]; then
  ./mvnw -s ./settings.xml spring-boot:run
else
  ./mvnw -s ./settings.xml spring-boot:run -Dtmc-auth.version="$1"
fi

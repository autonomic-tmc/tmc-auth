#!/bin/bash
set -eo errexit

if [ $# -eq 0 ]; then
  mvn -s ./settings.xml spring-boot:run
else
  mvn -s ./settings.xml spring-boot:run -Dtmc-auth.version="$1"
fi

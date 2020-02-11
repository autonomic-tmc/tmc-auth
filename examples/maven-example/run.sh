#!/bin/bash
set -eo errexit

./mvnw -s ./settings.xml spring-boot:run

#!/bin/bash
set -o errexit
set -o pipefail

./mvnw -s ./settings.xml spring-boot:run
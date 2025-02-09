#!/bin/bash

# Navigate to the project root
dir="$(dirname "$0")"
cd "$dir/../" || return

# test will be executed by the tests.sh script
mvn clean package -Dmaven.test.skip=true
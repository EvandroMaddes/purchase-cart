#!/bin/bash

# Navigate to the project root
dir="$(dirname "$0")"
cd "$dir/../" || return

# run the application
#mvn --offline spring-boot:run
java -jar target/demo-0.0.1-SNAPSHOT.jar
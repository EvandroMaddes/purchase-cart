#!/bin/bash

# Navigate to the project root
dir="$(dirname "$0")"
cd "$dir/../" || return

# Use --offline to avoid downloading dependencies if they are already available
mvn clean package
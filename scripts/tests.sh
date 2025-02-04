#!/bin/bash

# Navigate to the project root
dir="$(dirname "$0")"
cd "$dir/../" || return

# Build the application
mvn verify
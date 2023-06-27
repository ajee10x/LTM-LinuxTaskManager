#!/bin/bash

# Find script files and set execute permission
find . -type f -name "*.sh" -exec chmod +x {} +

echo "Execute permission set for all script files."

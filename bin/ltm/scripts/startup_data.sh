#!/bin/bash

# Fetch the list of startup applications
startup_apps=$(ls /etc/init.d)

# Output the collected data
for app in $startup_apps; do
    echo "$app"
done

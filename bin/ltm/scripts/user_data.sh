#!/bin/bash

# Fetch the list of users
users=$(cut -d: -f1 /etc/passwd)

# Output the collected data
echo "$users"

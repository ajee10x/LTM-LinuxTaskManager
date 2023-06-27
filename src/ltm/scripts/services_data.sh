#!/bin/bash

# Fetch the list of services
services=$(service --status-all)

# Output the column labels
echo "Service   Status   Description"

# Output the collected data
echo "$services"

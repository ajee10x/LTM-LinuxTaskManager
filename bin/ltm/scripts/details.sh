#!/bin/bash

# Fetch system details
details=$(uname -a)
details+="\nOperating System: $(lsb_release -ds)"
details+="\nKernel Version: $(uname -r)"
details+="\nCPU Model: $(cat /proc/cpuinfo | grep 'model name' | head -n 1 | cut -d ':' -f 2 | sed -e 's/^[[:space:]]*//')"
details+="\nAvailable Processors: $(nproc)"
details+="\nTotal Memory: $(free -h | awk '/^Mem:/ {print $2}')"
details+="\nFree Memory: $(free -h | awk '/^Mem:/ {print $4}')"
details+="\nDisk Usage: $(df -h --output=size,used,avail / | awk 'NR==2 {print $1, $2, $3}')"

# Output the collected data
echo -e "$details"

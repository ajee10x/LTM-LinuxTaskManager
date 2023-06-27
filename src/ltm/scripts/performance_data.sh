#!/bin/bash

# Fetch CPU information
cpu_info=$(top -bn1 | grep "Cpu(s)" | sed "s/.*, *\([0-9.]*\)%* id.*/\1/" | awk '{printf "%.2f%%", 100 - $1}')

# Fetch memory information
mem_info=$(free -m | awk 'NR==2{printf "%.2f%%", $3*100/$2 }')

# Fetch disk usage information
disk_info=$(df -h --output=pcent / | sed -n 2p)

# Fetch network usage information
network_info=$(ifstat -i eth0 -q 1 1 | awk '/eth0/ {print $7}')

# Output the collected data
echo "CPU Usage: $cpu_info"
echo "Memory Usage: $mem_info"
echo "Disk Usage: $disk_info"
echo "Network Usage (eth0): $network_info"

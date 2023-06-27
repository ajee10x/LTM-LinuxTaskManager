#!/bin/bash

ps -eo comm,pid,%cpu,%mem --sort=-%mem | awk 'NR>1 {print $1, $2, $3, $4}'


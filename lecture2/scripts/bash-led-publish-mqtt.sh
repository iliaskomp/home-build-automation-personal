#!/bin/bash    

led_status=$1

if [ $led_status = "1" ]; then
  mosquitto_pub -h 192.168.3.10 -t led -m "1" 
elif [ $led_status = "0" ]; then
  mosquitto_pub -h 192.168.3.10 -t led -m "0" 
else
  echo "Wrong argument! 0 for led off, 1 for led on"
fi


#!/bin/sh

# check if apache is already running
ps -A | grep -q apache2
if [ $? -eq 1 ]; then
    echo "apache2 is currently stopped, is getting started"
    service apache2 start
    if [ $? -ne 0 ]; then
    	echo "killing apache2 processes"
    	ps -ef | grep apache2 | grep -v grep | awk '{print $2}' | xargs kill -9
    	service apache2 start
    fi	
else
    echo "apache2 is already started"
fi
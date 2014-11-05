#!/bin/sh

# check if apache is already running
ps -A | grep -q httpd
if [ $? -eq 1 ]; then
    echo "httpd is currently stopped, is getting started"
    service httpd start
    if [ $? -ne 0 ]; then
    	echo "killing httpd processes"
    	ps -ef | grep httpd | grep -v grep | awk '{print $2}' | xargs kill -9
    	service httpd start
    fi	
else
    echo "httpd is already started"
fi
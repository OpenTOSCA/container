#!/bin/sh

target_ip=`ifconfig eth0 | grep 'inet addr:' | awk '{print $2}' | sed s/addr://g`
httpdport=`cat /etc/apache2/httpd.conf | grep ^Listen | awk '{print $2}'`
check_count=0
iter_count=0

service apache2 stop
service apache2 start

# check if webserver is online
ps -A | grep -q apache2
if [ $? -eq 1 ]; then
    echo "apache2 is currently stopped, is getting started"
    service apache2 start

    if [ $? -ne 0 ]; then
    	echo "killing apache2 processes"
    	ps -ef | grep apache2 | grep -v grep | awk '{print $2}' | xargs kill -9
    	service apache2 start
    fi
fi


echo "checking apache at $target_ip:$httpdport/test.php"

while test $check_count -eq 0 && test $iter_count -lt 6; do
echo "Check count: $iter_count"
sleep 5
check_count=`curl --stderr /dev/null http://$target_ip:$httpdport/test.php | grep -c "PHP"`
iter_count=`echo $iter_count + 1 | bc`
done

if test $check_count -eq 0
then
echo "Error:  Apache is still not running"
exit -1
else
echo "Apache is running"
exit 0
fi
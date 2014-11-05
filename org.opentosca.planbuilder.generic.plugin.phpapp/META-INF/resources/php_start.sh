#!/bin/sh

target_ip=`ifconfig eth0 | grep 'inet addr:' | awk '{print $2}' | sed s/addr://g`
httpdport=`cat /etc/httpd/conf/httpd.conf | grep ^Listen | awk '{print $2}'`
check_count=0
iter_count=0

service httpd stop
service httpd start

# check if webserver is online
ps -A | grep -q httpd
if [ $? -eq 1 ]; then
    echo "httpd is currently stopped, is getting started"
    service httpd start

    if [ $? -ne 0 ]; then
    	echo "killing httpd processes"
    	ps -ef | grep httpd | grep -v grep | awk '{print $2}' | xargs kill -9
    	service httpd start
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
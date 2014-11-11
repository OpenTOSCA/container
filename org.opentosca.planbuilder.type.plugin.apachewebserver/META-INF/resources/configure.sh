#/bin/sh

iptables='/etc/sysconfig/iptables'
httpdconfig='/etc/httpd/conf/httpd.conf'

## set listening port for apache
sed -i "s/Listen 80/Listen $httpdport/" $httpdconfig

# make changes active through restart apache
ps -A | grep -q httpd
if [ $? -eq 1 ]; then
    echo "httpd is currently stopped, is getting started"
    service httpd start
    
    if [ $? -ne 0 ]; then
    	echo "killing httpd processes"
    	ps -ef | grep httpd | grep -v grep | awk '{print $2}' | xargs kill -9
    	service httpd start
    fi	
        
    ps -A | grep -q httpd 
	if [ $? -eq 0 ]; then
        service httpd stop
    fi    
else
    echo "httpd is beeing restarted, and stopped again"
    service httpd restart
    
    if [ $? -ne 0 ]; then
    	echo "killing httpd processes"
    	ps -ef | grep httpd | grep -v grep | awk '{print $2}' | xargs kill -9
    	service httpd start
    fi	
    
	ps -A | grep -q httpd
	if [ $? -eq 0 ]; then
        service httpd stop
    fi 
fi

## open firewall for this port
echo "apache configure.sh httpdport:$httpdport"

iptables-save | grep -q "OUTPUT -p tcp --sport $httpdport -j ACCEPT"
if [ $? -eq 1 ];then
	iptables -I OUTPUT -p tcp --sport $httpdport -j ACCEPT
fi

iptables-save | grep -q "INPUT -p tcp --dport $httpdport -j ACCEPT"
if [ $? -eq 1 ];then
	iptables -I INPUT -p tcp --dport $httpdport -j ACCEPT
fi
	
iptables-save > $iptables
iptables-restore < $iptables


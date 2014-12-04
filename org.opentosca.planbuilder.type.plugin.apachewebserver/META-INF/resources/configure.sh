#/bin/sh

iptables='/etc/sysconfig/iptables'
httpdconfig='/etc/apche2/ports.conf'

## set listening port for apache
sed -i "s/Listen 80/Listen $httpdport/" $httpdconfig

# make changes active through restart apache
ps -A | grep -q apache2
if [ $? -eq 1 ]; then
    echo "apache2 is currently stopped, is getting started"
    service apache2 start
    
    if [ $? -ne 0 ]; then
    	echo "killing apache2 processes"
    	ps -ef | grep apache2 | grep -v grep | awk '{print $2}' | xargs kill -9
    	service apache2 start
    fi	
        
    ps -A | grep -q apache2 
	if [ $? -eq 0 ]; then
        service apache2 stop
    fi    
else
    echo "apache2 is beeing restarted, and stopped again"
    service apache2 restart
    
    if [ $? -ne 0 ]; then
    	echo "killing apache2 processes"
    	ps -ef | grep apache2 | grep -v grep | awk '{print $2}' | xargs kill -9
    	service apache2 start
    fi	
    
	ps -A | grep -q apache2
	if [ $? -eq 0 ]; then
        service apache2 stop
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


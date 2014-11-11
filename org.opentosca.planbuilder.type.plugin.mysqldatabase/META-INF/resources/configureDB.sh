#!/bin/sh

iptables='/etc/sysconfig/iptables'
mySqlConfig='/etc/my.cnf'

## set mySql Port
cat $iptables | grep -q port=
if [ $? -ne 0 ]; then

#inserting data into /etc/my.cnf
sed -i '
/\[mysqld\]/ a\
port='$Source_DBPort'
' $mySqlConfig

else
	sed -i "s/port=3306/ port=$Source_DBPort/" $mySqlConfig
	service mysqld restart
fi

## open firewall for this port
iptables-save | grep -q "OUTPUT -p tcp --sport $Source_DBPort -j ACCEPT"
if [ $? -eq 1 ];then
	iptables -I OUTPUT -p tcp --sport $Source_DBPort -j ACCEPT
fi

iptables-save | grep -q "INPUT -p tcp --dport $Source_DBPort -j ACCEPT"
if [ $? -eq 1 ];then
	iptables -I INPUT -p tcp --dport $Source_DBPort -j ACCEPT
fi

iptables-save > $iptables
iptables-restore < $iptables

#create DB
# brutal hack
mysql -uroot -p$Target_RootPassword -e "CREATE DATABASE $Source_DBName CHARACTER SET utf8 COLLATE utf8_general_ci;"
#SELECT,INSERT,UPDATE,DELETE,CREATE,CREATE TEMPORARY TABLES,DROP,INDEX,ALTER
mysql -uroot -p$Target_RootPassword -e "GRANT ALL ON $Source_DBName.* TO $Source_DBUser@localhost IDENTIFIED BY '$Source_DBPassword';"

#create users
#mysql -uroot -p$Target_RootPassword -e "CREATE USER '$Source_DBUser'@'%' identified by '$Source_DBPassword';"
#mysql -uroot -p$Target_RootPassword -e "GRANT ALL ON $Source_DBName.* TO '$Source_DBUser'@'%';"

# Grant superuser access
#mysql -uroot -p$Target_RootPassword -e "GRANT SUPER on *.* to '$Source_DBUser'@'localhost';"
#mysql -uroot -p$Target_RootPassword -e "GRANT SUPER on *.* to '$Source_DBUser'@'127.0.0.1';"
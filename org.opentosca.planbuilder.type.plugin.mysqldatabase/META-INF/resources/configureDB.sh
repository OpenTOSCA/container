#!/bin/sh

iptables='/etc/sysconfig/iptables'
mySqlConfig='/etc/mysql/my.cnf'

# TODO we should discuss whether this part really is one of the responsibilities of this plugin
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
	service mysql restart
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
mysql -uroot -p$Target_RootPassword -e "CREATE DATABASE $Source_DBName CHARACTER SET utf8 COLLATE utf8_general_ci;"

#create user
mysql -uroot -p$Target_RootPassword -e "CREATE USER '$Source_DBUser'@'%' identified by '$Source_DBPassword';"

# grant all on db for user
mysql -uroot -p$Target_RootPassword -e "GRANT ALL PRIVILEGES ON $Source_DBName.* TO '$Source_DBUser'@'%' IDENTIFIED BY '$Source_DBPassword' WITH GRANT OPTION;FLUSH PRIVILEGES;"

#create users
#mysql -uroot -p$Target_RootPassword -e "GRANT ALL ON $Source_DBName.* TO '$Source_DBUser'@'%';"

# Grant superuser access
#mysql -uroot -p$Target_RootPassword -e "GRANT SUPER on *.* to '$Source_DBUser'@'localhost';"
#mysql -uroot -p$Target_RootPassword -e "GRANT SUPER on *.* to '$Source_DBUser'@'127.0.0.1';"

service mysqld restart;
service mysql restart;
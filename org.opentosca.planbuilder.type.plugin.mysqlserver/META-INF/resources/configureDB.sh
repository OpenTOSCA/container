#!/bin/sh

mySqlConfig='/etc/mysql/my.cnf'

# remove bind-address field in my.cnf
sed -i '/^bind-address/ d' $mySqlConfig 

# grant all for root
mysql -uroot -p$Target_RootPassword -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY '$Target_RootPassword' WITH GRANT OPTION;FLUSH PRIVILEGES;"

# restart server
service mysqld restart;
service mysql restart;
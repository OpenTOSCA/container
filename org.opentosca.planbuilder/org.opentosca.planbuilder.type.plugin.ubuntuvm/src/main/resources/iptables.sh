#!/bin/sh
sudo iptables -F
sudo iptables -P INPUT DROP
sudo iptables -A INPUT -i lo -p all -j ACCEPT
sudo iptables -A INPUT -m state --state RELATED,ESTABLISHED -j ACCEPT
# we still let the ssh port open, as this is a prototype
# sudo iptables -A INPUT -p tcp -m tcp --dport 22 -j ACCEPT
sudo iptables -A INPUT -j DROP

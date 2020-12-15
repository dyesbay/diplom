ssh root@10.102.16.163 "
mkdir -p /var/expert/logs &&
firewall-cmd --zone=public --permanent --add-service=http &&
firewall-cmd --zone=public --permanent --add-port 8808/tcp &&
firewall-cmd --reload &&
echo '10.102.6.135	app-db' >> /etc/hosts &&
echo '10.102.16.181	app-redis' >> /etc/hosts &&
echo '10.102.16.157	app-socket' >> /etc/hosts &&
exit
"
sed -ie "s@ExecStart=\/usr\/bin\/dockerd -H fd:\/\/@ExecStart=\/usr\/bin\/dockerd -H fd:\/\/ -H tcp:\/\/0.0.0.0:2375 -H unix:///var/run/docker.sock@g" /lib/systemd/system/docker.service
echo 'DOCKER_OPTS="-H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock"' >> /etc/default/docker
systemctl daemon-reload
service docker restart
wait 30

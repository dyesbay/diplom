ssh root@10.102.6.162 "
docker stop app-server &&
docker rm app-server &&
if [[ '$(docker images -q 10.102.6.190:5000/app-server:latest 2> /dev/null)' == '' ]]; then
  docker image rm 10.102.6.190:5000/app-server:latest
fi &&
docker pull 10.102.6.190:5000/app-server:latest &&
docker run -e TZ=Europe/Moscow  -d --network=host -v /var/expert/logs:/var/expert/logs --restart=always --name app-server 10.102.6.190:5000/app-server"


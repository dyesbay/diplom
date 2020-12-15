rm -rf build
rm -rf docker/image/*.tar
rm -rf docker/.env
mkdir -p docker/image
docker rmi app-server:1.0
./gradlew buildDocker -Pmode=test
docker save -o docker/image/app-server-1.0.tar app-server:1.0
scp docker/image/app-server-1.0.tar  root@10.102.6.162:/var/expert/
ssh root@10.102.6.162 "
docker stop app-server &&
docker rm app-server &&
docker image rm app-server:1.0 &&
docker load -i /var/expert/app-server-1.0.tar &&
docker run -e TZ=Europe/Moscow -d --network=host -v /var/expert/logs:/var/expert/logs --restart=always --name app-server app-server:1.0"


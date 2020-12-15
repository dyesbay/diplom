rm -rf build
rm -rf docker/image/*.tar
rm -rf docker/.env
mkdir -p docker/image
docker rmi app-server:1.0
./gradlew buildDocker -Pmode=test
docker save -o docker/image/app-server-1.0.tar app-server:1.0
docker tag app-server:1.0 10.102.6.190:5000/app-server
docker push 10.102.6.190:5000/app-server
Articles
========

This repository is used for following articles:

* [Microservices Development with Scala, Spray, MongoDB, Docker and Ansible](http://technologyconversations.com/2015/01/26/microservices-development-with-scala-spray-mongodb-docker-and-ansible/)

Docker
============

```bash
docker build \
    -t vfarcic/books-service-tests \
    -f Dockerfile.test \
    .

docker run --rm \
    -v /data/.ivy2:/root/.ivy2/cache \
    -v $PWD:/source \
    -v $PWD/target/scala-2.10:/source/target/scala-2.10 \
    -v /data/testdb:/data/db \
    vfarcic/books-service-tests
    
docker build -t vfarcic/books-service .
```

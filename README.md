Articles
========

This repository is used for following articles:

* [Microservices Development with Scala, Spray, MongoDB, Docker and Ansible](http://technologyconversations.com/2015/01/26/microservices-development-with-scala-spray-mongodb-docker-and-ansible/)

Docker
============

Build Tests
-----------

```bash
sudo docker build -t vfarcic/books-service-tests -f Dockerfile.test .
    
sudo docker push vfarcic/books-service-tests
```

Build Production
----------------

```bash
sudo docker-compose run tests

sudo docker build -t vfarcic/books-service .

sudo docker push vfarcic/books-service
```

Run Front-End Tests Watcher
---------------------------

```bash
sudo docker-compose up feTestsWatch
```
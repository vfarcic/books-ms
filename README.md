Articles
========

This repository is used for following articles:

* [Microservices Development with Scala, Spray, MongoDB, Docker and Ansible](http://technologyconversations.com/2015/01/26/microservices-development-with-scala-spray-mongodb-docker-and-ansible/)
* Developing Front-end Microservices With Polymer Web Components and Test-driven Development
** [The First Component](http://technologyconversations.com/2015/08/09/developing-front-end-microservices-with-polymer-web-components-and-test-driven-development-part-15-the-first-component/)
** [Polishing The First Component](http://technologyconversations.com/2015/08/09/developing-front-end-microservices-with-polymer-web-components-and-test-driven-development-part-25-polishing-the-first-component/)
** [The Second Component](http://technologyconversations.com/2015/08/09/developing-front-end-microservices-with-polymer-web-components-and-test-driven-development-part-35-the-second-component/)
** [Styling And Communication](http://technologyconversations.com/2015/08/09/developing-front-end-microservices-with-polymer-web-components-and-test-driven-development-part-45-styling-and-communication/)
** [Using Microservices](http://technologyconversations.com/2015/08/09/developing-front-end-microservices-with-polymer-web-components-and-test-driven-development-part-55-using-microservices/)

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
Microservices Development with Scala, Spray, MongoDB, Docker and Ansible
========================================================================

This article tries to provide one possible approach to building microservices. In this particular case we'll use [Scala](http://www.scala-lang.org/) as programming language. API will be RESTful JSON provided by [Spray](http://spray.io/) and [AKKA](http://akka.io/). [MongoDB](http://www.mongodb.org/) will be used as database. Once everything is done we'll pack it all into a Docker container. [Vagrant](https://www.vagrantup.com/) with [Ansible](http://www.ansible.com/home) will take care of our environment and configuration management needs.

 We'll do the books service. It should be able to do following:

* List all books
* Retrieve all the information related to a book
* Update an existing book
* Delete an existing book

This article will not try to teach everything one should know about Scala, Spray, Akka, MongoDB, Docker, Vagrant, Ansible, TDD, etc. There is no single article that can do that. The goal is to show the flow and setup that one might use when developing services. Actually, most of this article is equally relevant for other types of developments. Docker has much broader usage than microservices, Ansible and CM in general can be used for any types of provisioning, Vagrant is very useful for quick creation of virtual machines, etc.

Environment
-----------

We'll use Ubuntu as development and deployment server. Easiest way to setup a server is with [Vagrant](https://www.vagrantup.com/). If you don't have it already, please download and install it. You'll also need [Git](http://git-scm.com/) to clone with repository. The rest of the article will not require any additional installations.

Let's start by cloning this repo.

```bash
git clone https://github.com/vfarcic/books-service.git
cd books-service
```

Next we'll create an Ubuntu server using Vagrant. The definition is following:

```
# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.synced_folder ".", "/vagrant"
  config.vm.provision "shell", path: "bootstrap.sh"
  config.vm.provider "virtualbox" do |v|
    v.name = "books-service"
    v.memory = 1024
  end
end
```

We defined the box (OS) to be Ubuntu. Sync folder is /Vagrant meaning that everything inside this folder on the host will be available inside /Vagrant directory inside the VM. The rest of things we'll need will be installed using Ansible so we're provisioning our VM with it through the bootstrap.sh script. Finally, name of the VM is "books-service" and it has GB memory assigned.
 
 Let's bring it up! First time it might take a bit of time since Vagrant will need to download the whole Ubuntu distribution. Each next run will be much faster.

```bash
vagrant up
vagrant ssh
ll /vagrant
```

`vagrant up` creates a new VM or brings the existing one to life. With `vagrant ssh` we can enter the newly created box. Finally, `ll /vagrant` list all files within that directory as a proof that all our local files are available inside the VM. 

At the moment we have the VM with Ubuntu and Ansible without any additional packages installed. We'll be adding more soon.


Data Storage
------------

We'll be using MongoDB to store and retrieve data. Instead of installing MongoDB we'll use Docker to run a container with MongoDB. If we'd do that manually, commands would be following:

```bash
sudo mkdir -p /data/db
sudo docker run -d --name mongodb \
  -p 27017:27017 \
  -v /data/db:/data/db \
  dockerfile/mongodb
```

First command creates a directory `/data/db` that will be used by MongoDB to store the data on the host. Second one will download and run Docker container dockerfile/mongodb that contains fully operational MongoDB. `--name` assigns name to the Docker process, `-p` makes ports available outside the container and, finally, `-v` makes host directory available to the Docker process making our MongoDB files permanently stored outside the container. 

However, these commands assume that Docker is installed and our VM does not have it. To install Docker we'll use [Ansible](http://www.ansible.com/home). While we're at it, we'll add Ansible equivalent of the commands used to run MongoDB Docker container.

Preferable way to work with Ansible is to divide configurations into roles. In our case, there are three roles located in ansible/roles. One will make sure that Scala and SBT are installed, the other that Docker is up and running while the last one will run the MongoDB container.

As example, definition of the mongodb role is following.
 
```
- name: Directory is present
  file:
    path=/data/db
    state=directory
  tags: [mongodb]

- name: Container is running
  docker:
    name=mongodb
    image=dockerfile/mongodb
    ports=27017:27017
    volumes=/data/db:/data/db
  tags: [mongodb]
```

It does the same as the script listed above and it should be self explanatory. It makes sure that the directory is present and that the mongodb container is running. Playbook ansible/dev.yml is where we tie it all together.

```
- hosts: localhost
  remote_user: vagrant
  sudo: yes
  roles:
    - scala
    - docker
    - mongodb
```

As the previous example, it should be self explanatory. Every time we run this playbook, all tasks from roles docker and mongodb will be executed.

Let's run it.

[Inside the VM]
```bash
cd /vagrant/ansible
ansible-playbook dev.yml -c local
```

Nice thing about Ansible and Configuration Management in general is that they don't blindly run scripts but are acting only when needed. If you run the above commands the second time, Ansible will detect that everything is in order and do nothing. On the other hand, if, for example, you delete the directory /data/db, Ansible will detect that it is absent and create it again.

That's it. Our development environment with Scala, SBT and MongoDB container is ready. Now it's time to develop our books service.

Books Service
-------------

I love [Scala](http://www.scala-lang.org/) and [AKKA](http://akka.io/). It is a very powerful language and AKKA is my favourite framework for building message driven JVM applications. While it was born from Scala, AKKA can be used with Java as well.

[Spray](http://spray.io/) is simple yet very powerful toolkit for building REST/HTTP based applications. It's asynchronuous, uses AKKA actors and has a great (if weird at the beginning) DSL for defining HTTP routes.

In a TDD fashion, we do tests before implementation. Here's an example of tests for the route that retrieves the list of all books.

```scala
"GET /api/v1/books" should {

  "return OK" in {
    Get("/api/v1/books") ~> route ~> check {
      response.status must equalTo(OK)
    }
  }
    
  "return all books" in {
    val expected = insertBooks(3).map { book =>
      BookReduced(book._id, book.title, book.author)
    }
    Get("/api/v1/books") ~> route ~> check {
      response.entity must not equalTo None
      val books = responseAs[List[BookReduced]]
      books must haveSize(expected.size)
      books must equalTo(expected)
    }
  }

}
```

These are very basic tests that hopefully show the direction one should take to test Spray based APIs. First one makes sure that our route returns the code 200 (OK). The second, after inserting few example books to the DB, validates that they are correctly retrieved. Full source code with all tests can be found in (ServiceSpec.scala)[https://github.com/vfarcic/books-service/blob/master/src/test/scala/com/technologyconversations/api/ServiceSpec.scala].

How would we implement those tests? Here's the code that provides implementation based on the tests above.

```scala
val route = pathPrefix("api" / "v1" / "books") {
  get {
    complete(
      collection.find().toList.map(grater[BookReduced].asObject(_))
    )
   }
}
```

That was easy. We define the route (/api/v1/books), method (GET) and the response inside the `complete` statement. In this particular case, we retrieve all the books from the DB and transform them to the BookReduced case class. Full source code with all methods (GET, PUT, DELETE) can be found in the (ServiceActor.scala)[https://github.com/vfarcic/books-service/blob/master/src/main/scala/com/technologyconversations/api/ServiceActor.scala].

Both tests and implementation presented here are simplified and in the real world scenarios there would be more to be done. Actually, complex routes and scenarios are where Spray truly shines.

While devveloping you can run tests in quick mode.

[Inside the VM]
```bash
cd /vagrant
sbt ~test-quick
```

Whenever source code changes, all affected tests will be re-run automatically. I tend have terminal window with test results displayed at all times and get continuous feedback of the quality of the code I'm working on.

Testing, Building and Deploying
-------------------------------

As any other application, this one should be tested, built and deployed. This would normally mean that we should have JDK, Scala and SBT installed on the machines where testing, building and deployment is performed. However, with Docker the only dependency we need is Docker itself. Everything else is packed inside the container.

Let's create a Docker container with the service. Definition needed for the creation of the container can be found in the [Dockerfile](https://github.com/vfarcic/books-service/blob/master/Dockerfile). 

[Inside the VM]
```bash
cd /vagrant
sbt assembly
sudo docker build -t vfarcic/books-service .
sudo docker push vfarcic/books-service
```

We assemble the JAR (tests are part of the assemble task), build docker container and push it to the Hub. If you're planning to reproduce those steps, please create the account in (hub.docker.com)[https://hub.docker.com/] and change **vfarcic** for your username.

The container that we built contains everything we need to run this service. It is based on Ubuntu, has JDK7, contains an instance of MongoDB and has the JAR that we assembled. From now on this container can be run on any machine that has Docker installed. There is no need for JDK, MongoDB or any other dependency to be installed on the server. Container is self sufficient and can run anywhere.

Let's deploy (run) the container we just created. If you're trying to run it on the same machine as the one we used for development, make sure that MongoDB container is not running. Our new container already contains MongoDB inside.

```bash
sudo docker rm -f mongodb
```

To run the newly created container:

```bash
sudo docker run -d --name books-service \
  -p 8080:8080 \
  -v /data/db:/data/db \
  vfarcic/books-service
```

This can be executed on any server that has the Docker installed. If it's not in the same machine as where we built the container, vfarcic/books-service will be downloaded from the Hub. While running, it will have the port 8080 exposed and share the directory /data/db with the host.
 
Let's try it out. First we should sent PUT requests to insert some test data.

```bash
curl -H 'Content-Type: application/json' -X PUT \
  -d '{"_id": 1, "title": "My First Book", "author": "John Doe", "description": "Not a very good book"}' \
  http://localhost:8080/api/v1/books
curl -H 'Content-Type: application/json' -X PUT \
  -d '{"_id": 2, "title": "My Second Book", "author": "John Doe", "description": "Not a bad as the first book"}' \
  http://localhost:8080/api/v1/books
curl -H 'Content-Type: application/json' -X PUT \
  -d '{"_id": 3, "title": "My Third Book", "author": "John Doe", "description": "Failed writers club"}' \
  http://localhost:8080/api/v1/books
```

Let's check whether the service returns correct data.

```bash
curl -H 'Content-Type: application/json' \
  http://localhost:8080/api/v1/books
```

We can delete a book.

```bash
curl -H 'Content-Type: application/json' -X DELETE \
  http://localhost:8080/api/v1/books/_id/3
```

We can check that deleted book is not present any more.


```bash
curl -H 'Content-Type: application/json' \
  http://localhost:8080/api/v1/books
```

Finally, we can request a specific book.

```bash
curl -H 'Content-Type: application/json' \
  http://localhost:8080/api/v1/books/_id/1
```

Summary
-------

That was a very quick way to develop, build and deploy a microservice. One of the advantages of Docker is that it simplifies deployments by reducing needed dependencies to none. Even though the service we built requires JDK and MongoDB, neither needs to be installed on the destination server. Everything is part of the container that will be run as a Docker process.

Microservices exist for a long time but until recently they did not get enough attention due to problems that arise when trying to provision environments capable of running hundreds if not thousands microservices. Benefits that were gained with microservices (separation, faster development, scalability, etc) were not as big as problems that were created with increased efforts that needed to be put intro deployment and provisioning. With CM tools like Ansible and Docker this effort is almost negligible. On the other hand, development, building and deployment of microservices is faster when compared to monolithis applications.

Spray is a very good choice for microservices. Docker containers shine when they contain everything the application needs but not more. Using big Web servers like JBoss and WebSphere would be an overkill for a single (small) service. Even Web servers with smaller footprint like Tomcat are not needed. [Play!](https://www.playframework.com/) is great for building RESTful APIs. However, it still contains a lot of things we don't need. Spray does only one things and does it well. It provides routing capabilities for RESTful APIs.
  
We could continue adding more features to this service. For example, we could add registration and authentication services. However, that would bring us one step closer to monolithic applications. In microservices world, new services would be new applications and in case of Docker, new containers, each of them listening on a different port and happily responding to our HTTP requests.
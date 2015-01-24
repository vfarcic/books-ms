Microservices Development with Scala, Spray, MongoDB, Docker and Ansible
========================================================================

This article tries to provide one possible approach to building microservices. In this particular case we'll use [Scala](http://www.scala-lang.org/) as programming language. API will be RESTful JSON provided by [Spray](http://spray.io/) and [AKKA](http://akka.io/). [MongoDB](http://www.mongodb.org/) will be used as database. Once everything is done we'll pack it all into a Docker container. [Vagrant](https://www.vagrantup.com/) with [Ansible](http://www.ansible.com/home) will take care of our environment and configuration management needs.

 We'll do the books service. It should be able to do following:

* List all books
* Retrieve all information related to a book
* Update an existing book
* Delete an existing book

This article will not try to teach everything one should know about Scala, Spray, Akka, MongoDB, Docker, Vagrant, Ansible, TDD, etc. There's no single article that can do that. The goal is to show the flow and setup that one might use when developing services. Actually, most of this article is equally relevant for other types of developments. Docker has much broader usage than microservices, Ansible and CM in general should be used for any types of provisioning, Vagrant is very useful for quick creation of virtual machines, etc.

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

We defined the box (OS) to be Ubuntu. Sync folder is /Vagrant meaning that everything inside this folder on the host will be available inside /Vagrant directory inside the VM. The rest of things we'll need will be installed using Ansible so we're provisioning our VM with it through the bootstrap.sh script. Finally, name of the VM is "books-service" and it has 1GB memory assigned.
 
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

Preferable way to work with Ansible is to divide configurations into roles. In our case, there are, at the moment, two roles located in ansible/roles. One will make sure that Docker is up and running while the other will run the MongoDB container.

As example, definition of the mongodb role is:
 
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

It does the same as the script listed above and it should be self explanatory. It makes sure that the directory is present and that the mongodb container is running. Playbook ansible/books.yml is where we tie is all together.

```
- hosts: localhost
  remote_user: vagrant
  sudo: yes
  roles:
    - docker
    - mongodb
```

As the previous example, it should be self explanatory. Every time we run this playbook, all tasks from roles docker and mongodb will be executed.

Let's run it.

[Inside the VM]
```bash
cd /vagrant/ansible
ansible-playbook books.yml -c local
```

Nice thing about Ansible and Configuration Management in general is that they don't blindly run scripts but are acting only when needed. If you run the above commands the second time, Ansible will detect that everything is in order and do nothing. On the other hand, if, for example, you delete the directory /data/db, Ansible will detect that it is absent and create it again.

That's it regarding the data storage. We have the MongoDB container up and running. Now it's time to develop our books service that will utilize it.

Books Service
-------------

I love [Scala](http://www.scala-lang.org/) and [AKKA](http://akka.io/). Scala is a very powerful language and AKKA is my favourite framework for building message driven applications JVM. While it was born from Scala, AKKA can be used with Java as well.

[Spray](http://spray.io/) is simple yet very powerful toolkit for building REST/HTTP based applications. It's asynchronuous, uses AKKA actors and has a great (if weird at the beginning) DSL for defining HTTP routes.
In this article we'll go through Web Components development in context of microservices. We'll use [Polymer](https://www.polymer-project.org) as the library that will help us out. The objective is to create a microservice that will handle full functionality. The service will contain not only back-end API (as is the case with most microservices) but also front-end in form of Web Components. Later on, when the time comes to use the service we're creating, we'll simply import Web Components. That is quite a different approach than what you might be used to. We won't create a Web Application that calls APIs handled by microservices. We'll import parts of the front-end from microservices. Our objective is to have a microservice that contains everything; from front-end to back-end. Please refer to the [TODO](TODO) article for more information that lead to this decision.

Since I am a huge fan of [Test-Driven Development](TODO), everything we do will be done using test first approach. We'll write a test, run all tests and confirm that the last one fails, write implementation, run all tests and confirm that all passed. If you are new to TDD, please read the article [TODO](TODO).

Since the objective is to focus on front-end part of the service we'll be creating, repository with fully operational back-end is already available.

Back-End Server
===============

The code in the [books-service](https://github.com/vfarcic/books-service) repository already contains a Web server. We'll use it both to respond to REST API requests from our Web Components and to serve static files that we'll be creating throughout this exercise.

Assuming that you have [Git](https://git-scm.com/) installed, please clone the [books-service](https://github.com/vfarcic/books-service) repository. **Master** branch has the complete code that you can consult if you get stuck. For now, we'll use **polymer-init** branch that has the full back-end and only the bare minimum we'll need to start working with Polymer.

```bash
git clone -b polymer-init https://github.com/vfarcic/books-service.git
cd books-service
```

The back-end part of the service run on [Scala](http://www.scala-lang.org/) and [SBT](http://www.scala-sbt.org/). The framework we're using is [Spray](http://spray.io/). Don't worry if you are new to Scala. This article is only about JavaScript, Polymer, Test-Driven Development and Microservices. We won't even touch Scala code. However, since we need a back-end and this one was already available, we'll simply use it to respond to HTTP requests that our Web Components will be making. Since the server is already there, we'll also use it to serve static files that we are about to start working on.
 
The server can be run in two different ways. The first one is as a container. Assuming that you installed [Docker](https://www.docker.com/), please run the following command. If you are using Windows, please replace $PWD with the path to the current directory where we cloned the Git repository.

```bash
sudo docker run -t --rm --name books-service-test-fe \
  -v /data/.ivy2:/root/.ivy2/cache \
  -v $PWD:/source \
  -p 8080:8080 \
  -e TEST_TYPE=watch-front \
  vfarcic/books-service-test
```

** TODO: Add second option to run as Vagrant VM**

This command runs Docker container **books-service-test** that contains everything we need (Java, Scala, SBT, etc.). We're mounting few volumes. **/root/.ivy2/cache** is mounted so that dependencies we need are downloaded only the first time we run this container. **/source** contains all the source code. We're also exposing the port server is running on (**8080**). **TEST_TYPE** is the environment variable that is used to decide what type of tests should be run. In this case, we're running client side tests continuously with **gulp**. Finally, the command we're executing is **sbt run**; it starts the server. It will take a while when run for the first time since a lot of libraries will need to be downloaded (both for front-end and back-end). Each consecutive run will be much faster. For more information, please take a look at the [Dockerfile.test](https://github.com/vfarcic/books-service/blob/master/Dockerfile.test).

With back-end up and running and tests being executed every time we change client source code, we're ready to start developing the front-end with Polymer. Docker container that we just run contains both Chrome and Firefox. In the future we could add more browsers to the container but, for now, those two should suffice.

Now that we are all set, let's start writing tests and implementation of our first Polymer Web Component.

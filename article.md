Building services with Scala, Spray, MongoDB and Docker
=======================================================

This article tries to provide one possible approach to building services. In this particular case we'll use [Scala](http://www.scala-lang.org/) as programming language. API will be RESTful JSON provided by [Spray](http://spray.io/). [MongoDB](http://www.mongodb.org/) will provide storage. Once everything is done we'll have to make a decision which architectural approach to take for the future development of this service and pack it into an Docker container.

Data Storage
------------
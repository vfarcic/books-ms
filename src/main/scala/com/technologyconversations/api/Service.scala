package com.technologyconversations.api

import akka.actor.{Props, ActorSystem}

object Service extends App {

  import akka.io.IO
  import spray.can.Http

  implicit val system = ActorSystem("routingSystem")
  val service = system.actorOf(Props[ServiceActor], "service")
  IO(Http) ! Http.Bind(service, "localhost", port = 8080)

}
package com.technologyconversations.api

import org.specs2.mutable.Specification
import scalaj.http._
import scala.util.Properties._

class ServiceInteg extends Specification {

  val domain = envOrElse("DOMAIN", "http://localhost:8080")
  val uri = s"$domain/api/v1/books"

  s"GET $uri" should {

    "return OK" in {
      val response: HttpResponse[String] = Http(uri).asString
      response.code must equalTo(200)
    }

  }

}

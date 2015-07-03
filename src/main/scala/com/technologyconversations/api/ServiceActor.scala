package com.technologyconversations.api

import java.io.File

import akka.actor.Actor
import spray.http.HttpHeaders.RawHeader
import spray.json.DefaultJsonProtocol
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport._
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.MongoClient
import scala.util.Properties._

case class BookReduced(_id: Int, title: String, author: String)
case class Book(_id: Int, title: String, author: String, description: String) {
  require(!title.isEmpty)
  require(!author.isEmpty)
}

class ServiceActor extends Actor with ServiceRoute with StaticRoute {

  val address = envOrElse("DB_PORT_27017_TCP", "localhost:27017")
  val client = MongoClient(MongoClientURI(s"mongodb://$address/"))
  val db = client(envOrElse("DB_DBNAME", "books"))
  val collection = db(envOrElse("DB_COLLECTION", "books"))

  def actorRefFactory = context
  def receive = runRoute {
    respondWithHeaders(RawHeader("Access-Control-Allow-Origin", "*"))
    { serviceRoute ~ staticRoute }
  }

}

trait StaticRoute extends HttpService {

  val staticRoute = pathPrefix("") {
    getFromDirectory("client/")
  }

}

trait ServiceRoute extends HttpService with DefaultJsonProtocol {

  implicit val booksReducedFormat = jsonFormat3(BookReduced)
  implicit val booksFormat = jsonFormat4(Book)
  val collection: MongoCollection

  val serviceRoute = pathPrefix("api" / "v1" / "books") {
    path("_id" / IntNumber) { id =>
      get {
        complete(
          grater[Book].asObject(
            collection.findOne(MongoDBObject("_id" -> id)).get
          )
        )
      } ~ delete {
        complete(
          grater[Book].asObject(
            collection.findAndRemove(MongoDBObject("_id" -> id)).get
          )
        )
      }
    } ~ pathEnd {
      get {
        complete(
          collection.find().toList.map(grater[BookReduced].asObject(_))
        )
      } ~ put {
        entity(as[Book]) { book =>
          collection.update(
            MongoDBObject("_id" -> book._id),
            grater[Book].asDBObject(book),
            upsert = true
          )
          complete(book)
        }
      } ~ post {
        entity(as[Book]) { book =>
          collection.update(
            MongoDBObject("_id" -> book._id),
            grater[Book].asDBObject(book),
            upsert = true
          )
          complete(book)
        }
      }
    }
  }

}
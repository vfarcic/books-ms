package com.technologyconversations.api

import akka.actor.Actor
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

class ServiceActor extends Actor with ServiceRoute {

  val client = MongoClient(
    envOrElse("DB_PORT_27017_TCP_ADDR", "localhost"),
    envOrElse("DB_PORT_27017_TCP_PORT", "27017").toInt
  )
  val db = client(envOrElse("DB_NAME", "books"))
  val collection = db(envOrElse("DB_COLLECTION", "books"))

  def actorRefFactory = context
  def receive = runRoute(route)

}

trait ServiceRoute extends HttpService with DefaultJsonProtocol {

  implicit val booksReducedFormat = jsonFormat3(BookReduced)
  implicit val booksFormat = jsonFormat4(Book)
  val collection: MongoCollection

  val route = pathPrefix("api" / "v1" / "books") {
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
      }
    }
  }

}
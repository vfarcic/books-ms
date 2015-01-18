package com.technologyconversations.api

import akka.actor.Actor
import spray.json.DefaultJsonProtocol
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport._
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.MongoClient

case class BookReduced(_id: Int, title: String, author: String)
case class Book(_id: Int, title: String, author: String, description: String) {
  require(!title.isEmpty)
  require(!author.isEmpty)
}

class ServiceActor extends Actor with ServiceRoute {

  val config = context.system.settings.config
  val client = MongoClient(config.getString("books.db.host"), config.getInt("books.db.port"))
  val db = client(config.getString("books.db.db"))
  val collection = db(config.getString("books.db.collection"))

  def actorRefFactory = context
  def receive = runRoute(route)

}

trait ServiceRoute extends HttpService with DefaultJsonProtocol {

  implicit val booksReducedFormat = jsonFormat3(BookReduced)
  implicit val booksFormat = jsonFormat4(Book)
  val collection: MongoCollection

  val route = pathPrefix("api" / "v1" / "books") {
    pathEnd {
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
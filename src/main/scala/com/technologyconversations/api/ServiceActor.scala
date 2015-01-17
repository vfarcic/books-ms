package com.technologyconversations.api

import akka.actor.Actor
import spray.json.DefaultJsonProtocol
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport._
import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.MongoClient

case class Book(_id: Int, title: String, author: String)

class ServiceActor extends Actor with HttpService with DefaultJsonProtocol {

  implicit val booksFormat = jsonFormat3(Book)
  val config = context.system.settings.config
  val client = MongoClient(config.getString("books.db.host"), config.getInt("books.db.port"))
  val db = client(config.getString("books.db.db"))
  val collection = db(config.getString("books.db.collection"))

  def actorRefFactory = context
  def receive = runRoute(route)

  val route = pathPrefix("api" / "v1" / "books") {
    pathEnd {
      get {
        complete(
          collection.find().toList.map(grater[Book].asObject(_))
        )
      }
    }
  }

}
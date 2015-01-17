package com.technologyconversations.api

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import com.novus.salat.global._
import org.specs2.mutable.Specification
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest
import spray.http.StatusCodes._

//import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._

class ServiceSpec extends Specification with Specs2RouteTest with HttpService with ServiceRoute {

  def actorRefFactory = system

  val client = MongoClient("localhost", 27017)
  val db = client("test-books")
  val collection = db("books")

  "Get" should {

    val uri = "/api/v1/books"

    "return OK" in {
      Get(uri) ~> route ~> check {
        response.status must equalTo(OK)
      }
    }

    "return all books" in {
      deleteBooks()
      val expected: List[Book] = insertBooks(3)
      Get(uri) ~> route ~> check {
        response.entity must not equalTo(None)
        val books = responseAs[List[Book]]
        books must haveSize(expected.size)
        books must equalTo(expected)
      }
    }

  }

  def deleteBooks(): Unit = {
    collection.remove(MongoDBObject.empty)
  }

  def insertBooks(quantity: Int): List[Book] = {
    val books = List.tabulate(quantity)(id => Book(id, s"Title $id", s"Author $id"))
    for (book <- books) {
      collection.insert(grater[Book].asDBObject(book))
    }
    books
  }

}

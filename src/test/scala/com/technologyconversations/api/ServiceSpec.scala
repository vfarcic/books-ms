package com.technologyconversations.api

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import com.novus.salat.global._
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeExample
import spray.routing.HttpService
import spray.testkit.Specs2RouteTest
import spray.http.StatusCodes._

import spray.httpx.SprayJsonSupport._

class ServiceSpec extends Specification with Specs2RouteTest with HttpService with ServiceRoute with BeforeExample {

  val client = MongoClient("localhost", 27017)
  val db = client("books")
  val collection = db("books")
  val uri = "/api/v1/books"

  def actorRefFactory = system
  def before = db.dropDatabase()

  sequential

  "GET" should {

    "return OK" in {
      Get(uri) ~> route ~> check {
        response.status must equalTo(OK)
      }
    }

    "return all books" in {
      val expected = insertBooks(3).map { book =>
        BookReduced(book._id, book.title, book.author)
      }
      Get(uri) ~> route ~> check {
        response.entity must not equalTo None
        val books = responseAs[List[BookReduced]]
        books must haveSize(expected.size)
        books must equalTo(expected)
      }
    }

  }

  "PUT" should {

    val id = 1234
    val expected = Book(id, "PUT title", "Put author", "Put description")

    "return OK" in {
      Put(uri, expected) ~> route ~> check {
        response.status must equalTo(OK)
      }
    }

    "return Book" in {
      Put(uri, expected) ~> route ~> check {
        response.entity must not equalTo None
        val book = responseAs[Book]
        book must equalTo(expected)
      }
    }

    "insert book to the DB" in {
      Put(uri, expected) ~> route ~> check {
        response.status must equalTo(OK)
        val book = getBook(id)
        book must equalTo(expected)
      }
    }

    "update book when it exists in the DB" in {
      collection.insert(grater[Book].asDBObject(expected))
      Put(uri, expected) ~> route ~> check {
        response.status must equalTo(OK)
        val book = getBook(id)
        book must equalTo(expected)
      }
    }

  }

  def insertBooks(quantity: Int): List[Book] = {
    val books = List.tabulate(quantity)(id => Book(id, s"Title $id", s"Author $id", s"Description $id"))
    for (book <- books) {
      collection.insert(grater[Book].asDBObject(book))
    }
    books
  }

  def getBook(id: Int): Book = {
    val dbObject = collection.findOne(MongoDBObject("_id" -> id))
    grater[Book].asObject(dbObject.get)
  }

}

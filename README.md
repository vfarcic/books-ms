MongoDB
=======

[https://registry.hub.docker.com/u/dockerfile/mongodb/](https://registry.hub.docker.com/u/dockerfile/mongodb/)

```bash
sudo mkdir -p /data/db
sudo docker run -d --name mongodb \
  -p 27017:27017 \
  -v /data/db:/data/db \
  dockerfile/mongodb
```

Unit Tests
==========

```bash
sbt ~test-quick
```

```bash
sbt test
```

Package
=======

```bash
sbt assembly
sudo docker build -t vfarcic/books-service .
sudo docker push vfarcic/books-service
```

Run
===

```bash
sudo docker run -d --name books-service \
  -p 8080:8080 \
  -v /data/db:/data/db \
  vfarcic/books-service
```

Manual Tests
============

Insert data
-----------

```bash
curl -H 'Content-Type: application/json' -X PUT \
  -d '{"_id": 1, "title": "My First Book", "author": "John Doe", "description": "Not a very good book"}' \
  http://localhost:8080/api/v1/books
curl -H 'Content-Type: application/json' -X PUT \
  -d '{"_id": 2, "title": "My Second Book", "author": "John Doe", "description": "Not a bad as the first book"}' \
  http://localhost:8080/api/v1/books
curl -H 'Content-Type: application/json' -X PUT \
  -d '{"_id": 3, "title": "My Third Book", "author": "John Doe", "description": "Failed writers club"}' \
  http://localhost:8080/api/v1/books
```

Check that data is inserted
---------------------------

```bash
curl -H 'Content-Type: application/json' \
  http://localhost:8080/api/v1/books
```

Delete a book
-------------

```bash
curl -H 'Content-Type: application/json' -X DELETE \
  http://localhost:8080/api/v1/books/_id/3
```

Check that data is deleted
--------------------------

```bash
curl -H 'Content-Type: application/json' \
  http://localhost:8080/api/v1/books
```

Retrieve specific book
----------------------

```bash
curl -H 'Content-Type: application/json' \
  http://localhost:8080/api/v1/books/_id/1
```
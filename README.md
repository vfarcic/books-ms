Docker
============

Build Tests
-----------

```bash
sudo docker build -t vfarcic/books-service-tests -f Dockerfile.test .
    
sudo docker push vfarcic/books-service-tests
```

Build Production
----------------

```bash
sudo docker-compose run tests

sudo docker build -t vfarcic/books-service .

sudo docker push vfarcic/books-service
```

Run Front-End Tests Watcher
---------------------------

```bash
sudo docker-compose up feTestsWatch
```
Docker
============

Build Tests
-----------

```bash
sudo docker build -t vfarcic/books-ms-tests -f Dockerfile.test .
    
sudo docker push vfarcic/books-ms-tests
```

Build Production
----------------

```bash
sudo docker-compose run tests

sudo docker build -t vfarcic/books-ms .

sudo docker push vfarcic/books-ms
```

Run Front-End Tests Watcher
---------------------------

```bash
sudo docker-compose up feTestsWatch
```
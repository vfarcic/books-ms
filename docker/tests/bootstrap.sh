#!/bin/bash

sudo mkdir -p /data/.ivy2
sudo docker build -t vfarcic/books-service-tests docker/tests/.
sudo docker push vfarcic/books-service-tests
sudo docker run -t --rm -v $PWD:/source -v /data/.ivy2:/root/.ivy2/cache books-service-tests
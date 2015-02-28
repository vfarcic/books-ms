#!/bin/sh

sudo docker run -t --rm \
  -v $PWD/target/scala-2.10:/source/target/scala-2.10 \
  -v /data/.ivy2:/root/.ivy2/cache \
  vfarcic/books-service-tests

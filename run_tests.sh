#!/bin/bash

set -e

# Specification tests example
# sudo docker run -t --rm \
#   -v $PWD/target/scala-2.10:/source/target/scala-2.10 \
#   -v /data/.ivy2:/root/.ivy2/cache \
#   vfarcic/books-service-tests

# Integration tests example
# sudo docker run -t --rm \
#   -v /data/.ivy2:/root/.ivy2/cache \
#   -e TEST_TYPE=integ \
#   -e DOMAIN=http://172.17.42.1 \
#   vfarcic/books-service-tests

if [ "$TEST_TYPE" = "integ" ]
then
  sbt "testOnly *Integ"
elif [ "$TEST_TYPE" = "watch-front" ]
then
  mongod &
  sbt run &
  cd /source/client
  Xvfb :1 -screen 0 1024x768x16 &> xvfb.log  &
  gulp watch
elif [ "$TEST_TYPE" = "all" ]
then
  mongod &
  sbt "testOnly *Spec"
  mongod --shutdown
  cd /source/client
  Xvfb :1 -screen 0 1024x768x16 &> xvfb.log  &
  gulp test:local
  cd /source
  sbt assembly
else
  mongod &
  sbt "testOnly *Spec"
  mongod --shutdown
  sbt assembly
fi

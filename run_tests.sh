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
else
  mongod &
  sbt "testOnly *Spec"
  sbt assembly
  mongod --shutdown
fi

#!/bin/bash

set -e

if [ "$TEST_TYPE" = "integ" ]
then
  sbt "testOnly *Integ"
else
  mongod &
  sbt "testOnly *Spec"
  sbt assembly
  mongod --shutdown
fi

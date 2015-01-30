#!/bin/bash

set -e

mongod &
sbt assembly
mongod --shutdown

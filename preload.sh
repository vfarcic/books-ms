#!/usr/bin/env bash

docker pull vfarcic/books-ms-tests

cd /vagrant

docker-compose -f docker-compose-dev.yml run testsLocal
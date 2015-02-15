#!/bin/bash

mv Dockerfile Dockerfile.orig
cp Dockerfile.test Dockerfile
docker build -t vfarcic/books-service-tests .
mv Dockerfile.orig Dockerfile

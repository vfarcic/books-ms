#!/bin/bash

tar --transform='s|Dockerfile.test|Dockerfile|' -cz * | docker build -t vfarcic/books-service-tests -

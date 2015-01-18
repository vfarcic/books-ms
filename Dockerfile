# Version: 0.1
FROM ubuntu:14.04
MAINTAINER Viktor Farcic "viktor@farcic.com"

# General
RUN apt-get update
RUN apt-get -y install --no-install-recommends openjdk-7-jdk && \
    apt-get -y autoremove && \
    apt-get clean all

# MongoDB
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10 && \
    echo 'deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen' > /etc/apt/sources.list.d/mongodb.list && \
    apt-get update && \
    apt-get install -y mongodb-org && \
    rm -rf /var/lib/apt/lists/*
VOLUME ["/data/db"]
# mongod

# Service
COPY target/scala-2.10/books-service-assembly-1.0.jar /bs/bs.jar
WORKDIR /bs

# Default command
CMD ["java", "-jar", "bs.jar"]

EXPOSE 8080
EXPOSE 27017
EXPOSE 28017
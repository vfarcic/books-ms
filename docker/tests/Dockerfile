# Version: 0.1
FROM ubuntu:14.04
MAINTAINER Viktor Farcic "viktor@farcic.com"

# Packages
RUN echo "deb http://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN apt-get update && \
    apt-get -y --force-yes install --no-install-recommends openjdk-7-jdk mongodb wget sbt && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
RUN wget -q http://downloads.typesafe.com/scala/2.11.5/scala-2.11.5.deb && \
    dpkg -i scala-2.11.5.deb && \
    rm scala-2.11.5.deb

RUN mkdir -p /data/db
ENV TEST_TYPE "spec"
ENV DOMAIN "http://172.17.42.1"
VOLUME ["/source", "/root/.ivy2/cache"]
WORKDIR /source
CMD ["/source/run_tests.sh"]
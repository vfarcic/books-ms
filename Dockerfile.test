FROM debian:jessie
MAINTAINER Viktor Farcic "viktor@farcic.com"

ENV VERSION 1.0

RUN apt-get update

# CUrl
RUN apt-get -y install curl

# Dependencies
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash - && \
    echo "deb http://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list && \
    curl -sL https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list && \
    echo "deb http://packages.linuxmint.com debian import" >> /etc/apt/sources.list

# Mongo, NodeJS, Git, SBT, xvfb, FireFox, Chrome
RUN apt-get update && \
    apt-get -y --fix-missing install wget bzip2 make g++ && \
    apt-get -y --force-yes --fix-missing install --no-install-recommends mongodb git sbt=0.13.13 xvfb nodejs firefox google-chrome-stable && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Scala
RUN curl -O -q http://downloads.typesafe.com/scala/2.11.5/scala-2.11.5.deb && \
    dpkg -i scala-2.11.5.deb && \
    rm scala-2.11.5.deb

# Gulp, bower
RUN npm install -g gulp bower

# Dirs
RUN mkdir /source
RUN mkdir -p /data/db

ADD project /source/project
ADD build.sbt /source/build.sbt
ADD client/bower.json /source/client/bower.json
ADD client/gulpfile.js /source/client/gulpfile.js
ADD client/package.json /source/client/package.json
ADD client/wct.conf.js /source/client/wct.conf.js
ADD client/test.html /source/client/test.html
ADD run_tests.sh /source/run_tests.sh

# Dependencies
RUN cd /source && sbt update
RUN cd /source/client && npm install && bower install --allow-root --config.interactive=false -s

# Envs
ENV TEST_TYPE "spec"
ENV DOMAIN "http://172.17.42.1"
ENV DISPLAY ":1.0"
ENV DB_HOST localhost

WORKDIR /source
VOLUME ["/source", "/source/target/scala-2.10", "/root/.ivy2/cache", "/data/db"]

CMD ["/source/run_tests.sh"]

EXPOSE 8080

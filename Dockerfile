FROM debian:jessie
MAINTAINER Viktor Farcic "viktor@farcic.com"

RUN apt-get update && \
    apt-get install -y --force-yes --no-install-recommends openjdk-7-jdk && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

ENV DB_DBNAME books
ENV DB_COLLECTION books
ENV DB_HOST localhost

COPY run.sh /run.sh
RUN chmod +x /run.sh

COPY target/scala-2.10/books-ms-assembly-1.0.jar /bs.jar
COPY client/components /client/components

CMD ["/run.sh"]

EXPOSE 8080
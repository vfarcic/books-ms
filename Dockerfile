#FROM openjdk
FROM openjdk:7u181-jdk-jessie
MAINTAINER Viktor Farcic "viktor@farcic.com"

ENV DB_DBNAME books
ENV DB_COLLECTION books
ENV DB_HOST localhost

COPY run.sh /run.sh
RUN chmod +x /run.sh

COPY target/scala-2.10/books-ms-assembly-1.0.jar /bs.jar
COPY client /client

CMD ["sh","/run.sh"]

EXPOSE 8080

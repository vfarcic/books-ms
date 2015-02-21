#!/bin/bash

if [ -z "$DB_PORT_27017_TCP_ADDR" ]; then
  mongod &
fi
java -jar bs.jar
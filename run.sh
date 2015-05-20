#!/bin/bash

if [ -z "$DB_PORT_27017_TCP" ]; then
  mongod &
fi
java -jar bs.jar
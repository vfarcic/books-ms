#!/usr/bin/env bash

sbt compile
cd client
npm install
bower install --allow-root --config.interactive=false -s
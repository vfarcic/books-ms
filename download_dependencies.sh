#!/usr/bin/env bash

sbt compile
cd client
npm install --no-bin-links
bower install --allow-root --config.interactive=false -s
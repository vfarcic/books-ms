#!/usr/bin/env bash

sbt assembly
cd client
npm install
bower install --allow-root --config.interactive=false -s
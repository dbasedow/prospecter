#!/bin/sh

command -v java >/dev/null 2>&1 || { echo >&2 "Java not installed or not properly configured."; exit 1; }

java -Xmx512m -server -jar prospecter.jar data/server.json
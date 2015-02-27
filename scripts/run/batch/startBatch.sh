#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/conf.sh;
java -jar -Dspring.profiles.active=${profile} $@ ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar &
tail -f ${logsFolder}/batch/*

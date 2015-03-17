#!/bin/bash
echo LOAD CONF LOCAL

server=

cliPackageFolder=/home/jcharlet/_workspace/cat/taxonomy-cli/target
wsPackageFolder=/home/jcharlet/_workspace/cat/taxonomy-ws/target

batchPackageFolder=/home/jcharlet/_workspace/cat/taxonomy-batch/target
agentPath=/home/jcharlet/.m2/repository/org/springframework/spring-instrument/4.0.7.RELEASE/spring-instrument-4.0.7.RELEASE.jar

scriptsFolder=
tmpFolder=

logsFolder=/home/jcharlet/_workspace/cat/bin/logs
iaViewIndexFolder=/mnt/search_indexes/index_20150217

profile=local,queryBased
mongoDbHostName=localhost
port=8090

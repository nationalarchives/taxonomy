#!/bin/bash
echo LOAD CONF LOCAL

javaBinary=/usr/java/jdk1.8.0_40/bin/java
server=local

cliPackageFolder=/home/jcharlet/_workspace/cat/taxonomy-cli/target
wsPackageFolder=/home/jcharlet/_workspace/cat/taxonomy-ws/target

batchPackageFolder=/home/jcharlet/_workspace/cat/taxonomy-batch/target

appConfFolder=/home/jcharlet/_workspace/cat_configuration
scriptsFolder=
tmpFolder=/tmp

logsFolder=/home/jcharlet/_workspace/cat/bin/logs
iaViewIndexFolder=/mnt/search_indexes/index_20150217

profile=local,queryBased
mongoDbHostName=localhost
port=8090


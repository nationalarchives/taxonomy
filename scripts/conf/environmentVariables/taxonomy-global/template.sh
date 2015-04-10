#!/bin/bash
echo load conf global local
confFolder=/home/jcharlet/_workspace/sysdev-config/applications

javaBinary=/usr/java/jdk1.8.0_40/bin/java
server=local

cliPackageFolder=/home/jcharlet/_workspace/cat/taxonomy-cli/target
wsPackageFolder=/home/jcharlet/_workspace/cat/taxonomy-ws/target

batchPackageFolder=/home/jcharlet/_workspace/cat/taxonomy-batch/target

scriptsFolder=
tmpFolder=/tmp

logsFolder=/home/jcharlet/apps/taxonomy/logs
iaViewIndexFolder=/mnt/search_indexes/index_20150217

profile=queryBased
mongoDbHostName=localhost
port=8090
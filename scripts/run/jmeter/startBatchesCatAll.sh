#!/bin/bash
echo $(pwd)
currentDirectory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );
cd $currentDirectory;
source ../batch/taxonomyBatchRunTool.sh -bt masterSlaveCluster -ns 1 --doNotDisplayLogs -aa -taxonomyCatAllJmeter
cd $currentDirectory;
source ../../conf/exportEnvVar.sh taxonomy-global;

sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/batch/master.log | { sed "/PROGRESS OF CATEGORISATION/ q" && kill $$ ;}'
echo APPS STARTED
exit 0
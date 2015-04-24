#!/bin/bash
echo $(pwd)
currentDirectory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );
cd $currentDirectory;
source ../batch/taxonomyBatchRunTool.sh -bt supervisorWorkerCluster -ns 1 --doNotDisplayLogs -aa -taxonomyCatAllJmeter

sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/batch/supervisor.log | { sed "/PROGRESS OF CATEGORISATION/ q" && kill $$ ;}'
echo APPS STARTED
exit 0
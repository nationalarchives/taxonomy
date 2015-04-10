#!/bin/bash
cd "$(dirname "$0")"
source ../batch/taxonomyBatchRunTool.sh -bt masterSlaveCluster -ns 1 --doNotDisplayLogs -aa -taxonomyCatAllJmeter

sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/batch/logging.log | { sed "/PROGRESS OF CATEGORISATION/ q" && kill $$ ;}'
echo APPS STARTED
exit 0
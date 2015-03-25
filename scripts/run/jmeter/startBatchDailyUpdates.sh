#!/bin/bash
cd "$(dirname "$0")"
source ../../conf/conf.sh;
source ../batch/taxonomyBatchRunTool.sh -bt dailyUpdates --doNotDisplayLogs -aa -taxonomyDailyUpdatesJmeter

sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/batch/logging.log | { sed "/Started BatchApplication/ q" && kill $$ ;}'
echo APP STARTED
exit 0
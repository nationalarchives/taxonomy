#!/bin/bash
cd "$(dirname "$0")"
source ../../conf/conf.sh;
source ../ws/startWS.sh -taxonomyWSJmeter

sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/ws/logging.log | { sed "/Started WSApplication/ q" && kill $$ ;}'
echo APP STARTED
exit 0
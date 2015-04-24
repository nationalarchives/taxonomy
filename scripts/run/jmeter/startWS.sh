#!/bin/bash
directory=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );
cd $directory;
source ../ws/startWS.sh -taxonomyWSJmeter
touch ${logsFolder}/ws/logging.log;
sh -c 'tail --pid=$$ -f -n 3 '${logsFolder}'/ws/logging.log | { sed "/Started WSApplication/ q" && kill $$ ;}'
echo APP STARTED
exit 0
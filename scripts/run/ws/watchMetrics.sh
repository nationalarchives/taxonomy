#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/conf.sh;
watch -n 1 --differences=cumulative 'curl -s http://localhost:'${port}'/metrics --noproxy localhost | python -mjson.tool'

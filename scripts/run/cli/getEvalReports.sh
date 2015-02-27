#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/conf.sh;
mongo ${mongoDbHostName}:27017/taxonomy --eval 'printjson(db.evaluationReports.find({},{avgRecall:1,avgAccuracy:1,comments:1,timestamp:1}).sort({_id:-1}).limit(5).toArray())'

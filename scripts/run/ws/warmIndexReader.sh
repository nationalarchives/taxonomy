#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );

source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global;
counter=1;
mongo ${mongoDbHostName}:27017/taxonomy --eval 'printjson(db.categories.find({},{qry:1,_id:0}).toArray())' | egrep -v "\[|{|}|\]" | sed s/\"qry\"/{\"categoryQuery\"/g | sed 's/.$/",\"limit\":\"1\"}/g' | grep -v MongoDB | grep -v "connecting to:" | while read -r line; 
do 
	echo $counter;
	curl http://localhost:${port}/taxonomy/search --data "$line" -H "content-type: application/json" --noproxy localhost; 
	echo;
	echo;
	counter=$((counter+1)); 
done;

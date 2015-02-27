#!/bin/bash
cd "$(dirname "$0")"



usage ()
{
	echo
	echo "NAME taxonomyBatchRunTool - tool to run taxonomy batch application" 
	echo
	echo "DESCRIPTION"
	echo 
	#echo "	-a --application <applicationName> 		Run the application among the following: ws, cli, batch	
	echo "	-cm --categorisationMode <categorisationMode>		Select categorisation mode between those: tsetBased, qryBased	
	echo
	echo "	-ra --remoteApplication <remoteApplication> 		Run the application using a remote tool: Eclipse debugger, JProfiler
	echo 	
	echo "	-ll --logLevel <logLevel> 		Overwrite the default log level for taxonomy classes	
	echo "	-sd --springDebug 		Run Spring in Debug mode (see auto configuration et other useful stuff)
	echo
	#echo "	-tl --tailLogs		Tail logs for the chosen application	
	#echo "	- -- <>	
	echo
	echo "	-pl --platform <platform>	target platform (local, tooling, test)"
	echo
	echo "	-h --help			display help"
	echo 
	exit
}



platform=

# http://linuxcommand.org/lc3_wss0080.php
while [ "$1" != "" ]; do
    case $1 in
        -pl | --platform )      shift
        						platform=$1
                                ;;
        -h | --help )           usage
                                exit
                                ;;
        * )                     usage
                                exit 1
    esac
    shift
done


function_loadVars () {
	platform=$1
	case $platform in
	        local )     
	        	source ../conf/conf-local.sh 
	            ;;
	        test )      
	        	source ../conf/conf-test.sh 
	            ;;
	        tooling )   
	        	source ../conf/conf-tooling.sh 
	            ;;
	esac
}

function_loadVars $platform


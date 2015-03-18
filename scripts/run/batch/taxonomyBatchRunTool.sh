#!/bin/bash
cd "$(dirname "$0")"
source ../../conf/conf.sh;



usage ()
{
	echo
	echo "NAME taxonomyBatchRunTool - tool to run taxonomy batch application" 
	echo
	echo "DESCRIPTION"
	echo 
	echo "	-bt --batchType <applicationName> 		Run the batch app among the following types:" 
	echo "								'master' : Categorise All - Master	"
	echo "								'slave' : Categorise All - Slave	"
	echo "								'dailyUpdates' : apply daily Updates	"
	echo "								'updateSolr' : update Solr from Mongo db entries"
	echo
	echo "	-ja --jvmArgs <jvmArgs>		provide jvm arguments"
	echo
	echo "	-aa --applicationArgs <application args>	provide application arguments"
	echo
	echo "	-h --help			display help"
	echo 
	exit
}

applicationArgs=
jvmArgs=
batchType=

# Tutorial on shell script with list of operators for if, while, etc statements:
# http://linuxcommand.org/lc3_wss0080.php
if [ -z "$1" ]
then
	usage
    exit
fi
while [ "$1" != "" ]; do
    case $1 in
        -ja | --jvmArgs )      	shift
        						jvmArgs=$1
                                ;;
        -aa | --applicationArgs ) shift
        						applicationArgs=$1
                                ;;
        -bt | --batchType )     shift
								batchType=$1
                                ;;
        -h | --help )           usage
                                exit
                                ;;
        * )                     usage
                                exit 1
    esac
    shift
done

batchTypeBasedJvmArgs=
batchTypeBasedApplicationArgs=


case $batchType in
        master )     
			batchTypeBasedJvmArgs="-javaagent:${agentPath} -Xbootclasspath/a:${agentPath}"
			batchTypeBasedApplicationArgs="--batch.role.udpate-solr-cloud=false --batch.role.check-categorisation-request-messages=false --batch.role.categorise-all=true --batch.role.categorise-all.supervisor=true --batch.role.categorise-all.slave=false --server.port=0"
            ;;
        slave )      
			batchTypeBasedJvmArgs="-javaagent:${agentPath} -Xbootclasspath/a:${agentPath} -Dakka.remote.netty.tcp.port=0"
			batchTypeBasedApplicationArgs="--batch.role.udpate-solr-cloud=false --batch.role.check-categorisation-request-messages=false --batch.role.categorise-all=true --batch.role.categorise-all.supervisor=false --batch.role.categorise-all.slave=true --server.port=0"
            ;;
        dailyUpdates )   
			batchTypeBasedJvmArgs=
			batchTypeBasedApplicationArgs="--batch.role.udpate-solr-cloud=true --batch.role.check-categorisation-request-messages=true --server.port=0"
            ;;
        updateSolr )   
			batchTypeBasedJvmArgs=
			batchTypeBasedApplicationArgs="--batch.role.udpate-solr-cloud=true --server.port=0"
            ;;
esac

jvmArgs=$(echo $batchTypeBasedJvmArgs $jvmArgs);
applicationArgs=$(echo $batchTypeBasedApplicationArgs $applicationArgs);

echo "JVM ARGS: " $jvmArgs
echo "APP ARGS: " $applicationArgs 
echo java -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs
java -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs 
#java -jar -Dspring.profiles.active=${profile} $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs &
#tail -f ${logsFolder}/batch/*

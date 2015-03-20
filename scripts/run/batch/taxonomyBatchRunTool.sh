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
	echo "								'masterSlaveCluster' : Categorise All - Master + Cluster of n slaves"
	echo "								'dailyUpdates' : apply daily Updates	"
	echo "								'updateSolr' : update Solr from Mongo db entries"
	echo
	echo "	-adn --afterDocNumber <logName>		specific to Slave: after doc number"
	echo "								to restart categorisation after services were stopped"
	echo
	echo "	-ns --numberOfSlaves <logName>		Specific to MasterSlaveCluster: number of slaves instantiated"
	echo
	echo "	-ln --logName <logName>		provide log name"
	echo
	echo "	-ja --jvmArgs <jvmArgs>		provide jvm arguments"
	echo
	echo "	-aa --applicationArgs <application args>	provide application arguments"
	echo
	echo "	-h --help			display help"
	echo 
	exit
}

inputApplicationArgs=
inputJvmArgs=
batchType=
afterDocNumber=
numberOfSlaves=
logName=

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
        						inputJvmArgs=$1
                                ;;
        -aa | --applicationArgs ) shift
        						inputApplicationArgs=$1
                                ;;
        -bt | --batchType )     shift
								batchType=$1
                                ;;
        -ns | --numberOfSlaves )     shift
								numberOfSlaves=$1
                                ;;
        -adn | --afterDocNumber )     shift
								afterDocNumber=$1
                                ;;
        -ln | --logName )     shift
								logName=$1
                                ;;
        -h | --help )           usage
                                exit
                                ;;
        * )                     usage
                                exit 1
    esac
    shift
done


masterJvmArgs="-javaagent:${agentPath} -Xbootclasspath/a:${agentPath}"
masterApplicationArgs="--batch.role.udpate-solr-cloud=false --batch.role.check-categorisation-request-messages=false --batch.role.categorise-all=true --batch.role.categorise-all.supervisor=true --batch.role.categorise-all.slave=false --server.port=0"
slaveJvmArgs="-javaagent:${agentPath} -Xbootclasspath/a:${agentPath} -Dakka.remote.netty.tcp.port=0"
slaveApplicationArgs="--batch.role.udpate-solr-cloud=false --batch.role.check-categorisation-request-messages=false --batch.role.categorise-all=true --batch.role.categorise-all.supervisor=false --batch.role.categorise-all.slave=true --server.port=0"
dailyUpdatesJvmArgs=
dailyUpdatesApplicationArgs="--batch.role.udpate-solr-cloud=true --batch.role.check-categorisation-request-messages=true --server.port=0"
updateSolrJvmArgs=
updateSolrApplicationArgs="--batch.role.udpate-solr-cloud=true --server.port=0"


runApplication()
{
	batchTypeBasedJvmArgs=
	batchTypeBasedApplicationArgs=
	case $batchType in
	        master )     
				batchTypeBasedJvmArgs=$masterJvmArgs
				batchTypeBasedApplicationArgs=$masterApplicationArgs
	            ;;
	        slave )    
				batchTypeBasedJvmArgs=$slaveJvmArgs
				batchTypeBasedApplicationArgs=$slaveApplicationArgs
				
				if [ -n "$afterDocNumber" ]
				then
					batchTypeBasedApplicationArgs=$(echo $batchTypeBasedApplicationArgs "-afterDocNumber="$afterDocNumber)
				fi
				;;
	        dailyUpdates )   
				batchTypeBasedJvmArgs=$dailyUpdatesJvmArgs
				batchTypeBasedApplicationArgs=$dailyUpdatesApplicationArgs
	            ;;
	        updateSolr )   
				batchTypeBasedJvmArgs=$updateSolrJvmArgs
				batchTypeBasedApplicationArgs=$updateSolrApplicationArgs
	            ;;
	esac
	
	
	if [ -z "$logName" ]
	then
		logName=logging.log
	fi
	batchTypeBasedJvmArgs=$(echo $batchTypeBasedJvmArgs "-Dlogfile.name="$logName)
	
	
	jvmArgs=$(echo $batchTypeBasedJvmArgs $inputJvmArgs);
	applicationArgs=$(echo $batchTypeBasedApplicationArgs $inputApplicationArgs);
	
	echo "JVM ARGS: " $jvmArgs
	echo
	echo "APP ARGS: " $applicationArgs 
	echo
	echo java -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs
	java -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs & 
}

case $batchType in
        master )     
			runApplication
			sleep 3
			tail -f ${logsFolder}/batch/$logName
            ;;
        slave )    
			runApplication
			sleep 3
			tail -f ${logsFolder}/batch/$logName
            ;;
        dailyUpdates )   
			runApplication
			sleep 3
			tail -f ${logsFolder}/batch/$logName
            ;;
        updateSolr )   
			runApplication
			sleep 3
			tail -f ${logsFolder}/batch/$logName
            ;;
    	masterSlaveCluster )
    		echo "Running cluster of master and slaves"
    		
    		echo "Starting Master"
    		batchType="master"
			logName="master.log"
    		runApplication
    		echo
    		echo
    		
    		sleep 3
    		for (( slaveNumber=1; slaveNumber<=$numberOfSlaves; slaveNumber++ ))
			do
				echo "Starting Slave$slaveNumber"
    			batchType="slave"
				logName=$(echo "slave"$slaveNumber".log")
    			runApplication
    			echo
    			echo
    			sleep 2
			done
			tail -f ${logsFolder}/batch/master.log
			
    		;;
esac


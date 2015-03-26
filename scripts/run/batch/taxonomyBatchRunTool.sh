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
	echo "	-jp --jProfiler		profile with JProfiler." 
	echo "	-jpt --jProfilerTarget		If batchType=masterSlaveCluster, select which app to profile:"
	echo "									'master' to profile master app"
	echo "									'slave' to profile slave app"
	echo
	echo "	-dndl --doNotDisplayLogs	Do not show logs once the application is started"
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
useJprofiler=false
jProfilerTarget=
doesSlaveStartEpic=true

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
        -jp | --jProfiler )     
								useJprofiler=true
                                ;;
        -jpt | --jProfilerTarget )   shift  
								jProfilerTarget=$1
                                ;;
        -h | --help )           usage
                                exit
                                ;;
		-dndl | --doNotDisplayLogs )
								displayLogs=false
								;;
        * )                     usage
                                exit 1
    esac
    shift
done


masterJvmArgs=
masterApplicationArgs="--batch.role.udpate-solr-cloud=false --batch.role.check-categorisation-request-messages=false --batch.role.categorise-all=true --batch.role.categorise-all.supervisor=true --batch.role.categorise-all.slave=false --server.port=0"

slaveJvmArgs="-Dakka.remote.netty.tcp.port=0"
slaveApplicationArgs="--batch.role.udpate-solr-cloud=false --batch.role.check-categorisation-request-messages=false --batch.role.categorise-all=true --batch.role.categorise-all.supervisor=false --batch.role.categorise-all.slave=true --server.port=0"
slaveStarterExtraApplicationArgs="--batch.categorise-all.startEpic=true"
slaveClassicExtraApplicationArgs="--batch.categorise-all.startEpic=false"

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
					batchTypeBasedApplicationArgs=$(echo $batchTypeBasedApplicationArgs "--batch.categorise-all.afterDocNumber="$afterDocNumber)
				fi
				
				if [  "$doesSlaveStartEpic" = true ]
				then
					batchTypeBasedApplicationArgs=$(echo $batchTypeBasedApplicationArgs $slaveStarterExtraApplicationArgs);
				else
					batchTypeBasedApplicationArgs=$(echo $batchTypeBasedApplicationArgs $slaveClassicExtraApplicationArgs);
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
	
	if [ "$useJprofiler" = true ]
	then
		if [[ -z "$jProfilerTarget"  || ( -n "$jProfilerTarget"  &&  "$jProfilerTarget" == "$batchType" ) ]]
		then
			echo "using JPROFILER"
			batchTypeBasedJvmArgs=$(echo $batchTypeBasedJvmArgs -Dcom.sun.management.jmxremote -agentpath:/opt/jprofiler8/bin/linux-x64/libjprofilerti.so=port=8080)				
		fi
	fi
	
	
	jvmArgs=$(echo $batchTypeBasedJvmArgs $inputJvmArgs);
	applicationArgs=$(echo $batchTypeBasedApplicationArgs $inputApplicationArgs);
	
	echo "JVM ARGS: " $jvmArgs
	echo
	echo "APP ARGS: " $applicationArgs 
	echo
	echo nohup java -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs 2>> /dev/null >> /dev/null & 
	nohup java -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs 2>> /dev/null >> /dev/null & 
}

case $batchType in
        master )     
			runApplication
			if [ "$displayLogs" = true ]
			then
				sleep 3
				tail -f ${logsFolder}/batch/$logName
			fi
            ;;
        slave )    
			runApplication
			if [ "$displayLogs" = true ]
			then
				sleep 3
				tail -f ${logsFolder}/batch/$logName
			fi
            ;;
        dailyUpdates )   
			runApplication
			if [ "$displayLogs" = true ]
			then
				sleep 3
				tail -f ${logsFolder}/batch/$logName
			fi
            ;;
        updateSolr )   
			runApplication
			sleep 3
			if [ "$displayLogs" = true ]
			then
				sleep 3
				tail -f ${logsFolder}/batch/$logName
			fi
            ;;
    	masterSlaveCluster )
    	
	    	if [ -z "$numberOfSlaves" ]
			then
				echo "numberOfSlaves missing"
				exit
			fi
    	
    		echo "Running cluster of master and slaves"
    		
    		echo "Starting Master"
    		batchType="master"
			logName="master.log"
    		runApplication
    		echo
    		echo
    		
    		sleep 5
    		for (( slaveNumber=1; slaveNumber<=$numberOfSlaves; slaveNumber++ ))
			do
				echo "Starting Slave$slaveNumber"
    			batchType="slave"
				logName=$(echo "slave"$slaveNumber".log")
				
				if [ "$slaveNumber" != "1" ]
				then
					doesSlaveStartEpic=false
					
					if [ -n "$jProfilerTarget" ]
					then
						useJprofiler=""
						jProfilerTarget=""
					fi
				fi
				
    			runApplication
    			echo
    			echo
			done
			
			if [ "$displayLogs" = true ]
			then
    			sleep 2
				tail -f ${logsFolder}/batch/master.log
			fi
    		;;
esac
#!/bin/bash
cd $( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd );
source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-global taxonomy-batch;



usage ()
{
	echo
	echo "NAME taxonomyBatchRunTool - tool to run taxonomy batch application" 
	echo
	echo "DESCRIPTION"
	echo 
	echo "	-bt --batchType <applicationName> 		Run the batch app among the following types:" 
	echo "								'supervisor' : Categorise All - Supervisor	"
	echo "								'worker' : Categorise All - Worker	"
	echo "								'supervisorWorkerCluster' : Categorise All - Supervisor + Cluster of n workers"
	echo "								'remoteWorkerCluster' : Categorise All - Cluster of n workers for remote supervisor"
	echo "								'dailyUpdates' : apply daily Updates	"
	echo "								'updateSolr' : update Solr from Mongo db entries"
	echo
	echo "	-adn --afterDocNumber <logName>		specific to Worker: after doc number"
	echo "								to restart categorisation after services were stopped"
	echo
	echo "	-ns --numberOfWorkers <logName>		Specific to SupervisorWorkerCluster: number of workers instantiated"
	echo
	echo "	-ln --logName <logName>		provide log name"
	echo
	echo "	-ja --jvmArgs <jvmArgs>		provide jvm arguments"
	echo
	echo "	-aa --applicationArgs <application args>	provide application arguments"
	echo
	echo "	-jp --jProfiler		profile with JProfiler." 
	echo "	-jpt --jProfilerTarget	<jProfilerTarget>	If --jProfiler AND batchType=supervisorWorkerCluster, select which app to profile:"
	echo "									'supervisor' to profile supervisor app"
	echo "									'worker' to profile worker app"
	echo
	echo "	-dndl --doNotDisplayLogs	Do not show logs once the application is started"
	echo "	-ric --runInConsole			Run the application in console (do not use nohup to run it async)"
	echo "	-h --help			display help"
	echo 
	exit
}

inputApplicationArgs=
inputJvmArgs=
batchType=
afterDocNumber=
numberOfWorkers=
logName=
useJprofiler=false
jProfilerTarget=
doesWorkerStartEpic=true
displayLogs=true
runInConsole=false

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
        -ns | --numberOfWorkers )     shift
								numberOfWorkers=$1
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
		-ric | --runInConsole )
								runInConsole=true
								;;
        * )                     usage
                                exit 1
    esac
    shift
done


supervisorJvmArgs=
supervisorApplicationArgs="--batch.role.categorise-all=true --batch.role.categorise-all.supervisor=true --server.port=0"

workerJvmArgs="-Dakka.remote.netty.tcp.port=0"
workerApplicationArgs="--batch.role.categorise-all=true --batch.role.categorise-all.worker=true --server.port=0"
workerStarterExtraApplicationArgs="--batch.categorise-all.startEpic=true"
workerClassicExtraApplicationArgs="--batch.categorise-all.startEpic=false"

dailyUpdatesJvmArgs=
dailyUpdatesApplicationArgs="--batch.role.check-categorisation-request-messages=true --server.port=0"

updateSolrJvmArgs=
updateSolrApplicationArgs="--batch.role.udpate-solr-cloud=true --server.port=0"


runApplication()
{
	batchTypeBasedJvmArgs=
	batchTypeBasedApplicationArgs=
	case $batchType in
	        supervisor )     
				batchTypeBasedJvmArgs=$supervisorJvmArgs
				batchTypeBasedApplicationArgs=$supervisorApplicationArgs
	            ;;
	        worker )    
				batchTypeBasedJvmArgs=$workerJvmArgs
				batchTypeBasedApplicationArgs=$workerApplicationArgs
				
				if [ -n "$afterDocNumber" ]
				then
					batchTypeBasedApplicationArgs=$(echo $batchTypeBasedApplicationArgs "--batch.categorise-all.afterDocNumber="$afterDocNumber)
				fi
				
				if [  "$doesWorkerStartEpic" = true ]
				then
					batchTypeBasedApplicationArgs=$(echo $batchTypeBasedApplicationArgs $workerStarterExtraApplicationArgs);
				else
					batchTypeBasedApplicationArgs=$(echo $batchTypeBasedApplicationArgs $workerClassicExtraApplicationArgs);
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
	touch ${logsFolder}/batch/$logName;
	
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


	if [ "$runInConsole" = true ]
	then
		$javaBinary -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs
	else
		echo "nohup $javaBinary -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs 2>> /dev/null >> /dev/null &" 
		nohup $javaBinary -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs 2>> /dev/null >> /dev/null &
		#$javaBinary -jar -Dspring.profiles.active=${profile},batch $jvmArgs ${batchPackageFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar $applicationArgs 	
	fi
}

case $batchType in
        supervisor )     
			if [ -z "$logName" ]
			then
				logName=supervisor.log
			fi
			runApplication
			if [ "$displayLogs" = true ]
			then
				sleep 3
				tail -f ${logsFolder}/batch/$logName
			fi
            ;;
        worker )    
			if [ -z "$logName" ]
			then
				logName=worker.log
			fi
			runApplication
			if [ "$displayLogs" = true ]
			then
				sleep 3
				tail -f ${logsFolder}/batch/$logName
			fi
            ;;
        dailyUpdates )  
			if [ -z "$logName" ]
			then
				logName=dailyUpdates.log
			fi 
			runApplication
			if [ "$displayLogs" = true ]
			then
				sleep 3
				tail -f ${logsFolder}/batch/$logName
			fi
            ;;
        updateSolr )   
			if [ -z "$logName" ]
			then
				logName=updateSolr.log
			fi 
			runApplication
			sleep 3
			if [ "$displayLogs" = true ]
			then
				sleep 3
				tail -f ${logsFolder}/batch/$logName
			fi
            ;;
    	supervisorWorkerCluster )
    	
	    	if [ -z "$numberOfWorkers" ]
			then
				echo "numberOfWorkers missing"
				exit
			fi
    	
    		echo "Running cluster of supervisor and workers"
    		
    		echo "Starting Supervisor"
    		batchType="supervisor"
			logName="supervisor.log"
    		runApplication
    		echo
    		echo
    		
    		sleep 6
    		for (( workerNumber=1; workerNumber<=$numberOfWorkers; workerNumber++ ))
			do
				echo "Starting Worker$workerNumber"
    			batchType="worker"
				logName=$(echo "worker"$workerNumber".log")
				
				if [ "$workerNumber" != "1" ]
				then
					doesWorkerStartEpic=false
					
					if [ -n "$jProfilerTarget" ]
					then
						useJprofiler=""
						jProfilerTarget=""
					fi
				fi
				
    			sleep 1
    			runApplication
    			echo
    			echo
			done
			
			if [ "$displayLogs" = true ]
			then
    			sleep 2
				tail -f ${logsFolder}/batch/supervisor.log
			fi
    		;;
    	remoteWorkerCluster )
    		source ../../conf/environmentVariables/exportEnvVar.sh taxonomy-batch-remote-worker;
	    	if [ -z "$numberOfWorkers" ]
			then
				echo "numberOfWorkers missing"
				exit
			fi
    	
    		echo "Running cluster of workers for remote supervisor"
    		
    		for (( workerNumber=1; workerNumber<=$numberOfWorkers; workerNumber++ ))
			do
				echo "Starting Worker$workerNumber"
    			batchType="worker"
				logName=$(echo "worker"$workerNumber".log")
				
				if [ "$workerNumber" != "1" ]
				then
					doesWorkerStartEpic=false
					
					if [ -n "$jProfilerTarget" ]
					then
						useJprofiler=""
						jProfilerTarget=""
					fi
				fi
				
    			sleep 1
    			runApplication
    			echo
    			echo
			done
			
			if [ "$displayLogs" = true ]
			then
    			sleep 2
				tail -f ${logsFolder}/batch/worker1.log
			fi
    		;;
esac
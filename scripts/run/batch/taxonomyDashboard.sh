#!/bin/bash
cd "$(dirname "$0")"

usage ()
{
	echo
	echo "NAME taxonomyDashboard - tool to track taxonomy batch application" 
	echo
	echo "DESCRIPTION"
	echo 
	echo "	-ds --dateStarted		Date of the start of the process" 
	echo 
	echo "	-ts --timeStarted		Time of the start of the process" 
	echo 
	echo "	-nb --nbOfDocsAtStart		Number of documents to categorise when the process was started" 
	echo 
	echo "	-tbr --timeBetweenRefresh		Time between refresh" 
	echo 
	exit
}

if [ -z "$1" ]
then
	usage
    exit
fi

while [ "$1" != "" ]; do
    case $1 in
        -ds | --dateStarted )   shift
        						dateStarted=$1
                                ;;
        -ts | --timeStarted ) 	shift
        						timeStarted=$1
                                ;;
        -nb | --NbOfDocsAtStart ) shift
        						nbOfDocsAtStart=$1
                                ;;
        -tbr | --timeBetweenRefresh ) shift
        						timeBetweenRefresh=$1
                                ;;
        * )                     usage
                                exit 1
    esac
    shift
done


clear;

fullStartDate=$(echo ${dateStarted}"T"${timeStarted})
cmd=$(echo 'db.iaViewUpdates.count({creationDate:{$gte:new ISODate("'${fullStartDate}'")}})');

currentNbOfDocsSnapshot=
currentTimeSnapshot=

lastNbOfDocsSnapshot=
lastTimeSnasphot=
listOfMeasures=

if [ -z "$timeBetweenRefresh" ]
then
	timeBetweenRefresh=10
fi

while true
do
	currentTimeSnapshot=$(date +%s)
	currentNbOfDocsSnapshot=$(mongo taxonomy -eval "$cmd" | tail -1)
	
	clear
	echo "Categorisation started on " $fullStartDate
	echo "Current time: " $(date)
	echo "refreshing every $timeBetweenRefresh seconds"
	
	if [[ $nbOfDocsCategorised != 0 ]]
	then
		echo "Nb of docs categorised: " $currentNbOfDocsSnapshot " (available in Mongo db)"
		echo
	fi
	
	if [ -n "$lastNbOfDocsSnapshot" ]
	then
		if [[ $(($currentNbOfDocsSnapshot - $lastNbOfDocsSnapshot)) != 0 ]];
		then
			#### CURRENT AVG SPEED
			avgCatSpeed=$(bc <<< "scale = 10; ( 1000 * ($currentTimeSnapshot - $lastTimeSnasphot) / ($currentNbOfDocsSnapshot - $lastNbOfDocsSnapshot) )")
			echo "Average Categorisation Speed (ms/doc): " $avgCatSpeed
			echo
			listOfMeasures=$(echo $avgCatSpeed " ; " $listOfMeasures | cut -d ";" -f -40 )
			####
			
			
			#### ESTIMATED TIME LEFT
			if [ -n "$nbOfDocsAtStart" ]
			then
				timeLeft=$(bc <<< "scale = 10; ($nbOfDocsAtStart-$currentNbOfDocsSnapshot)*$avgCatSpeed/(1000*3600*24)")
				echo "Estimated time left: " $timeLeft " days"
			fi
			#######	
		fi
	fi
	
	if [ -n "$listOfMeasures" ]
	then
		echo "Last Measures of the avg cat speed: " $listOfMeasures 
	fi
	
	sleep $timeBetweenRefresh
	lastNbOfDocsSnapshot=$currentNbOfDocsSnapshot;
	lastTimeSnasphot=$currentTimeSnapshot;
done


function_bin (){
	
	lastNbOfDocsForCustomTimer=
	listOfCustomMeasures=
	timerLengthInSeconds=50
	startTime=
	
	
	#### AVG SPEED ON THE LAST 60 Seconds
	if [ -z "$startTime" ]
	then
		startTime=`date +%S`
		lastNbOfDocsForCustomTimer=$currentNbOfDocsSnapshot
	fi
	
	
	function_timer $timerLengthInSeconds $startTime
	timerReturnedValue=$?
	if [ "$timerReturnedValue" == 1 ]
	then
		listOfCustomMeasures=$(( 1000 * $timerLengthInSeconds / ($currentNbOfDocsSnapshot - $lastNbOfDocsForCustomTimer) ))
		listOfCustomMeasures=$(echo $listOfCustomMeasures " ; " $listOfCustomMeasures  )
		
		startTime=`date +%S`
		lastNbOfDocsForCustomTimer=$currentNbOfDocsSnapshot
	fi
	#######
	
	
	
	
	
	if [ -n "$listOfCustomMeasures" ]
	then
		echo "Last Measures of the avg cat speed (every " $timerLengthInSeconds " secs): " $listOfCustomMeasures
	fi
}




function_timer () {
	timerLengthInSeconds=$1
	startTime=$2
	
	currentTimeSnapshot=`date +%S`
	spentTime=`expr $currentTimeSnapshot - $startTime`
	
	##Handle when startTime between 50 and 59 secs
	if [ "$spentTime" -lt "0" ]; 
	then
		spentTime=$(($spentTime + 60))
	fi
	
	if [ "$spentTime" -lt "$timerLengthInSeconds" ]; 
	then
		return "0"
	else
		return "1"
	fi
}



# db.iaViews.aggregate([{$match:{}},{$unwind:"$categories"},{$group:{_id:1,totalNbOfFoundCategories:{$sum:1}}}])
# { "_id" : 1, "totalNbOfFoundCategories" : 53863016 }

# db.iaViews.count({categories:{$size:0}})
# 779627

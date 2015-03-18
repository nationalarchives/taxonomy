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
	echo "	-nb --NbOfDocsAtStart		Number of documents to categorise when the process was started" 
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
        						
                                ;;
        * )                     usage
                                exit 1
    esac
    shift
done




clear;

fullStartDate=$(echo ${dateStarted}"T"${timeStarted})
cmd=$(echo 'db.iaViewUpdates.count({creationDate:{$gte:new ISODate("'${fullStartDate}'")}})');
lastNbOfDocs=
currentNbOfDocs=

listOfMeasures=

timeBetweenRefresh=5

while true
do
	currentNbOfDocs=$(mongo taxonomy -eval "$cmd" | tail -1)
	
	clear
	echo "Categorisation started on " $fullStartDate
	echo "refreshing every $timeBetweenRefresh seconds"
	
	if [[ $nbOfDocsCategorised != 0 ]]
	then
		echo "Nb of docs categorised: " $currentNbOfDocs " (available in Mongo db)"
		echo
	fi
	
	if [ -n "$lastNbOfDocs" ]
	then
		if [[ $(($currentNbOfDocs - $lastNbOfDocs)) != 0 ]];
		then
			avgCatSpeed=$(( 1000 * $timeBetweenRefresh / ($currentNbOfDocs - $lastNbOfDocs) ))
			echo "Average Categorisation Speed (ms/doc): " $avgCatSpeed
			echo
			listOfMeasures=$(echo $avgCatSpeed " ; " $listOfMeasures  )
		fi
	fi
	
	if [ -n "$listOfMeasures" ]
	then
		echo "Last Measures of the avg cat speed: " $listOfMeasures
	fi
	
	
	sleep $timeBetweenRefresh
	lastNbOfDocs=$currentNbOfDocs;
done


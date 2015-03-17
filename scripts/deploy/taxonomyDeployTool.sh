#!/bin/bash
cd "$(dirname "$0")"



usage ()
{
	echo
	echo "NAME taxonomyDeployTool - tool to package taxonomy, deploy it, deploy its scripts" 
	echo
	echo "DESCRIPTION"
	echo 
	echo "	-p --package			run mvn goal to package locally the application"
	echo "	-s --skipTest			(optional) skip tests while packaging"
	echo
	echo "	-cb --createBackup			create backup of currently deployed packages (BEFORE deployment)"
	echo "	-bfn --backupFolderName <folderName>			(optional) set name for backup folder"
	echo
	echo "	-d --deploy			deploy to external platform"
	echo "	-ds --deployScripts			deploy scripts to external platform"
	echo "	-pl --platform <platform>	target platform (local, tooling, test)"
	echo
	echo "	-h --help			display help"
	echo 
	exit
}



platform=
skipTest=
createBackup=
backupFolderName=
package=
deploy=
deployScripts=

# Tutorial on shell script with list of operators for if, while, etc statements:
# http://linuxcommand.org/lc3_wss0080.php
if [ -z "$1" ]
then
	usage
    exit
fi
while [ "$1" != "" ]; do
    case $1 in
        -p | --package )     
        					package=1
                                ;;
        -s | --skipTest )    skipTest=1
                                ;;
        -d | --deploy )    deploy=1
                                ;;
        -ds | --deployScripts )    deployScripts=1
                                ;;
        -pl | --platform )      shift
        						platform=$1
                                ;;
        -cb | --createBackup )    createBackup=1
                                ;;
        -bfn | --backupFolderName )      shift
        						backupFolderName=$1
                                ;;
        -h | --help )           usage
                                exit
                                ;;
        * )                     usage
                                exit 1
    esac
    shift
done

function_package () {
	skipTest=$1;
		if [ -n "$skipTest" ]
		then
			echo "PACKAGE - skip Test"
			mvn clean compile package -DskipTests=true -f ../../ || exit
		else
			echo "PACKAGE - do not skip test"
			mvn clean compile package -f ../../ || exit
		fi
}

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

function_deployTaxonomyPackages () {
	platform=$1;
	case $platform in
	        local )     
	        	echo DEPLOYING to LOCAL 
	            ;;
	        test )      
	        	echo DEPLOYING to TEST
	            ;;
	        tooling )   
	        	echo DEPLOYING to TOOLING
	            ;;
	esac
	
	function_loadVars $platform
	
	scp  /home/jcharlet/_workspace/cat/taxonomy-cli/target/taxonomy-cli-0.0.1-SNAPSHOT.jar ${USER}@${server}:$tmpFolder
	scp  /home/jcharlet/_workspace/cat/taxonomy-ws/target/taxonomy-ws-0.0.1-SNAPSHOT.war ${USER}@${server}:$tmpFolder
	scp  /home/jcharlet/_workspace/cat/taxonomy-batch/target/taxonomy-batch-0.0.1-SNAPSHOT.jar ${USER}@${server}:$tmpFolder
	
	ssh -t ${server} sudo mv ${tmpFolder}/taxonomy-cli-0.0.1-SNAPSHOT.jar ${cliPackageFolder}/
	ssh -t ${server} sudo mv ${tmpFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war ${wsPackageFolder}/
	ssh -t ${server} sudo mv ${tmpFolder}/taxonomy-batch-0.0.1-SNAPSHOT.jar ${batchPackageFolder}/
	
	# Deploy Spring agent dedicated to aspect weaving for the batch application
	scp  /home/jcharlet/.m2/repository/org/springframework/spring-instrument/4.0.7.RELEASE/spring-instrument-4.0.7.RELEASE.jar ${USER}@${server}:$tmpFolder
	ssh -t ${server} sudo mv ${tmpFolder}/spring-instrument-4.0.7.RELEASE.jar ${batchPackageFolder}/
	
	ssh ${server} rm -rf $tmpFolder/taxonomy-*
	
	
}


function_deployScripts () {
	platform=$1;
	function_loadVars $platform
	
	ssh -t ${server} rm -rf ${scriptsFolder}/*
	
	scp -r /home/jcharlet/_workspace/cat/scripts/run ${USER}@${server}:$tmpFolder/
	scp -r /home/jcharlet/_workspace/cat/scripts/conf ${USER}@${server}:$tmpFolder/
	
	
	ssh -t ${server} sudo mv ${tmpFolder}/run ${scriptsFolder}/
	ssh -t ${server} sudo mv ${tmpFolder}/conf ${scriptsFolder}/

ssh -t ${server} sudo mv ${scriptsFolder}/conf/conf-${platform}.sh ${scriptsFolder}/conf/conf.sh
ssh -t ${server} sudo rm ${scriptsFolder}/conf/conf-*
}

function_backupTaxonomyPackages () {
	backupFolderName=$1;
	platform=$2;
	
	function_loadVars $platform
	
	if [ -z "$backupFolderName" ]
	then
		backupFolderName=$(date +%Y%m%d%H%M%S);	
	fi
	
	ssh -t ${server} sudo mkdir ${wsPackageFolder}/backups/${backupFolderName}
	ssh -t ${server} sudo cp ${wsPackageFolder}/* ${wsPackageFolder}/backups/${backupFolderName}/
	
}

if [ -n "$package" ]
then
	function_package "$skipTest"
fi

if [ -n "$createBackup" ]
then
	function_backupTaxonomyPackages "$backupFolderName" $platform
fi

if [[ -n "$deploy" &&  "$platform" != local ]]
then 
	function_loadVars $platform
	
	function_deployTaxonomyPackages $platform
fi


if [[ -n "$deployScripts" &&  "$platform" != local ]]
then 
	function_loadVars $platform
	
	function_deployScripts $platform
fi


#backup:

#diff with server
#rsync -arnv --itemize-changes /home/jcharlet/_workspace/cat/scripts/run/* jcharlet@***REMOVED***:/home/jcharlet/apps/taxonomy/scripts/

#diff with ssh
#diff ws/stopWS.sh <( ssh jcharlet@***REMOVED*** 'cat /home/jcharlet/apps/taxonomy/scripts/ws/stopWS.sh')

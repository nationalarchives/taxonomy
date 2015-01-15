source /home/jcharlet/_workspace/cat/scripts/run/ws/stopWS.sh;
cd /home/jcharlet/_workspace/cat;
mvn clean compile package || exit #-Dmaven.test.skip=true;
scp /home/jcharlet/_workspace/cat/cat-cli/target/taxonomy-cli-0.0.1-SNAPSHOT.jar ***REMOVED***:${cliPackageFolder}
scp /home/jcharlet/_workspace/cat/cat-ws/target/taxonomy-ws-0.0.1-SNAPSHOT.war ***REMOVED***:${wsPackageFolder}

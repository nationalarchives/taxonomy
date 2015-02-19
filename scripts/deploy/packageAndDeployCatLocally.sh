source /home/jcharlet/_workspace/cat/scripts/run/ws/stopWS.sh
cd /home/jcharlet/_workspace/cat;
mvn clean compile package || exit #-Dmaven.test.skip=true;

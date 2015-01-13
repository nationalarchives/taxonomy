cd /home/jcharlet/_workspace/cat;mvn clean compile package -Dmaven.test.skip=true || exit #-Dmaven.test.skip=true;
scp /home/jcharlet/_workspace/cat/cat-cli/target/taxonomy-cli-0.0.1-SNAPSHOT.jar ***REMOVED***:/home/jcharlet/apps/taxonomy/cli
scp /home/jcharlet/_workspace/cat/cat-ws/target/taxonomy-ws-0.0.1-SNAPSHOT.war ***REMOVED***:/home/jcharlet/apps/taxonomy/ws

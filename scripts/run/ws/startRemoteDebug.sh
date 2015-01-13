source ../load-vars.sh;
ps aux | grep taxonomy | awk "{print \$2}" |  xargs kill;
#jar -uvf taxonomy-ws-0.0.1-SNAPSHOT.war WEB-INF
java -jar -Dspring.profiles.active=${profile} -Dlucene.categoriser.useTSetBasedCategoriser=true -Dlucene.categoriser.useQueryBasedCategoriser=false -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=9003 ${wsPackageFolder}/taxonomy-ws-0.0.1-SNAPSHOT.war 

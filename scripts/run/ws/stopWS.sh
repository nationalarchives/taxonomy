source ../load-vars.sh;
ps aux | grep taxonomy | awk "{print \$2}"  | xargs kill

curl http://localhost:8983/solr/IAView_subset/update/json -H 'Content-type:application/json' --noproxy localhost -d '
{"delete":{"query":"NOT SERIES:\"AIR 37\""}, "commit":{}},
"optimize": { "waitFlush":false, "waitSearcher":false }}'

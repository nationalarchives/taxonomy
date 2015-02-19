rm /mnt/search_indexes/index_20150217/core.properties;
rm /mnt/search_indexes/index_20150217_subset2/core.properties;



curl 'http://localhost:8983/solr/admin/cores?schema=schema.xml&shard=&instanceDir=/mnt/search_indexes/index_20150217&indexInfo=false&name=index_20150217&action=CREATE&config=solrconfig.xml&_=1424281085379&collection=&dataDir=/mnt/search_indexes/index_20150217/data&wt=json' --noproxy localhost
curl 'http://localhost:8983/solr/admin/cores?schema=schema.xml&shard=&instanceDir=/mnt/search_indexes/index_20150217_subset2&indexInfo=false&name=IAView_subset&action=CREATE&config=solrconfig.xml&_=1424281085379&collection=&dataDir=/mnt/search_indexes/index_20150217_subset2/data&wt=json' --noproxy localhost

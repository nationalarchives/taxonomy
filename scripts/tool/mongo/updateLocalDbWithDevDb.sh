mongoexport -h ***REMOVED***.***REMOVED***:27017 --db taxonomy --collection categories --out /home/jcharlet/Documents/projects/taxonomy/dumps/mongo/categories.json --journal
mongo taxonomy --eval 'printjson(db.categories.remove({}))'
mongoimport --db taxonomy --collection categories --type json --file /home/jcharlet/Documents/projects/taxonomy/dumps/mongo/categories.json

mongo ***REMOVED***.***REMOVED***:27017/taxonomy --eval 'printjson(db.categories.ensureIndex({CIAID:1}))'
mongo ***REMOVED***.***REMOVED***:27017/taxonomy --eval 'printjson(db.trainingset.ensureIndex({CATEGORY:1}))'
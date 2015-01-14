source ../load-vars.sh;
mongo ${mongoDbHostName}:27017/taxonomy --eval 'cursor=db.evaluationReports.aggregate([ {$sort:{_id:-1}}, {$limit:1}, {$unwind:"$results"}, {$project:{category:"$results.category",accuracy:"$results.accuracy",recall:"$results.recall",foundInTDocCat:"$results.foundInTDocCat",foundInTDocLegacyCat:"$results.foundInTDocLegacyCat", foundInCatRepo:"$results.foundInCatRepo",tp:"$results.tp",fp:"$results.fp",fn:"$results.fn", _id:0}}, {$sort:{category:1}}]);
print("category;accuracy;recall;tp;fp;fn");while(cursor.hasNext()){doc=cursor.next(); print(doc.category + ";" + doc.accuracy + ";" + doc.recall + ";" + doc.tp + ";" + doc.fp + ";" + doc.fn)};'

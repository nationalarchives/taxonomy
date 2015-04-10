#!/bin/bash
cd "$(dirname "$0")"

source ../../conf/exportEnvVar.sh taxonomy-global;
mongo ${mongoDbHostName}:27017/taxonomy --eval 'cursor=db.evaluationReports.aggregate([ {$sort:{_id:-1}}, {$limit:1}, {$unwind:"$results"}, {$project:{category:"$results.category",accuracy:"$results.accuracy",recall:"$results.recall",foundInTDocCat:"$results.foundInTDocCat",foundInTDocLegacyCat:"$results.foundInTDocLegacyCat", foundInCatRepo:"$results.foundInCatRepo",tp:"$results.tp",fp:"$results.fp",fn:"$results.fn", _id:0}}, {$sort:{category:1}}]);

var separator="|";

print("category;accuracy;recall;tp;fp;fn");
while(cursor.hasNext()){
	doc=cursor.next(); 
	print(doc.category + separator + doc.accuracy + separator + doc.recall + separator + doc.tp + separator + doc.fp + separator + doc.fn)
};'

echo;
echo;

mongo ${mongoDbHostName}:27017/taxonomy --eval 'cursor=db.testdocuments.find();

var separator="|";

print("catDocRef;title;categories;legacyCategories");
while(cursor.hasNext()){
	doc=cursor.next();
	print(doc.catDocRef + separator + doc.title + separator + doc.categories + separator + doc.legacyCategories);
};'

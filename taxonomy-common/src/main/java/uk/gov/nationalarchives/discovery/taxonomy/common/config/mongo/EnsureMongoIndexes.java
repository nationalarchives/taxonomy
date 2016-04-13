package uk.gov.nationalarchives.discovery.taxonomy.common.config.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;

/**
 * Created by jcharlet on 3/2/16.
 */
@Component
public class EnsureMongoIndexes implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public EnsureMongoIndexes(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... strings) throws Exception {
        createCollectionIfMissing(IAViewUpdate.class);
        createCollectionIfMissing(MongoInformationAssetView.class);

        mongoTemplate.indexOps(IAViewUpdate.class).ensureIndex(
                new Index().on("creationDate", Sort.Direction.DESC).background()
        );
        mongoTemplate.indexOps(IAViewUpdate.class).ensureIndex(
                new Index().on("creationDate", Sort.Direction.ASC).on("docReference", Sort.Direction.ASC).background()
        );
        mongoTemplate.indexOps(IAViewUpdate.class).ensureIndex(
                new Index().on("docReference", Sort.Direction.ASC).background()
        );
        mongoTemplate.indexOps(MongoInformationAssetView.class).ensureIndex(
                new Index().on("series", Sort.Direction.ASC).on("categories", Sort.Direction
                        .ASC).background()
        );
    }

    private void createCollectionIfMissing(Class clazz) {
        if (!mongoTemplate.collectionExists(clazz)){
            mongoTemplate.createCollection(clazz);
        }
    }


}

package gov.tna.discovery.taxonomy.repository.domain.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import gov.tna.discovery.taxonomy.repository.domain.InformationAssetViewFull;

@Document(collection="trainingset")
public class TrainingDocument extends InformationAssetViewFull {

}

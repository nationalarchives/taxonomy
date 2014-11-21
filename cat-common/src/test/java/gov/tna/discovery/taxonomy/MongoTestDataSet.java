package gov.tna.discovery.taxonomy;

import gov.tna.discovery.taxonomy.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

//@Component
//@ConditionalOnMissingBean(name = "fongo")
public class MongoTestDataSet {

    // @Autowired
    CategoryRepository categoryRepository;

    // @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    public void createCategory() {
	Category category = new Category();
	category.setCiaid("C10032");
	category.setTtl("Disease");
	category.setQry("\"venereal disease\" OR \"tropical disease\" OR \"industrial disease\" OR \"infectious disease\" OR \"bubonic plague\" OR \"yellow fever\" OR \"malaria\" OR \"tuberculosis\" OR \"scurvy\" OR \"rickets\" OR \"measles\" OR \"influenza\" OR \"bronchitis\" OR \"pneumoconiosis\" OR \"emphysema\" OR \"byssinosis\" OR \"polio\" OR \"dengue fever\" OR \"rabies\" OR \"swine fever\" OR \"weils disease\" OR \"cancer\" OR \"asthma\" OR \"syphilis\" OR \"typhoid\" OR \"gonorrhoea\" OR \"smallpox\" OR \"cholera\" OR \"cholera morbus\" OR \"typhus\" OR \"meningitis\" OR \"dysentery\" OR \"scarlatina\" OR \"scarlet fever\" OR \"pneumonia\" OR \"cynanche tonsillaris\" OR \"synocha\" OR \"opthalmia\" OR \"whooping cough\" OR \"HIV\" OR \"asbestosis\" OR \"mesothelioma\" OR \"beri beri\" OR \"multiple sclerosis\" OR \"diabetes\" OR \"leus venerea\" OR \"leprosy\" OR \"poliomyelitis\" OR \"encephalitis\" OR \"Trypanosomiasis\"");
	category.setLck(false);
	category.setSc(0.001f);
	categoryRepository.save(category);
    }

    public void createTrainingSetDocument() {
	TrainingDocument trainingDocument = new TrainingDocument();
	trainingDocument.setDescription("Cloth planning and the clothes ration 1944-1947.");
	trainingDocument.setTitle("Rationing");
	trainingDocumentRepository.save(trainingDocument);

    }
}

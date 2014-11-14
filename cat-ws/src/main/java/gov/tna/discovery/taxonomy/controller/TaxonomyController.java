package gov.tna.discovery.taxonomy.controller;

import gov.tna.discovery.taxonomy.domain.SearchIAViewRequest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.lucene.Searcher;

import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/taxonomy")
public class TaxonomyController {
    
//    private static final Logger logger = LoggerFactory.getLogger(TaxonomyController.class);

    @RequestMapping("/hello")
    String home() {
	return "Hello World!";
    }

    @RequestMapping(value="/search", method = RequestMethod.POST, consumes="application/json", produces="application/json")
    @ResponseBody
    List<InformationAssetView> searchIAView(@RequestBody SearchIAViewRequest searchRequest) throws Exception {
	Searcher searcher = new Searcher();
	if(StringUtils.isEmpty(searchRequest.getCategoryQuery())){
	    throw new ParseException("categoryQuery should be provided and not empty");
	}
	if(searchRequest.getLimit()==null){
	    searchRequest.setLimit(10);
	}
	if(searchRequest.getOffset()==null){
	    searchRequest.setOffset(0);
	}
	return searcher.performSearch(searchRequest.getCategoryQuery(), searchRequest.getScore(), searchRequest.getLimit(), searchRequest.getOffset());
    }
    

    
    @RequestMapping("/publish")
    String publish(Object category) {
	return "{\"status:\"ok ?\"}";
    }
    

    
    @RequestMapping("/test")
    String etst(Object category) {
	return "{\"test:\"ok ?\"}";
    }

}

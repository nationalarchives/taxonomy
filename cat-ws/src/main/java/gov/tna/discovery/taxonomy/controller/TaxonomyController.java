package gov.tna.discovery.taxonomy.controller;

import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class TaxonomyController {


    @RequestMapping("/hello")
    String home() {
        return "Hello World!";
    }
    
    List<String>searchIAView(String categoryQuery, Integer score){
	return null;
    }
    
    
}

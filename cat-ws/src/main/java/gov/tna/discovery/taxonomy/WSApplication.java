package gov.tna.discovery.taxonomy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class WSApplication {

    public static void main(String[] args) throws Exception {
	SpringApplication.run(WSApplication.class, args);
    }

}

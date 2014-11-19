package gov.tna.discovery.taxonomy;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class CLIApplication {

    public static void main(String[] args) throws IOException, ParseException {
	SpringApplication.run(CLIApplication.class, args);

    }

}

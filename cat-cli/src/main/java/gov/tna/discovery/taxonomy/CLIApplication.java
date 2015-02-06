package gov.tna.discovery.taxonomy;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@ComponentScan
@EnableAutoConfiguration
@PropertySource("application.yml")
public class CLIApplication {

    public static void main(String[] args) throws IOException, ParseException {
	SpringApplication.run(CLIApplication.class, args);
    }

}

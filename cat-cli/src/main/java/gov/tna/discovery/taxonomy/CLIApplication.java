package gov.tna.discovery.taxonomy;

import gov.tna.discovery.taxonomy.service.impl.Categoriser;

import java.io.IOException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
public class CLIApplication {

    public static void main(String[] args) throws IOException, ParseException {
	SpringApplication.run(CLIApplication.class, args);

    }

}

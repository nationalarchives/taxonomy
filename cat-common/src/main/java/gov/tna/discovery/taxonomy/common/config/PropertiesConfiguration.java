package gov.tna.discovery.taxonomy.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * To inject property values (from app.yml) with @Value annotation
 * 
 * @author jcharlet
 *
 */
@Configuration
@PropertySource("classpath:application.yml")
public class PropertiesConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	return new PropertySourcesPlaceholderConfigurer();
    }

}

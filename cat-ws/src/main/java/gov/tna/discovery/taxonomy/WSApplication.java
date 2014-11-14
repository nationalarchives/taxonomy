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
    
//    @Bean
//    public EmbeddedServletContainerFactory servletContainer() {
//	JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
//        factory.setPort(8080);
//        factory.setSessionTimeout(10, TimeUnit.MINUTES);
//        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/notfound.html"));
//        return factory;
//    }
    
    
}

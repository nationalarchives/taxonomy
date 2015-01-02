package gov.tna.discovery.taxonomy.ws.config;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

//TODO Log input/output of methods/ws endpoints generically with that class
//@Configuration
//@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {

    public class LogRequestInterceptor implements WebRequestInterceptor {
	private Logger logger = LoggerFactory.getLogger(LogRequestInterceptor.class);

	@Override
	public void preHandle(WebRequest pRrequest) throws Exception {
	    DispatcherServletWebRequest request = (DispatcherServletWebRequest) pRrequest;
	    logger.info("WS > {}", request.getRequest().getRequestURI());
	    for (Entry<String, String[]> parameter : request.getParameterMap().entrySet()) {
		logger.info("Parameter: {} = {}", parameter.getKey(), parameter.getValue());
	    }

	}

	@Override
	public void postHandle(WebRequest pRrequest, ModelMap model) throws Exception {
	    DispatcherServletWebRequest request = (DispatcherServletWebRequest) pRrequest;
	    logger.info("WS > {}", request.getRequest().getRequestURI());
	    for (Entry<String, Object> parameter : model.entrySet()) {
		logger.info("Parameter: {} = {}", parameter.getKey(), parameter.getValue());
	    }
	}

	@Override
	public void afterCompletion(WebRequest request, Exception ex) throws Exception {

	}

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
	WebRequestInterceptor logRequestInterceptor = new LogRequestInterceptor();
	registry.addInterceptor(new WebRequestHandlerInterceptorAdapter(logRequestInterceptor));
    }

}

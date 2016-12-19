package com.ftc.configuration;

/*
  To work without a web.xml file in a modern server that supports Servelet 3, we declare a class 
  that that extends AbstractAnnotationConfigDispatcherServletInitializer. The server detects 
  this class in the class path.
 */
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	// This specifies that class AppConfig configures the application.
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { AppConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

}

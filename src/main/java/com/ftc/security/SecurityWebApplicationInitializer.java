package com.ftc.security;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/*
  Spring detects in the class path a class that extends AbstractSecurityWebApplicationInitializer and inserts
  springSecurityFilterChain .
 */
public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

}

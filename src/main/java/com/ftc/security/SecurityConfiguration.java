package com.ftc.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/*
 * CONFIGURATION OF SPRING SECURITY
 * 
 * The @EnableWebSecurity annotation and WebSecurityConfigurerAdapter work together to provide web based security.
 * Annotation @EnableWebSecurity creates a Servlet Filter known as the springSecurityFilterChain which is
  * responsible for all the security (protecting the application URLs, validating submitted username and passwords,
  * redirecting to the log in form, etc.); Spring detects that there is a class that extends
  * WebSecurityConfigurerAdapter. Other aspects are explained in the code.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  @Qualifier("customUserDetailsService")
  UserDetailsService userDetailsService;

  @Autowired
  PersistentTokenRepository tokenRepository;

  @Autowired
  public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
    auth.authenticationProvider(authenticationProvider());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    /*
    To access / or /list the user must have role USER, ADMIN or DBA

    To access /newuser/** or /delete-user-*" the user must have role ADMIN

    To access /edit-user-* the user must have role ADMIN or DBA

    The application requires /login as login form

    The names of the components for username and password are "ssoId" and "password". The name of the username in the
     databse is ssoId, but I don't know if this is relevant or if another name could be used.

    The application uses the functionality of remember me with a check box named remember-me.

    The login page has a hidden field to protect against Cross-Site Request Forgery.
      */
    http.authorizeRequests().antMatchers("/", "/list").access(
        "hasRole('USER') or hasRole('ADMIN') or hasRole('DBA')").antMatchers("/newuser/**",
        "/delete-user-*").access("hasRole('ADMIN')").antMatchers("/edit-user-*").access(
        "hasRole('ADMIN') or hasRole('DBA')").and().formLogin().loginPage("/login")
        .loginProcessingUrl("/login").usernameParameter("ssoId").passwordParameter("password")
        .and().rememberMe().rememberMeParameter("remember-me").tokenRepository(tokenRepository)
        .tokenValiditySeconds(86400).and().csrf().and().exceptionHandling().accessDeniedPage(
        "/Access_Denied");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    /*
      The authentication provider is set up with a UserDetailsService object (injected above) and a a password encoder.
     */
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());
    return authenticationProvider;
  }

  /*
    Remember-me or persistent-login authentication refers to web sites being able to remember the identity of a
    principal between sessions; if the user chooses the "Remember-me" option in the login page, the application
    will open the next page the next time the user attempts to open the login page in the browser. this method is
    called by SPRING at the initiation of the application to get a PersistentTokenBasedRememberMeServices object.
    The application uses table "persistent_logins" in the database.  This application has a persistent object
    PersistentLogin in the com.ftc.model package with the other persistent objects.
   */
  @Bean
  public PersistentTokenBasedRememberMeServices getPersistentTokenBasedRememberMeServices() {
    PersistentTokenBasedRememberMeServices tokenBasedservice = new PersistentTokenBasedRememberMeServices(
        "remember-me", userDetailsService, tokenRepository);
    return tokenBasedservice;
  }

  @Bean
  public AuthenticationTrustResolver getAuthenticationTrustResolver() {
    return new AuthenticationTrustResolverImpl();
  }

}

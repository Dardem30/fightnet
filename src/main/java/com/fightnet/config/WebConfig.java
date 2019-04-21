package com.fightnet.config;

import com.fightnet.interceptor.AccessControlHeaderInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * Configuration for interceptors.
 */
@Configuration
@EnableScheduling
public class WebConfig extends WebMvcConfigurationSupport {

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new AccessControlHeaderInterceptor());
    }
}

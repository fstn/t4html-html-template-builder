package com.fstn.t4html;

import com.fstn.t4html.service.BlocksService;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.inject.Named;
import javax.ws.rs.ApplicationPath;

/**
 * Created by stephen on 18/03/2016.
*/
@SpringBootApplication
public class Application {

    @Named
    @ApplicationPath("/rest")
    public static class JerseyConfig extends ResourceConfig {

        public JerseyConfig() {
            this.register(BlocksService.class);

        }
    }

    @Bean
    public FilterRegistrationBean contextFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        AccessControlAllowOriginFilter contextFilter = new AccessControlAllowOriginFilter();
        registrationBean.setFilter(contextFilter);
        registrationBean.setOrder(1);
        return registrationBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
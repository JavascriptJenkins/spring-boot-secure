package springbootapi.spring.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import springbootapi.restapi.controllers.SecurityInterceptor


@Configuration
    class WebMvcConfig extends WebMvcConfigurerAdapter{
        @Override
        void addInterceptors(InterceptorRegistry registry){
            registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/**");
        }
    }

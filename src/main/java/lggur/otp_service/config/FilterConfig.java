package lggur.otp_service.config;

import lggur.otp_service.service.JwtService;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<@NotNull JwtFilter> jwtFilter(JwtService jwtService) {

        FilterRegistrationBean<@NotNull JwtFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtFilter(jwtService));
        bean.addUrlPatterns("/*");

        return bean;
    }
}
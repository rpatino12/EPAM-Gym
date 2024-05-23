package com.rpatino12.epam.gym.config;

//import feign.RequestInterceptor;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

//@Configuration
public class FeignConfig {
    /**
     * Feign is a declarative web service client, which makes writing HTTP clients easier.
     * To pass the JWT token from this main microservice to any secondary microservice using Feign,
     * you can add an interceptor that will add the JWT token to the request headers.
     */

//    @Autowired
//    private HttpServletRequest request;
//
//    @Bean
//    public RequestInterceptor requestInterceptor() {
//        return requestTemplate -> {
//            String authorizationHeader = request.getHeader("Authorization");
//            if (authorizationHeader != null) {
//                requestTemplate.header("Authorization", authorizationHeader);
//            }
//        };
//    }
}

//package cz.incad.nkp.inprove.permonikapi.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private static final String[] SWAGGER_WHITELIST = {
//            "swagger-ui/**",
//            "v3/api-docs/**",
//            "swagger-resources/**",
//            "swagger-resources"
//    };
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeRequests()
//                .antMatchers(SWAGGER_WHITELIST).permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .httpBasic();
//
//        return http.build();
//    }
//}

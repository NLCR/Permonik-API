package cz.incad.nkp.inprove.permonikapi.config.security.auth;

import cz.incad.nkp.inprove.permonikapi.config.security.user.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity
@Order(2)
public class PermSecurityConfiguration {

    private UserDetailsServiceImpl userDetailsService;
    private PasswordEncoder passwordEncoder;


//    @Value("${spring.profiles.active}")
//    private String activeProfile;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //TODO: zjistit, zda funguje
//        if (!isDev(activeProfile)) {
//            http.authorizeRequests().antMatchers("/api/swagger-ui/**").hasAnyRole("ADMIN");
//        }

        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("swagger-ui/**").permitAll()
                        .requestMatchers("v3/api-docs/**").permitAll()
                        .requestMatchers("swagger-resources/**").permitAll()
                        .requestMatchers("swagger-resources").permitAll()
                        .requestMatchers("api/auth/login/shibboleth").permitAll()
                        .requestMatchers("api/auth/login/basic").permitAll()
                        .requestMatchers("api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/me").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/metatitle/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "api/metatitle/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "api/metatitle/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "api/mutation/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "api/mutation/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "api/mutation/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "api/owner/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "api/owner/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "api/owner/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "api/publication/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "api/publication/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "api/publication/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "api/specimen/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/specimen/**").permitAll()
                        .requestMatchers("api/user/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "api/volume/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "api/volume/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "api/volume/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "api/volume/**").authenticated()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().denyAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        return http.build();


    }

    //    @Bean
//    public BasicAuthenticationFilter basicAuthenticationFilter(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return new BasicAuthenticationFilter(authenticationConfiguration.getAuthenticationManager());
//    }
//
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }


}

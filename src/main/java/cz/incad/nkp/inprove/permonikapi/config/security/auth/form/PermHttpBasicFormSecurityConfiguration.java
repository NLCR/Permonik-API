package cz.incad.nkp.inprove.permonikapi.config.security.auth.form;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@AllArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
@Order(1)
public class PermHttpBasicFormSecurityConfiguration {

    private PermFormAuthenticationProvider authenticationProvider;

    @Autowired
    public void configureGlobal(PermFormAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain httpBasicFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authz) -> authz
                .requestMatchers("api/auth/login/basic").permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
        ).csrf(AbstractHttpConfigurer::disable).httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider);
        return authenticationManagerBuilder.build();
    }

}

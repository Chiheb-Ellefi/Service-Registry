package org.delivery.system.serviceregistry.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Value(value = "${spring.security.user.name}")
    private String username;
    @Value(value = "${spring.security.user.password}")
    private String password;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(Customizer.withDefaults()).authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.anyRequest().authenticated();
        });
        return http.build();
    }

    @Bean
   public  UserDetailsManager userDetailsManager() {
        UserDetails user=User.withUsername(username).password(password).roles("ACTUATOR","ADMIN").build();
        return new InMemoryUserDetailsManager(user);
    }
    @Bean
   public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

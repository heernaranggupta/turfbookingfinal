package com.Turfbooking.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfing extends WebSecurityConfigurerAdapter {

    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public WebSecurityConfing(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.applyPermitDefaultValues();
        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin",
                "Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin",
                "Cache-Control", "Content-Type", "Authorization"));
        configuration.addAllowedOrigin("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/user/sign-up").permitAll()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/cart/guest").permitAll()
                .antMatchers("/user/cart/guest/remove").permitAll()
                .antMatchers("/user/get-all-slots-by-date").permitAll()
                .antMatchers("/user/mobile/get-all-slots-by-date").permitAll()
                .antMatchers("/user/get-all-slots-by-date/common").permitAll()

                .antMatchers("/common/generate-otp").permitAll()
                .antMatchers("/common/validate-otp").permitAll()

                .antMatchers("/business/login").permitAll()
                .antMatchers("/business/slot/make-unavailable").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers("/business/reschedule-booking").hasAnyRole("ADMIN", "MANAGER")
                .antMatchers("/business/all-slots").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                .antMatchers("/business/all-slots").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                .antMatchers("/business/signup").hasRole("ADMIN")
                .antMatchers("/business/get-all-business-users").hasRole("ADMIN")
                .antMatchers("/business/reset-password").hasRole("ADMIN")
                .antMatchers("/business/update").hasRole("ADMIN")

                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/v3/api-docs", "/v3/api-docs.yaml", "/v3/api-docs/swagger-config", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()

                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}

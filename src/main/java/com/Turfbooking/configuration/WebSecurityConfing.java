package com.Turfbooking.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfing extends WebSecurityConfigurerAdapter {

    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public WebSecurityConfing(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/user/sign-up").permitAll()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/user/getAllSlots").permitAll()
                .antMatchers("/user/validate-otp").permitAll()
                .antMatchers("/user/slot/cancel").permitAll()
                .antMatchers("/user/book-slot").permitAll()
                .antMatchers("/user/update-slot").permitAll()
                .antMatchers("/user/cancel-slot").permitAll()

                .antMatchers("/common/generateOTP").permitAll()
                .antMatchers("/common/validateOTP").permitAll()

                .antMatchers("/business/login").permitAll()
                .antMatchers("/business/reset-password").permitAll()
                .antMatchers("/business/update").permitAll()
                .antMatchers("/business/book-slot").permitAll()
                .antMatchers("/business/getAllSlots").permitAll()
                .antMatchers("/business/cancel-booking").permitAll()

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

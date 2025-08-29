package vn.hoidanit.laptopshop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;


@Configuration
public class ProjectSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/admin**").hasRole("ADMIN")
                .anyRequest().permitAll()
        );

        http.formLogin(flc -> flc
                .loginPage("/login")
                .failureUrl("/login?error")
                .successHandler(authenticationSuccessHandler())
                .permitAll()
        );

        http.sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .invalidSessionUrl("/logout?expired")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

       http.logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true));

       http.exceptionHandling(ex -> ex.accessDeniedPage("/access_denied"));

       http.rememberMe(r -> r.rememberMeServices(rememberMeServices()));

        return http.build();
    }

    @Bean
   public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider =new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
   }

   @Bean
   public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSuccessHandler();
   }

   @Bean
   public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(false);
        return rememberMeServices;
   }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}

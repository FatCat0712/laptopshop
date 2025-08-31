package vn.hoidanit.laptopshop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
public class ProjectSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/admin**").hasRole("ADMIN")
                .requestMatchers("/cart", "/order_history").authenticated()
                .anyRequest().permitAll()
        );

        http.formLogin(flc -> flc
                .loginPage("/login")
                .failureUrl("/login?error")
                .successHandler(authenticationSuccessHandler())
                .permitAll()
        );

        http.sessionManagement(session -> session
                .invalidSessionUrl("/login?expired")
                .maximumSessions(1)
                .expiredUrl("/login?expired")
        );


       http.logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true));

       http.exceptionHandling(ex -> ex.accessDeniedPage("/access_denied"));


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
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}

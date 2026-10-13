package com.starshop.config;

import com.starshop.security.CustomUserDetailsService;
import com.starshop.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Phân quyền URL theo 6 role (F2.6). LEADER sở hữu — thành viên KHÔNG sửa.
 * Web dùng session (form login). JWT (F2.7) dùng cho REST /api/** qua JwtAuthenticationFilter.
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.FORWARD, jakarta.servlet.DispatcherType.ERROR).permitAll()
                // Tài nguyên tĩnh + trang khách (Guest)
                .requestMatchers("/", "/home", "/products/**", "/stores/**",
                                 "/auth/**", "/webjars/**", "/assets/**", "/css/**", "/js/**", "/error").permitAll()
                // Khu vực theo role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/vendor/**").hasRole("VENDOR")
                .requestMatchers("/shipper/**").hasRole("SHIPPER")
                .requestMatchers("/cart/**", "/checkout/**", "/account/**", "/orders/**").hasAnyRole("USER", "VENDOR")
                // REST API: đăng nhập mở, còn lại cần JWT
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/do-login")
                .usernameParameter("email")
                .defaultSuccessUrl("/", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/?logout")
                .permitAll()
            )
            .authenticationProvider(authenticationProvider())
            // API REST (JWT) tách CSRF; web JSP vẫn giữ CSRF mặc định
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            // JWT filter chạy trước filter form-login (chỉ tác động /api/** — xem shouldNotFilter)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

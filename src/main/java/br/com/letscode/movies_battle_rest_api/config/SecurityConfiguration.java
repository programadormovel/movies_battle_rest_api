package br.com.letscode.movies_battle_rest_api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public static BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private br.com.letscode.movies_battle_rest_api.service.SSUserDetailService userDetailsService;

    @Autowired
    private br.com.letscode.movies_battle_rest_api.repository.UserRepository userRepository;

    @Autowired
    private br.com.letscode.movies_battle_rest_api.repository.RoleRepository roleRepository;

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new br.com.letscode.movies_battle_rest_api.service.SSUserDetailService(userRepository, roleRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("h2/console/**").access("hasAnyAuthority('ADMIN')")
                .antMatchers("/").access("hasAnyAuthority('USER', 'ADMIN')")
                .antMatchers("/resultado/{id}").access("hasAnyAuthority('USER', 'ADMIN')")
                .antMatchers("/partida").access("hasAnyAuthority('USER', 'ADMIN')")
                .antMatchers("/ranking").access("hasAnyAuthority('USER', 'ADMIN')")
                .antMatchers("/resultadoFinal").access("hasAnyAuthority('USER', 'ADMIN')")
                .antMatchers("/admin").access("hasAuthority('ADMIN')")
                //.anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login").permitAll()
                .and()
                .httpBasic();

        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsServiceBean()).passwordEncoder(passwordEncoder());
    }
}

package au.net.apollosoft.refdata.producer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@Configuration
@EnableWebSecurity//(debug = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

//    /* (non-Javadoc)
//     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)
//     */
//    @Override
//    @Autowired
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//            //.ldapAuthentication().
//            .inMemoryAuthentication()
//            .withUser("user")
//                .password("password")
//                //.roles("USER")
//            ;
//    }

    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
//                .antMatchers("/health/**").permitAll()
//                .antMatchers("/producer/**").authenticated()
                .anyRequest().authenticated()
            .and()
                .httpBasic()
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            .and()
//                .csrf().disable()
            ;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.WebSecurity)
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .ignoring()
                .antMatchers("/health/**")
                .antMatchers("/producer/**") // TODO: authenticated
            ;
    }

}
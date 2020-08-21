package au.net.apollosoft.refdata.producer;

import java.security.Security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@SpringBootApplication(
    exclude = {
        //SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class
    },
    scanBasePackages = {
        "au.net.apollosoft.refdata.refdata.producer.config",
        "au.net.apollosoft.refdata.refdata.producer.rs",
    }
)
@ImportResource("classpath:applicationContext.xml")
public class ProducerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProducerServiceApplication.class, args);
    }

//    @Primary
//    @Bean("dataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource dataSource() {
//        //properties.keySet().stream().collect(Collectors.toMap(key -> "tomcat." + key, key -> properties.get(key)))
//        return DataSourceBuilder.create().build();
//    }

}
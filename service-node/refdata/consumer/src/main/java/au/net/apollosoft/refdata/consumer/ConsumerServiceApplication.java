package au.net.apollosoft.refdata.consumer;

import java.security.Security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@SpringBootApplication(
    scanBasePackages = {
        "au.net.apollosoft.refdata.refdata.consumer.config",
        "au.net.apollosoft.refdata.refdata.consumer.rs",
    }
)
@ImportResource("classpath:applicationContext.xml")
public class ConsumerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerServiceApplication.class, args);
    }

//    @Primary
//    @Bean("dataSource")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DataSource dataSource() {
//        //properties.keySet().stream().collect(Collectors.toMap(key -> "tomcat." + key, key -> properties.get(key)))
//        return DataSourceBuilder.create().build();
//    }

}
package au.net.apollosoft.refdata.consumer.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@Configuration
public class Config {

    //@Inject private Environment env;

    @Bean("solaceConfig")
    @ConfigurationProperties(prefix = "refdata.solace")
    public Map<String, String> solaceConfig() {
        return new HashMap<>();
    }
    public static final String QUEUE = "queue";

    @Bean("consumerConfig")
    @ConfigurationProperties(prefix = "refdata.consumer")
    public Map<String, Map<String, String>> consumerConfig() {
        //refdata:
        //  consumer:
        //  - key: "key"
        //    table: "table"
        return new HashMap<>();
    }
    public static final String KEY = "key";
    public static final String TABLE = "table";

}
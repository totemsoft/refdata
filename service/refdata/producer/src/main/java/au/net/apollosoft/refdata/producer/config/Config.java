package au.net.apollosoft.refdata.producer.config;

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
    public static final String TOPIC = "topic";

    @Bean("producerConfig")
    @ConfigurationProperties(prefix = "refdata.producer")
    public Map<String, Map<String, String>> producerConfig() {
        //refdata:
        //  producer:
        //  - key: "key"
        //    table: "table"
        //    primaryKey: "id"
        //    criteria: "created_date >= '20200301'"
        return new HashMap<>();
    }
    public static final String KEY = "key";
    public static final String CRON = "cron";
    public static final String TABLE = "table";
    public static final String PRIMARY_KEY = "primaryKey";
    public static final String CRITERIA = "criteria";

}
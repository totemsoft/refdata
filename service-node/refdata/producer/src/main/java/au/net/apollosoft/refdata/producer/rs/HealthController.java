package au.net.apollosoft.refdata.producer.rs;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.net.apollosoft.refdata.commons.util.BeanUtils;

/**
 * https://stormpath.com/blog/jax-rs-vs-spring-rest-endpoints
 * https://stackoverflow.com/questions/20007164/spring-mvc-rest-is-not-jax-rs-compliant-does-it-matter
 * https://stackoverflow.com/questions/42944777/difference-between-jax-rs-and-spring-rest
 * @author vchibaev (Valeri CHIBAEV)
 */
@RestController
@RequestMapping(
    value = "/health"
    , consumes = {"application/json"} // MediaType.APPLICATION_JSON
    , produces = {"application/json"} // MediaType.APPLICATION_JSON
)
public class HealthController implements HealthIndicator {

    //private static final Logger LOG = LoggerFactory.getLogger(HealthController.class);

    @Inject @Named("dataSource") private DataSource dataSource;

    @Inject @Named("solaceConfig") private Map<String, String> solaceConfig;

//    @Inject private ProducerService producerService;

    @Inject @Named("producerConfig") private Map<String, Map<String, String>> config;

    /* (non-Javadoc)
     * @see org.springframework.boot.actuate.health.HealthIndicator#health()
     */
    @RequestMapping(value = "/health", method = RequestMethod.GET, consumes = "*"/*MediaType.MEDIA_TYPE_WILDCARD*/)
    @Override
    public Health health() {
//        if (producerService.health().down()) {
//            return Health.down().withDetail("producerService", false).build();
//        }
        return Health.up().build();
    }

    @RequestMapping(path = "/config", method = RequestMethod.GET, consumes = "*"/*MediaType.MEDIA_TYPE_WILDCARD*/)
    public Map<String, Map<String, String>> config() {
        return config;
    }

    @RequestMapping(path = "/solaceConfig", method = RequestMethod.GET, consumes = "*"/*MediaType.MEDIA_TYPE_WILDCARD*/)
    public Object solaceConfig() throws Exception {
        if (!solaceConfig.containsKey("host")) {
            solaceConfig.put("host", System.getProperty("solace_host", "???"));
        }
        if (!solaceConfig.containsKey("port")) {
            solaceConfig.put("port", System.getProperty("solace_port", "???"));
        }
        if (!solaceConfig.containsKey("VPN")) {
            solaceConfig.put("VPN", System.getProperty("solace_VPN", "???"));
        }
        if (!solaceConfig.containsKey("username")) {
            solaceConfig.put("username", System.getProperty("solace_username", "???"));
        }
        return solaceConfig;
    }
 
    @RequestMapping(path = "/dataSourceInfo", method = RequestMethod.GET, consumes = "*"/*MediaType.MEDIA_TYPE_WILDCARD*/)
    public Object dataSourceInfo() throws Exception {
        return BeanUtils.describe(dataSource);
    }

}
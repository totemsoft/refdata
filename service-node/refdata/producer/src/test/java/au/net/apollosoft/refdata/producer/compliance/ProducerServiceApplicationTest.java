package au.net.apollosoft.refdata.producer.compliance;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import au.net.apollosoft.refdata.producer.BaseProducerRequest;
import au.net.apollosoft.refdata.producer.ProducerResponse;
import au.net.apollosoft.refdata.producer.ProducerService;
import au.net.apollosoft.refdata.producer.config.Config;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@ActiveProfiles("profile1")
//@org.junit.Ignore
public class ProducerServiceApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerServiceApplicationTest.class);

    @Inject private ProducerService producerService;

    @Inject @Named("solaceConfig") private Map<String, String> solace;
    @Inject @Named("producerConfig") private Map<String, Map<String, String>> config;

    @BeforeClass
    public static void beforeClass() {
        // JASYPT
        System.setProperty("ENCRYPTION_PASSWORD", "jasypt");
        // JMS
        System.setProperty("solace_host", "smfs://sol-tst");
        System.setProperty("solace_port", "55443");
        System.setProperty("solace_VPN", "vpn");
        System.setProperty("solace_username", "username"); 
        System.setProperty("solace_password", "password");
        // SSL
        //System.setProperty("javax.net.debug", "ssl");
        System.setProperty("ssl_trustStore", "../../../commons/src/test/resources/ssl/cacerts");
        System.setProperty("ssl_trustStorePassword", "changeit");
        // DATASOURCE
        System.setProperty("datasource_driver", "net.sourceforge.jtds.jdbc.Driver");
        System.setProperty("datasource_url", "jdbc:jtds:sqlserver://dbserver:1433/database");
        System.setProperty("datasource_username", "username");
        System.setProperty("datasource_password", "password");
        // SERVER
        System.setProperty("server_port", "8081");
    }

    @Test
    public void produce() throws Exception {
        if (config.isEmpty()) {
            Assert.fail("No valid producerConfig found.");
            return;
        }
        //
        for (Map<String, String> cfg : config.values()) {
            BaseProducerRequest request = new BaseProducerRequest();
            request.setKey(cfg.get(Config.KEY));
            request.setStartDateInclusive(new Date()); // now
            //request.setStartDateInclusive(DateUtils.parse("2019-05-01", DateDeserializer.DATE_DEFAULT));
            request.setEndDateExclusive(null);
            //request.setMaxResults(10_000);
            request.setProfile("profile2"); // produce for RefData consumption ONLY
            ProducerResponse result = producerService.produce(request);
            if (result != null) {
                LOG.info(result.getColumns());
//                for (String data : result.getData()) {
//                    if (LOG.isDebugEnabled()) LOG.debug(data);
//                }
            }
        }
    }

}
package au.net.apollosoft.refdata.consumer.compliance;

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

import au.net.apollosoft.refdata.consumer.ConsumerService;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("profile1")
@org.junit.Ignore
public class ConsumerServiceApplicationTest {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerServiceApplicationTest.class);

    @Inject @Named("solaceConfig") private Map<String, String> solace;
    @Inject @Named("consumerConfig") private Map<String, Map<String, String>> config;

    @Inject private ConsumerService consumerService;

    //@EndpointInject(ref = "refdata_topic")
    //@Inject private ProducerTemplate refdataProducerTemplate;

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
        System.setProperty("datasource_url", "jdbc:jtds:sqlserver://dbserver:1433/");
        System.setProperty("datasource_username", "username");
        System.setProperty("datasource_password", "password");
        // SERVER
        System.setProperty("server_port", "8082");
    }

    @Test
    public void consume() throws Exception {
        if (config.isEmpty()) {
            Assert.fail("No valid consumerConfig found.");
            return;
        }
        //
        //consumerService.consume(refdata);
        //refdataProducerTemplate.sendBody(IOUtils.toString(this.getClass().getClassLoader().getResource("data/refdata.xml")));
        do {
            try {
                LOG.info("Waiting for a message to process [{} in progress]..", consumerService.progress());
                Thread.sleep(10_000L);
            } catch (InterruptedException ignore) {}
        } while (consumerService.progress() > 0);
        LOG.info("Stop waiting for a message to process.");
        //
        try {
            Thread.sleep(10_000L);
        } catch (InterruptedException ignore) {}
    }

}
package au.net.apollosoft.refdata.producer.rs;

import javax.inject.Inject;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.net.apollosoft.refdata.producer.BaseProducerRequest;
import au.net.apollosoft.refdata.producer.ProducerResponse;
import au.net.apollosoft.refdata.producer.ProducerService;

/**
 * https://stormpath.com/blog/jax-rs-vs-spring-rest-endpoints
 * https://stackoverflow.com/questions/20007164/spring-mvc-rest-is-not-jax-rs-compliant-does-it-matter
 * https://stackoverflow.com/questions/42944777/difference-between-jax-rs-and-spring-rest
 * @author vchibaev (Valeri CHIBAEV)
 */
@RestController
@RequestMapping(
    value = "/producer"
    , consumes = {"application/json"} // MediaType.APPLICATION_JSON
    , produces = {"application/json"} // MediaType.APPLICATION_JSON
)
public class ProducerController implements ProducerService {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerController.class);

    @Inject private ProducerService producerService;

    /* (non-Javadoc)
     * @see au.net.apollosoft.refdata.refdata.producer.ProducerService#produce(au.net.apollosoft.refdata.refdata.producer.BaseProducerRequest)
     */
    @Override
    @RequestMapping(path="/produce", method=RequestMethod.POST)
    public ProducerResponse produce(
        @RequestBody BaseProducerRequest request) throws Exception {
        LOG.info(">>> produce: " + request);
        try {
            return producerService.produce(request);
        } catch (Exception e) {
            LOG.error(ExceptionUtils.getRootCauseMessage(e), e);
            throw new Exception("Failed produce:\n" + ExceptionUtils.getRootCauseMessage(e));
        }
    }

}
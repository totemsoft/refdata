package au.net.apollosoft.refdata.producer;

import org.springframework.stereotype.Service;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@Service
public interface ProducerService {

    /**
     * 
     * @param request
     * @return
     * @throws Exception
     */
    ProducerResponse produce(BaseProducerRequest request) throws Exception;

}
package au.net.apollosoft.refdata.consumer;

import org.springframework.stereotype.Service;

import au.net.apollosoft.refdata.v1.Content;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@Service
public interface ConsumerService {

    /**
     * 
     * @return number of current running consume processes
     */
    int progress();

    /**
     * 
     * @param content
     * @throws Exception
     */
    void consume(Content content) throws Exception;

}
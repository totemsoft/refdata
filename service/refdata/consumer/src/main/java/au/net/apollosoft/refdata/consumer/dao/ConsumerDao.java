package au.net.apollosoft.refdata.consumer.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import au.net.apollosoft.refdata.v1.CsvType;

import au.net.apollosoft.refdata.consumer.ConsumerRequest;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@Repository
public interface ConsumerDao {

    /**
     * 
     * @param request
     * @param data
     * @throws Exception
     */
    void save(ConsumerRequest request, List<CsvType> data) throws Exception;

}
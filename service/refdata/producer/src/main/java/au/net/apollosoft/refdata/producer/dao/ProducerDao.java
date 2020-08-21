package au.net.apollosoft.refdata.producer.dao;

import java.util.Date;

import org.springframework.stereotype.Repository;

import au.net.apollosoft.refdata.producer.ProducerResponse;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@Repository
public interface ProducerDao {

    /**
     * 
     * @param table
     * @param criteria, eg "last_up_user_tad >= ? and last_up_user_tad < ?"
     * @param startDateInclusive - optional (default to 0 date)
     * @param endDateExclusive - optional (default to now)
     * @return
     */
    ProducerResponse find(String table, String criteria, Date startDateInclusive, Date endDateExclusive) throws Exception;

}
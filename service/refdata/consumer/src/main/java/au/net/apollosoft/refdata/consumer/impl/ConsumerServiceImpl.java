package au.net.apollosoft.refdata.consumer.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.net.apollosoft.refdata.v1.Content;
import au.net.apollosoft.refdata.v1.CsvType;
import au.net.apollosoft.refdata.v1.RefDataType;

import au.net.apollosoft.refdata.consumer.ConsumerRequest;
import au.net.apollosoft.refdata.consumer.ConsumerService;
import au.net.apollosoft.refdata.consumer.config.Config;
import au.net.apollosoft.refdata.consumer.dao.ConsumerDao;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
//@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class ConsumerServiceImpl implements ConsumerService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerServiceImpl.class);

    @Inject private Environment env;
    //
    private String[] activeProfiles;

    @Inject @Named("consumerConfig") private Map<String, Map<String, String>> config;

    @Inject private ConsumerDao consumerDao;

    private AtomicInteger progress = new AtomicInteger();

    @PostConstruct
    public void init() {
        if (config.isEmpty()) {
            throw new IllegalArgumentException("Consumer Config is empty, check application-$spring.profiles.active.yml");
        }
        this.activeProfiles = env.getActiveProfiles();
    }

    /* (non-Javadoc)
     * @see au.net.apollosoft.refdata.refdata.consumer.ConsumerService#progress()
     */
    @Override
    public int progress() {
        return progress.get();
    }

    /* (non-Javadoc)
     * @see au.net.apollosoft.refdata.refdata.consumer.ConsumerService#consume(au.net.apollosoft.refdata.v1.Content)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void consume(Content content) throws Exception {
        int p = progress.incrementAndGet();
        LOG.info("consume started: progress={}", p);
        try {
            final RefDataType refdata = content.getRefdata();
            final String key = refdata.getKey(); // employee
            // check if data not empty
            final List<CsvType> data = refdata.getData();
            if (data.isEmpty()) {
                return;
            }
            final String profile  = refdata.getProfile();
            // check if optional request profile name matches
            if (profile != null && Arrays.binarySearch(activeProfiles, profile) < 0) {
                LOG.info("consume skipped for profile {}: progress={}", profile, p);
                return;
            }
            //
            if (LOG.isInfoEnabled()) LOG.info("{} {} consume started..", Arrays.asList(activeProfiles), key);
            //
            Map<String, String> keyConfig = keyConfig(key);
            if (keyConfig == null) {
                throw new Exception("Failed to find consumer config [" + key + "]");
            }
            final String table = keyConfig.get(Config.TABLE); // common.dbo.employee
            //
            final String primaryKey = refdata.getPrimaryKey();
            final String columns = refdata.getColumns();
            LOG.info("[{}] loading {}: {} record(s), primaryKey: [{}], columns: [{}]", key, table, data.size(), primaryKey, columns);
            ConsumerRequest request = new ConsumerRequest();
            request.setKey(key);
            request.setTable(table);
            request.setPrimaryKey(primaryKey);
            request.setColumns(columns);
            consumerDao.save(request, data);
            if (LOG.isInfoEnabled()) LOG.info("{} {} consume completed.", Arrays.asList(activeProfiles), key);
        } finally {
            p = progress.decrementAndGet();
            LOG.info("consume completed: progress={}", p);
        }
    }

    private Map<String, String> keyConfig(String key) {
        LOG.info("config: {}", config); // {0={key=employee,table=common.dbo.employee}}
        for (Map<String, String> value : config.values()) {
            if (key.equals(value.get(Config.KEY))) {
                LOG.info("{}: {}", key, value);
                return value;
            }
        }
        return null;
    }

}
package au.net.apollosoft.refdata.producer.impl;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;

import au.net.apollosoft.refdata.producer.ProducerRequest;
import au.net.apollosoft.refdata.producer.ProducerService;
import au.net.apollosoft.refdata.producer.ProducerTask;
import au.net.apollosoft.refdata.producer.config.Config;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
public class ProducerTaskImpl implements ProducerTask {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerTaskImpl.class);

    @Inject private TaskScheduler executor;

    @Inject private ProducerService producerService;

    @Inject @Named("producerConfig") private Map<String, Map<String, String>> config;

    private Map<String, CronSequenceGenerator> cronGenerator = new Hashtable<>();
    private Map<String, Date> cronLast = new Hashtable<>();

    @PostConstruct
    public void init() {
        if (config.isEmpty()) {
            throw new IllegalArgumentException("Producer Config is empty, check application-$spring.profiles.active.yml");
        }
        //
        final Date now = new Date();
        for (Map<String, String> cfg : config.values()) {
            final String key = cfg.get(Config.KEY);
            final String cron = cfg.get(Config.CRON);
            //
            final CronSequenceGenerator generator = new CronSequenceGenerator(cron);
            cronGenerator.put(key, generator);
            //
            final Date next = generator.next(now);
            final Date next2 = generator.next(next);
            final Date last = new Date(next.getTime() - (next2.getTime() - next.getTime()));
            cronLast.put(key, last);
            //
            // Schedule a task with the given cron expression
            final Runnable task = () -> produce(cfg);
            executor.schedule(task, new CronTrigger(cron));
            LOG.info("[{}] Producer Task Scheduled: cron {}], last={}, next={}..", key, cron, last, next);
        }
    }

    private void produce(final Map<String, String> cfg) {
        final String key = cfg.get(Config.KEY);
        try {
            final CronSequenceGenerator generator = cronGenerator.get(key);
            final Date last = cronLast.get(key);
            final Date next = generator.next(last);
            LOG.info("[{}] Producer Started: next {} [last {}]..", key, next, last);
            //cronLast.put(key, next); // store to avoid duplicate run
            ProducerRequest request = new ProducerRequest();
            request.setKey(key);
            request.setTable(cfg.get(Config.TABLE));
            request.setPrimaryKey(cfg.get(Config.PRIMARY_KEY));
            request.setCriteria(cfg.get(Config.CRITERIA));
            request.setStartDateInclusive(last);
            request.setEndDateExclusive(next);
            //request.setMaxResults(1000);
            //request.setProfile(null);
            /*ProducerResponse result = */producerService.produce(request);
            cronLast.put(key, next); // finally store to avoid duplicate run
            LOG.info(".. [{}] Producer Completed: next {}.", key, next);
        } catch (Exception e) {
            LOG.error("[" + key + "] Producer Failed: " + e.getMessage(), e);
        }
    }

}
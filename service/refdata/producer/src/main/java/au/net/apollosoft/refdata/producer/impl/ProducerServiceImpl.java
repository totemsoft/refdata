package au.net.apollosoft.refdata.producer.impl;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.transform.stream.StreamResult;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.oxm.Marshaller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.net.apollosoft.refdata.commons.util.ArrayUtils;
import au.net.apollosoft.refdata.producer.BaseProducerRequest;
import au.net.apollosoft.refdata.producer.ProducerRequest;
import au.net.apollosoft.refdata.producer.ProducerResponse;
import au.net.apollosoft.refdata.producer.ProducerService;
import au.net.apollosoft.refdata.producer.config.Config;
import au.net.apollosoft.refdata.producer.dao.ProducerDao;
import au.net.apollosoft.refdata.v1.Content;
import au.net.apollosoft.refdata.v1.CsvType;
import au.net.apollosoft.refdata.v1.RefDataType;
import au.net.apollosoft.v1.DomainType;
import au.net.apollosoft.v1.IdType;
import au.net.apollosoft.v1.IdentifierType;
import au.net.apollosoft.v1.Message;
import au.net.apollosoft.v1.MetadataType;
import au.net.apollosoft.v1.PayloadType;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class ProducerServiceImpl implements ProducerService {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerServiceImpl.class);

    private static final DateFormat DATETIME_XML = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Inject private Environment env;
    //
    private String[] activeProfiles;

    @Inject @Named("producerConfig") private Map<String, Map<String, String>> config;

    @Inject private ProducerDao producerDao;

    /** oxm */
    @Inject private Marshaller marshaller;

    //@EndpointInject(ref = "direct-vm:producerService:produce")
    @Inject private ProducerTemplate refdataProducerTemplate;

    @PostConstruct
    public void init() {
        this.activeProfiles = env.getActiveProfiles();
    }

    private Map<String, String> config(String key) {
        for (Map<String, String> cfg : config.values()) {
            if (key.equals(cfg.get(Config.KEY))) {
                return cfg;
            }
        }
        throw new IllegalArgumentException("No producer config found for key: " + key);
    }

    /* (non-Javadoc)
     * @see au.net.apollosoft.refdata.refdata.producer.ProducerService#produce(au.net.apollosoft.refdata.refdata.producer.BaseProducerRequest)
     */
    @Override
    public ProducerResponse produce(BaseProducerRequest baseRequest) throws Exception {
        if (LOG.isInfoEnabled()) LOG.info("{} publishing: {}", Arrays.asList(activeProfiles), baseRequest.toString());
        final String key = baseRequest.getKey(); // employee
        final Map<String, String> cfg = config(key);
        final ProducerRequest request = new ProducerRequest(baseRequest);
        request.setTable(cfg.get(Config.TABLE));
        request.setPrimaryKey(cfg.get(Config.PRIMARY_KEY));
        //request.setColumns(cfg.get(Config.));
        request.setCriteria(cfg.get(Config.CRITERIA));
        ProducerResponse response = producerDao.find(
            request.getTable(),
            request.getCriteria(),
            request.getStartDateInclusive(),
            request.getEndDateExclusive());
        //
        final String columns = response.getColumns();
        // 
        final int step = request.getMaxResults() > 0 ? request.getMaxResults() : 1000;
        String[] data = response.getData();
        LOG.info("[{}] publishing: {} record(s)..", key, data.length);
        final int max = data.length;
        int startIndexInclusive = 0;
        int endIndexExclusive = startIndexInclusive + step;
        while (startIndexInclusive < max) {
            if (endIndexExclusive > max) {
                endIndexExclusive = max;
            }
            if (LOG.isDebugEnabled()) LOG.debug(">>> [{}] publishing max={}, startIndexInclusive={}, endIndexExclusive={}", request.getKey(), max, startIndexInclusive, endIndexExclusive);
            //
            String[] partData = ArrayUtils.subarray(data, startIndexInclusive, endIndexExclusive);
            produce(request, columns, partData);
            //
            // update indexes 
            startIndexInclusive = endIndexExclusive;
            endIndexExclusive = startIndexInclusive + step;
        }
        if (LOG.isInfoEnabled()) LOG.info("{} published: {}", Arrays.asList(activeProfiles), request.toString());
        return response;
    }

    private void produce(ProducerRequest request, String columns, String[] data) throws Exception {
        Content content = createContent(request, columns, data);
        Message message = wrapMessage(content, request.getTable());
        ByteArrayOutputStream canonStream = new ByteArrayOutputStream();
        marshaller.marshal(message, new StreamResult(canonStream));
        String canonXml = canonStream.toString().trim();
        if (LOG.isDebugEnabled()) LOG.debug(canonXml);
        // HACK BEGIN
        canonXml = canonXml
            .replace(" xmlns:ns2=\"http://apollosoft.net.au/refdata/v1\"", "")
            .replace("ns2:", "")
            .replace("<content>", "<content xmlns=\"http://apollosoft.net.au/refdata/v1\">")
            ;
        // HACK END
        if (LOG.isDebugEnabled()) LOG.debug(canonXml);
        refdataProducerTemplate.sendBody("direct-vm:producerService:produce", canonXml);
    }

    private Content createContent(ProducerRequest request, String columns, String[] data) {
        Content content = new Content();
        //
        RefDataType refdata = new RefDataType();
        // from request
        refdata.setKey(request.getKey());
        refdata.setTable(request.getTable());
        refdata.setPrimaryKey(request.getPrimaryKey());
        refdata.setCriteria(request.getCriteria());
        refdata.setProfile(request.getProfile());
        // from response
        refdata.setColumns(columns);
        //
        for (String row : data) {
            CsvType csv = new CsvType();
            //csv.setType("U"); // TODO: enum ([U]pdate, [D]elete, [I]nsert)
            csv.setValue(row);
            refdata.getData().add(csv);
        }
        content.setRefdata(refdata);
        //
        return content;
    }

    private Message wrapMessage(Content content, String entityName) {
        final String now = DATETIME_XML.format(new Date());
        // wrap as canon
        Message message = new Message();
        //
        Message.Header header = new Message.Header();
        MetadataType metadata = new MetadataType();
        metadata.setMessageId(UUID.randomUUID().toString());
        IdentifierType identifier = new IdentifierType();
        IdType id = new IdType();
        id.setValue(entityName);
        identifier.setId(id);
        identifier.setVersion(now);
        metadata.setIdentifier(identifier);
        DomainType domain = new DomainType();
        domain.setName("refdata");
        domain.setSchemaVersion("1.0");
        
        //domain.setSubdomain(subdomain);
        metadata.setDomain(domain);
        header.setMetadata(metadata);
        
        message.setHeader(header);
        
        //
        PayloadType payload = new PayloadType();
        payload.getContent().add(content);
        message.setPayload(payload);
        //
        return message;
    }

}
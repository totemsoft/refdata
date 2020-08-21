package au.net.apollosoft.refdata.producer;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import au.net.apollosoft.refdata.commons.databind.DateDeserializer;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@JsonInclude(Include.NON_NULL)
public class BaseProducerRequest {

    @JsonProperty protected String key;

    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    protected Date startDateInclusive;

    @JsonProperty
    @JsonDeserialize(using = DateDeserializer.class)
    protected Date endDateExclusive;

    @JsonProperty protected int maxResults = 1000;

    @JsonProperty protected String profile;

    public BaseProducerRequest() {
        super();
    }

    public BaseProducerRequest(BaseProducerRequest request) {
        this.key = request.key;
        this.startDateInclusive = request.startDateInclusive;
        this.endDateExclusive = request.endDateExclusive;
        this.maxResults = request.maxResults;
        this.profile = request.profile;
    }

    /**
     * @return the key - unique refdata key, eg "employee"
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the startDateInclusive
     */
    public Date getStartDateInclusive() {
        return startDateInclusive;
    }

    public void setStartDateInclusive(Date startDateInclusive) {
        this.startDateInclusive = startDateInclusive;
    }

    /**
     * @return the endDateExclusive
     */
    public Date getEndDateExclusive() {
        return endDateExclusive;
    }

    public void setEndDateExclusive(Date endDateExclusive) {
        this.endDateExclusive = endDateExclusive;
    }

    /**
     * @return the maxResults
     */
    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("key", key)
            .append("startDateInclusive", startDateInclusive)
            .append("endDateExclusive", endDateExclusive)
            .append("maxResults", maxResults)
            .append("profile", profile)
            .toString();
    }

}
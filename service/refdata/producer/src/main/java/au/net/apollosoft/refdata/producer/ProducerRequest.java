package au.net.apollosoft.refdata.producer;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@JsonInclude(Include.NON_NULL)
public class ProducerRequest extends BaseProducerRequest {

    @JsonProperty private String table;
    
    @JsonProperty private String primaryKey;

    @JsonProperty private String columns;

    @JsonProperty private String criteria;

    public ProducerRequest() {
        super();
    }

    public ProducerRequest(BaseProducerRequest request) {
        super(request);
    }

    /**
     * @return the table - table name
     */
    public String getTable() {
        return table;
    }
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the primaryKey - table primary key(s), eg "id,"
     */
    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @return the columns - table select
     */
    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    /**
     * @return the criteria - sql where criteria, eg "last_up_user_tad >= ? and last_up_user_tad < ?"
     */
    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("key", key)
            .append("table", table)
            .append("primaryKey", primaryKey)
            .append("columns", columns)
            .append("criteria", criteria)
            .append("startDateInclusive", startDateInclusive)
            .append("endDateExclusive", endDateExclusive)
            .append("maxResults", maxResults)
            .append("profile", profile)
            .toString();
    }

}
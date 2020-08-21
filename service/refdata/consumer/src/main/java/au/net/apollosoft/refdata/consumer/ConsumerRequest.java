package au.net.apollosoft.refdata.consumer;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@JsonInclude(Include.NON_NULL)
public class ConsumerRequest {

    @JsonProperty private String key;

    @JsonProperty private String table;

    @JsonProperty private String primaryKey;

    @JsonProperty private String columns;

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
     * @return the table - table name, eg "commond.dbo.employee"
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
            .toString();
    }

}
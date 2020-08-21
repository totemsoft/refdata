package au.net.apollosoft.refdata.producer;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
@JsonInclude(Include.NON_NULL)
public class ProducerResponse {

    @JsonProperty private String columns;

    @JsonIgnore private String[] data;

    /**
     * @return the columns
     */
    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    /**
     * @return the data
     */
    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("columns", columns)
            //.append("data", data == null ? null : Arrays.asList(data))
            .toString();
    }

}
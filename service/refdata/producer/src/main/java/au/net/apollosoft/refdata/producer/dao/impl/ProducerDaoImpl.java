package au.net.apollosoft.refdata.producer.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.net.apollosoft.refdata.producer.ProducerResponse;
import au.net.apollosoft.refdata.producer.dao.ProducerDao;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
public class ProducerDaoImpl implements ProducerDao {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerDaoImpl.class);

    @Inject private DataSource dataSource;

    /* (non-Javadoc)
     * @see au.net.apollosoft.refdata.refdata.producer.dao.ProducerDao#find(java.lang.String, java.lang.String, java.util.Date, java.util.Date)
     */
    @Override
    public ProducerResponse find(String table, String criteria, Date startDateInclusive, Date endDateExclusive) throws Exception {
        String sql = String.format("SELECT * FROM %1$s", table);
        if (criteria != null && !criteria.isEmpty()) {
            sql += " WHERE " + criteria;
        }
        try (
            Connection con = dataSource.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            // check for date from/to parameters
            if (LOG.isInfoEnabled()) LOG.info("sql: {}", sql);
            if (sql.contains("?")) {
                //
                final java.sql.Date from = new java.sql.Date(
                    startDateInclusive == null ? 0 : startDateInclusive.getTime());
                if (LOG.isInfoEnabled()) LOG.info("startDateInclusive: {}", from);
                stmt.setDate(1, from);
                //
                if (sql.indexOf('?') != sql.lastIndexOf('?')) {
                    final java.sql.Date to = new java.sql.Date(
                        endDateExclusive == null ? System.currentTimeMillis() : endDateExclusive.getTime());
                    if (LOG.isInfoEnabled()) LOG.info("endDateExclusive: {}", to);
                    stmt.setDate(2, to);
                }
            }
            //
            try (ResultSet rs = stmt.executeQuery()) {
                ProducerResponse result = new ProducerResponse();
                //
                final ResultSetMetaData md = rs.getMetaData();
                String header = "";
                for (int i = 0; i < md.getColumnCount(); i++) {
                    if (!header.isEmpty()) {
                        header += ",";
                    }
                    header += md.getColumnName(i + 1);
                }
                result.setColumns(header);
                //
                List<String> rows = new ArrayList<>();
                //CSVWriter csvWriter = new CSVWriter(new StringWriter());
                while (rs.next()) {
                    String row = "";
                    for (int i = 0; i < md.getColumnCount(); i++) {
                        int columnIndex = i + 1;
                        if (!row.isEmpty()) {
                            row += ",";
                        }
                        String value = rs.getString(columnIndex);
                        if (value == null) {
                            // TODO: handle null
                            //row += value;
                        } else {
                            value = value.trim();
                            if (value.contains(",")) {
                                value = "\"" + value + "\"";
                            }
                            row += value;
                        }
                    }
                    rows.add(row);
                    //csvWriter.writeNext(nextLine);
                }
                result.setData(rows.toArray(new String[0]));
                //
                return result;
            }
        } catch (Exception e) {
            LOG.error("Failed to find: " + table, e);
            throw e;
        }
    }

}
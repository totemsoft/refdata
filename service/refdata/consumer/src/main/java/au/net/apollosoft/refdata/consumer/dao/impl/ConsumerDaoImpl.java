package au.net.apollosoft.refdata.consumer.dao.impl;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.net.apollosoft.refdata.v1.CsvType;

import au.com.bytecode.opencsv.CSVReader;
import au.net.apollosoft.refdata.commons.domain.SqlTypeEnum;
import au.net.apollosoft.refdata.commons.util.SqlUtils;
import au.net.apollosoft.refdata.consumer.ConsumerRequest;
import au.net.apollosoft.refdata.consumer.dao.ConsumerDao;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
public class ConsumerDaoImpl implements ConsumerDao {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumerDaoImpl.class);

    @Inject private DataSource dataSource;

    /* (non-Javadoc)
     * @see au.net.apollosoft.refdata.refdata.consumer.dao.ConsumerDao#save(au.net.apollosoft.refdata.refdata.consumer.ConsumerRequest, java.util.List)
     */
    @Override
    public void save(ConsumerRequest request, List<CsvType> data) throws Exception {
        final String key = request.getKey(); // employee
        final String table = request.getTable(); // common.dbo.employee
        final String primaryKey = request.getPrimaryKey(); // id
        final String columns = request.getColumns(); // id,last_name,first_name,..
        final int primaryKeyIdx = primaryKeyIdx(primaryKey, columns);
        try (
            Connection con = dataSource.getConnection();
        ) {
            final ResultSetMetaData md = SqlUtils.getMetaData(con, table);
            //
            final String selectSql = String.format("SELECT * FROM %1$s WHERE %2$s = ?", table, primaryKey);
            LOG.info("selectSql: {}", selectSql);
            //
            final String deleteSql = String.format("DELETE FROM %1$s WHERE %2$s = ?", table, primaryKey);
            LOG.info("deleteSql: {}", deleteSql);
            //
            String updateSet = SqlUtils.updateSet(md, primaryKey);
            final String updateSql = String.format("UPDATE %1$s SET %2$s WHERE %3$s = ?", table, updateSet, primaryKey);
            LOG.info("updateSql: {}", updateSql);
            //
            String insertColumns = SqlUtils.insertColumns(md);
            String insertValues = SqlUtils.insertValues(md);
            final String insertSql = String.format("INSERT INTO %1$s (%2$s) VALUES (%3$s)", table, insertColumns, insertValues);
            LOG.info("insertSql: {}", insertSql);
            //
            try (
                PreparedStatement selectStmt = con.prepareStatement(selectSql);
                PreparedStatement deleteStmt = con.prepareStatement(deleteSql);
                PreparedStatement updateStmt = con.prepareStatement(updateSql);
                PreparedStatement insertStmt = con.prepareStatement(insertSql);
            ) {
                //
                for (CsvType row : data) {
                    SqlTypeEnum type;
                    try {
                        type = row.getType() == null ? null : SqlTypeEnum.valueOf(row.getType());
                    } catch (Exception e) {
                        type = null;
                    }
                    final String csv = row.getValue(); // 91861,Orlov,Alex,,aorlov1,..
                    final String[] values;
                    try (CSVReader csvReader = new CSVReader(new StringReader(csv))) {
                        values = csvReader.readNext();
                    }
                    final String primaryKeyValue = values[primaryKeyIdx];
                    selectStmt.setString(1, primaryKeyValue);
                    try (ResultSet rs = selectStmt.executeQuery();) {
                        if (SqlTypeEnum.D == type) {
                            // delete
                            if (LOG.isDebugEnabled()) LOG.debug("[{}] DELETE {}: {}", key, primaryKeyValue, csv);
                            deleteStmt.setString(1, primaryKeyValue);
                            deleteStmt.executeUpdate();
                        } else {
                            if (rs.next()) {
                                // update
                                if (LOG.isDebugEnabled()) LOG.debug("[{}] UPDATE {}: {}", key, primaryKeyValue, csv);
                                int i = 0;
                                for (String updateItem : updateSet.split(",")) {
                                    final String column = updateItem.substring(0, updateItem.indexOf("=")).trim();
                                    if (column.equals(primaryKey)) {
                                        continue; // skip primary key
                                    }
                                    int columnIdx = columnIdx(column, columns);
                                    if (columnIdx >= 0) {
                                        updateStmt.setString(++i, values[columnIdx]);
                                        if (LOG.isDebugEnabled()) LOG.debug("{}: {}={}", i, column, values[columnIdx]);
                                    } else {
                                        if (LOG.isDebugEnabled()) LOG.debug("{} not found", column);
                                    }
                                }
                                updateStmt.setString(++i, primaryKeyValue);
                                if (LOG.isDebugEnabled()) LOG.debug("{}: {}={}", i, primaryKey, primaryKeyValue);
                                updateStmt.executeUpdate();
                            } else {
                                // insert
                                if (LOG.isDebugEnabled()) LOG.debug("[{}] INSERT {}: {}", key, primaryKeyValue, csv);
                                int i = 0;
                                for (String column : insertColumns.split(",")) {
                                    column = column.trim();
                                    int columnIdx = columnIdx(column, columns);
                                    if (columnIdx >= 0) {
                                        insertStmt.setString(++i, values[columnIdx]);
                                        if (LOG.isDebugEnabled()) LOG.debug("{}: {}={}", i, column, values[columnIdx]);
                                    } else {
                                        if (LOG.isDebugEnabled()) LOG.debug("{} not found", column);
                                    }
                                }
                                insertStmt.executeUpdate();
                            }
                        }
                    } catch (Exception e) {
                        LOG.error("[{}] Failed to save: {}", table, csv);
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to save: " + table, e);
            throw e;
        }
    }

    /**
     * derive primaryKey index
     * @param primaryKey
     * @param columns
     * @return
     */
    private int primaryKeyIdx(String primaryKey, String columns) throws Exception {
        final String[] columnNames = columns.split(",");
        for (int i = 0; i < columnNames.length; i++) {
            if (primaryKey.equals(columnNames[i])) {
                return i;
            }
        }
        throw new Exception("Failed to find primaryKey [" + primaryKey + "] in columns: " + columns);
    }
    /**
     * 
     * @param column
     * @param columns
     * @return
     */
    private int columnIdx(String column, String columns) {
        final String[] columnNames = columns.split(",");
        for (int i = 0; i < columnNames.length; i++) {
            if (column.equals(columnNames[i])) {
                return i;
            }
        }
        return -1;
    }

}
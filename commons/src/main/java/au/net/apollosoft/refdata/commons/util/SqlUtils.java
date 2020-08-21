package au.net.apollosoft.refdata.commons.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * @author vchibaev (Valeri CHIBAEV)
 */
public abstract class SqlUtils {

    //private static final Logger LOG = LoggerFactory.getLogger(SqlUtils.class);

    public static ResultSetMetaData getMetaData(Connection con, String table) throws Exception {
        final String sql = String.format("SELECT * FROM %1$s", table);
        try (
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
        ) {
            return rs.getMetaData();
        }
    }

    public static String updateSet(ResultSetMetaData md, String primaryKey) throws Exception {
        String result = "";
        for (int i = 0; i < md.getColumnCount(); i++) {
            final String updateColumn = md.getColumnName(i + 1);
            if (updateColumn.equals(primaryKey)) {
                continue; // skip primary key
            }
            if (!result.isEmpty()) {
                result += ", ";
            }
            result += updateColumn + " = ?";
        }
        return result;
    }

    public static String insertColumns(ResultSetMetaData md) throws Exception {
        String result = "";
        for (int i = 0; i < md.getColumnCount(); i++) {
            final String insertColumn = md.getColumnName(i + 1);
            if (!result.isEmpty()) {
                result += ", ";
            }
            result += insertColumn;
        }
        return result;
    }

    public static String insertValues(ResultSetMetaData md) throws Exception {
        String result = "";
        for (int i = 0; i < md.getColumnCount(); i++) {
            if (!result.isEmpty()) {
                result += ", ";
            }
            result += "?";
        }
        return result;
    }

}
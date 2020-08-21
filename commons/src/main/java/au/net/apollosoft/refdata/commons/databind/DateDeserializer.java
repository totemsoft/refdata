package au.net.apollosoft.refdata.commons.databind;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import au.net.apollosoft.refdata.commons.util.DateUtils;

/**
 * Unparseable date: "1494288...
 * @author vchibaev (Valeri CHIBAEV)
 */
public class DateDeserializer extends JsonDeserializer<Date> {

    /** json date format */
    private static final DateFormat DATE_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    /** default date format */
    public static final DateFormat DATE_DEFAULT = DATE_YYYY_MM_DD;

    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {
        String s = jsonparser.getText();
        if (NumberUtils.isDigits(s)) {
            return DateUtils.getStartOfDay(new Date(Long.parseLong(s)));
        }
        return DateUtils.parse(s, DATE_DEFAULT, true);
    }

}
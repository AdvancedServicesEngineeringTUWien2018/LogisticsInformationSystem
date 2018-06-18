package micc.ase.logistics.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String localTimeFormat(long timestamp) {
        return DF.format(new Date(timestamp));
    }

}

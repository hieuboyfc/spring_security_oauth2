package zimji.hieuboy.oauth2.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 16/08/2020 - 17:55
 */

public class Common {

    public static String getStackTrace(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.close();
        try {
            stringWriter.close();
        } catch (IOException ex) {
            ex.getStackTrace();
        }
        return stringWriter.toString();
    }

    public static String convertDate2String(Date dateInput, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(dateInput);
    }

    public static long getDateDiff(Date dateBegin, Date dateEnd, TimeUnit dateDiff) {
        long iDiff = dateEnd.getTime() - dateBegin.getTime();
        switch (dateDiff) {
            case SECONDS:
                iDiff = TimeUnit.SECONDS.convert(iDiff, TimeUnit.MILLISECONDS);
                break;
            case MINUTES:
                iDiff = TimeUnit.MINUTES.convert(iDiff, TimeUnit.MILLISECONDS);
                break;
            case HOURS:
                iDiff = TimeUnit.HOURS.convert(iDiff, TimeUnit.MILLISECONDS);
                break;
            case DAYS:
                iDiff = TimeUnit.DAYS.convert(iDiff, TimeUnit.MILLISECONDS);
                break;
            default:
                break;
        }
        return iDiff;
    }

}

package zimji.hieuboy.oauth2.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 16/08/2020 - 17:55
 */

public class Common {

    public static String convertDate2String(Date tInput, String sFomat) {
        return new SimpleDateFormat(sFomat).format(tInput);
    }

    public static long getDateDiff(java.util.Date tBegin, java.util.Date tEnd, TimeUnit tuDiff) {
        long iDiff = tEnd.getTime() - tBegin.getTime();
        switch (tuDiff) {
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

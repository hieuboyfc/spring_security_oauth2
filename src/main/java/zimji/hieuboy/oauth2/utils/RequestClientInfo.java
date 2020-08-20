package zimji.hieuboy.oauth2.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 17/08/2020 - 11:10
 * https://gist.github.com/c0rp-aubakirov/a4349cbd187b33138969
 * http://stackoverflow.com/a/18030465/1845894
 */

@Component
public class RequestClientInfo {

    private static RequestClientInfo instance = new RequestClientInfo();

    public static RequestClientInfo getInstance() {
        return instance;
    }

    private RequestClientInfo() {

    }

    public String getReferer(HttpServletRequest request) {
        return request.getHeader("referer");
    }

    public String getUrlWithQueryString(HttpServletRequest request) {
        final StringBuffer requestURL = request.getRequestURL();
        final String queryString = request.getQueryString();

        return queryString == null ? requestURL.toString() : requestURL.append('?').append(queryString).toString();
    }

    public String getUrlWithoutQueryString(HttpServletRequest request) {
        return request.getRequestURL() == null ? null : request.getRequestURL().toString();
    }

    public String getHttpMethodAndUrlWithoutQueryString(HttpServletRequest request) {
        return getHttpMethodAndUrlWithoutQueryString(request.getMethod(), request.getRequestURI());
    }

    public String getHttpMethodAndUrlWithoutQueryString(String method, String requestURL) {
        String value = null;
        requestURL = StringUtils.trim(requestURL);
        if (!StringUtils.isEmpty(requestURL) && requestURL.contains("/v1/")) {
            requestURL = requestURL.substring(
                    requestURL.indexOf("/v1/") + "/v1/".length());
            if (requestURL.contains("?")) {
                requestURL = requestURL.substring(0, requestURL.indexOf("?"));
            }
            while (requestURL.endsWith("/")) {
                requestURL = requestURL.substring(0, requestURL.length() - 1);
            }
            value = requestURL;
        }
        return String.format("[%s]%s", method, value);
    }

    /**
     * getClientIpAddr - Get Client Ip Address
     */
    public String getClientIpAddr(HttpServletRequest request) {
        String unknown = "unknown";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * getClientOS - Get Client OS
     */
    public String getClientOS(HttpServletRequest request) {
        final String browserDetails = request.getHeader(HttpHeaders.USER_AGENT);

        // =================OS=======================
        final String lowerCaseBrowser = browserDetails.toLowerCase();
        if (lowerCaseBrowser.contains("windows")) {
            return "Windows";
        } else if (lowerCaseBrowser.contains("mac")) {
            return "Mac";
        } else if (lowerCaseBrowser.contains("x11")) {
            return "Unix";
        } else if (lowerCaseBrowser.contains("android")) {
            return "Android";
        } else if (lowerCaseBrowser.contains("iphone")) {
            return "IPhone";
        } else {
            return "UnKnown, More-Info: " + browserDetails;
        }
    }

    /**
     * getClientBrowser - get Client Browser
     */
    public String getClientBrowser(HttpServletRequest request) {
        final String browserDetails = request.getHeader("User-Agent");
        final String user = browserDetails.toLowerCase();

        String browser = "";

        // ===============Browser===========================
        if (user.contains("msie")) {
            String substring = browserDetails.substring(browserDetails.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-"
                    + (browserDetails.substring(browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera"))
                browser = (browserDetails.substring(browserDetails.indexOf("Opera")).split(" ")[0]).split("/")[0] + "-"
                        + (browserDetails.substring(browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
            else if (user.contains("opr"))
                browser = ((browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0]).replace("/", "-"))
                        .replace("OPR", "Opera");
        } else if (user.contains("chrome")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.contains("mozilla/7.0")) || (user.contains("netscape6"))
                || (user.contains("mozilla/4.7")) || (user.contains("mozilla/4.78"))
                || (user.contains("mozilla/4.08")) || (user.contains("mozilla/3"))) {
            browser = "Netscape-?";
        } else if (user.contains("firefox")) {
            browser = (browserDetails.substring(browserDetails.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            browser = "IE";
        } else {
            browser = "UnKnown, More-Info: " + browserDetails;
        }

        return browser;
    }

    /**
     * getUserAgent - Get UserAgent
     */
    public String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * getIdentifyDevice - Get Identify Device
     */
    public String getIdentifyDevice(HttpServletRequest request) {
        String sTemp = String.format("%s|%s", getClientIpAddr(request), getUserAgent(request));
        return DigestUtils.md5DigestAsHex(sTemp.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * getCookieValueByName - Get Cookie Value By Name
     */
    public String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    /**
     * getQueryStringValueByKey - Get QueryString Value By Key
     */
    public String getQueryStringValueByKey(HttpServletRequest request, String key) {
        return StringUtils.trimToEmpty(request.getParameter(key));
    }
}

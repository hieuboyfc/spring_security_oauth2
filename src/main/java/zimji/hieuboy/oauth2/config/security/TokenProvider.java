package zimji.hieuboy.oauth2.config.security;

import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import zimji.hieuboy.oauth2.config.AppProperties;

import java.util.Date;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 22:03
 */

@Component
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private AppProperties appProperties;

    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    /**
     * Khởi tạo token
     */
    public String createToken(TokenPayloadClaims tokenPayloadClaims) {
        // UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder()
                .setIssuer(tokenPayloadClaims.iss())
                .setExpiration(tokenPayloadClaims.exp())
                .setNotBefore(tokenPayloadClaims.nbf())
                .setIssuedAt(tokenPayloadClaims.iat())
                .claim("uid", tokenPayloadClaims.uid())
                .claim("ufn", tokenPayloadClaims.ufn())
                .claim("did", tokenPayloadClaims.did())
                .claim("lcp", tokenPayloadClaims.lcp())
                .claim("expirationDate", tokenPayloadClaims.expirationDate())
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .setSubject("System Authentication")
                .compact();
    }

    /**
     * Reset lại token
     */
    public String reCreateToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        TokenPayloadClaims tokenPayloadClaims = verifyTokenGetInfo(token);
        tokenPayloadClaims.exp(new Date((new Date()).getTime() + 604800000)); // Hết hạn Token 7 Ngày
        tokenPayloadClaims.nbf(new Date());
        tokenPayloadClaims.iat(new Date());
        return createToken(tokenPayloadClaims);
    }

    public TokenPayloadClaims verifyTokenGetInfo(String token) {
        Claims claims = Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(token).getBody();
        TokenPayloadClaims tokenPayloadClaims = new TokenPayloadClaims();
        tokenPayloadClaims.iss(claims.getIssuer());
        tokenPayloadClaims.exp(claims.getExpiration());
        tokenPayloadClaims.nbf(claims.getNotBefore());
        tokenPayloadClaims.iat(claims.getIssuedAt());
        tokenPayloadClaims.uid((String) claims.get("uid"));
        tokenPayloadClaims.ufn((String) claims.get("ufn"));
        tokenPayloadClaims.did((String) claims.get("did"));
        tokenPayloadClaims.lcp((Long) claims.get("lcp"));
        tokenPayloadClaims.expirationDate((Integer) claims.get("expirationDate"));
        return tokenPayloadClaims;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("JWT Token có chữ ký không hợp lệ");
        } catch (MalformedJwtException ex) {
            logger.error("JWT Token không hợp lệ");
        } catch (ExpiredJwtException ex) {
            logger.error("JWT Token đã hết hạn");
        } catch (UnsupportedJwtException ex) {
            logger.error("JWT Token không được hỗ trợ");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT Token không được để trống");
        }
        return false;
    }

}

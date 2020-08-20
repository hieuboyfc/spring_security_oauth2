package zimji.hieuboy.oauth2.configs.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import zimji.hieuboy.oauth2.exceptions.BadRequestException;
import zimji.hieuboy.oauth2.modules.auth.payload.ScopeRequest;
import zimji.hieuboy.oauth2.modules.auth.service.AuthService;
import zimji.hieuboy.oauth2.utils.BeanUtils;
import zimji.hieuboy.oauth2.utils.RequestClientInfo;
import zimji.hieuboy.oauth2.utils.RequestWrapper;
import zimji.hieuboy.oauth2.utils.ResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 22:35
 */

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    private TokenProvider tokenProvider;

    private AuthService authService;

    public TokenAuthenticationFilter() {
    }

    @Autowired
    public TokenAuthenticationFilter(TokenProvider tokenProvider,
                                     AuthService authService) {
        this.tokenProvider = tokenProvider;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, OPTIONS, DELETE");
            httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                    "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, " +
                            "Access-Control-Request-Method, Access-Control-Request-Headers, Authorization, JWTSuperSecretKey");
            RequestWrapper requestWrapper = new RequestWrapper(httpServletRequest);
            ResponseWrapper responseWrapper = new ResponseWrapper(httpServletResponse);
            // Khởi tạo và gán giá trị cho Scope Request
            ScopeRequest scopeRequest = BeanUtils.getBean(ScopeRequest.class);
            scopeRequest.userAgent(RequestClientInfo.getInstance().getUserAgent(requestWrapper));
            scopeRequest.ipAddress(RequestClientInfo.getInstance().getClientIpAddr(requestWrapper));
            scopeRequest.urlRequest(RequestClientInfo.getInstance().getUrlWithQueryString(requestWrapper));
            scopeRequest.urlReferer(RequestClientInfo.getInstance().getReferer(requestWrapper));
            scopeRequest.menuCode(RequestClientInfo.getInstance().getHttpMethodAndUrlWithoutQueryString(requestWrapper));
            scopeRequest.menuAction(httpServletRequest.getMethod());
            // Kiểm tra Api của Swagger
            checkSwagger(requestWrapper);
            if (!checkAuthorizationToken(requestWrapper, scopeRequest)) {
                filterChain.doFilter(requestWrapper, httpServletResponse);
            } else {
                filterChain.doFilter(requestWrapper, responseWrapper);
                String content = responseWrapper.getCaptureAsString();
                if (logger.isInfoEnabled() && !org.apache.commons.lang3.StringUtils.isEmpty(content)) {
                    logger.info(String.format("[LOG_RESPONSE] %s", content));
                }
                if (httpServletResponse.getStatus() < 300) {
                    httpServletResponse.getWriter().write(content);
                }
            }
        } catch (BadCredentialsException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } catch (CredentialsExpiredException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_GONE, e.getMessage());
        } catch (BadRequestException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private boolean checkAuthorizationToken(RequestWrapper request, ScopeRequest scopeRequest) {
        if (request.getMethod().equals(HttpMethod.OPTIONS.toString())) {
            return true;
        }
        if (!request.getServletPath().startsWith("/v1/")) {
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.isAllEmpty(request.getHeader(HttpHeaders.AUTHORIZATION))) {
            throw new BadCredentialsException(String.format("Header [%s] không được trống.", HttpHeaders.AUTHORIZATION));
        }
        String token = request.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
        if ("".equals(token) || tokenProvider.validateToken(token)) {
            throw new BadCredentialsException("Token không được trống.");
        }
        TokenPayloadClaims tokenPayloadClaims = tokenProvider.verifyTokenGetInfo(token);
        if (tokenPayloadClaims == null) {
            throw new BadCredentialsException("Token payload claims không được trống.");
        }
        // Gán các giá trị từ Token Payload Claims vào Scope Request
        scopeRequest.iss(tokenPayloadClaims.iss());
        scopeRequest.exp(tokenPayloadClaims.exp());
        scopeRequest.nbf(tokenPayloadClaims.nbf());
        scopeRequest.iat(tokenPayloadClaims.iat());
        scopeRequest.uid(tokenPayloadClaims.uid());
        scopeRequest.ufn(tokenPayloadClaims.ufn());
        if (!RequestClientInfo.getInstance().getIdentifyDevice(request).equals(scopeRequest.did())) {
            throw new BadCredentialsException("Thiết bị không hợp lệ.");
        }
        UserPrincipal userPrincipal = (UserPrincipal) authService.loadUserById(tokenPayloadClaims.uid());
        if (userPrincipal.getLastPasswordChange() == null || tokenPayloadClaims.lcp() == null
                || userPrincipal.getLastPasswordChange().getTime() != tokenPayloadClaims.lcp()) {
            throw new CredentialsExpiredException("Thông tin đã thay đổi.");
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return true;
    }

    private void checkSwagger(RequestWrapper request) {
        RequestClientInfo requestClientInfo = RequestClientInfo.getInstance();
        if (requestClientInfo.getUrlWithoutQueryString(request).endsWith("swagger-ui.html")) {
            if (StringUtils.trimToEmpty(requestClientInfo.getQueryStringValueByKey(request, "urls.primaryName"))
                    .equals("Authenticate_Public")) {
                return;
            }
            throw new BadRequestException("Api không được công khai.");
        }
        if (requestClientInfo.getUrlWithoutQueryString(request).endsWith("v2/api-docs")) {
            if (StringUtils.trimToEmpty(requestClientInfo.getQueryStringValueByKey(request, "group"))
                    .equals("Authenticate_Public")) {
                return;
            }
            throw new BadRequestException("Api không được công khai.");
        }
    }

}

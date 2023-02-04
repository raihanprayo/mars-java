package dev.scaraz.mars.core.config.interceptor;

import dev.scaraz.mars.core.config.datasource.AuditProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectProvider<AuditProvider> auditProvider;

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            HandlerMethod method = (HandlerMethod) handler;
            String methodName = String.format("%s.%s", method.getBeanType().getSimpleName(), method.getMethod().getName());

            String url = request.getRequestURL().toString();
            if (request.getQueryString() != null)
                url += "?" + request.getQueryString();

            log.info("[{}] INCOMING REQUEST TO {} {} FROM {}",
                    methodName,
                    request.getMethod(),
                    url,
                    getClientIpAddress(request)
            );
        }
        catch (Exception ex) {
            log.info("INCOMING REQUEST TO {}", request.getMethod());
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        auditProvider.ifAvailable(AuditProvider::clear);
    }

    private String getClientIpAddress(HttpServletRequest request) {

        String ip = null;
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                ip = ipList.split(",")[0];
                break;
            }
        }

        if (ip == null)
            ip = request.getRemoteAddr();

        String protocol = request.getProtocol().substring(0, request.getProtocol().indexOf("/")).toLowerCase();
        return String.format("%s://%s", protocol, ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip);
    }
}

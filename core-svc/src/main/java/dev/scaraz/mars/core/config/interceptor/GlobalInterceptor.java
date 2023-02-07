package dev.scaraz.mars.core.config.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class GlobalInterceptor implements HandlerInterceptor {

    private static final ThreadLocal<HttpServletRequest> requestAttr = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> responseAttr = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        requestAttr.set(request);
        responseAttr.set(response);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        responseAttr.remove();
        requestAttr.remove();
    }

    public static HttpServletRequest currentRequest() {
        return Optional.ofNullable(requestAttr.get())
                .orElseThrow(() -> new IllegalStateException("No request bounded to current thread"));
    }

    public static HttpServletResponse currentResponse() {
        return Optional.ofNullable(responseAttr.get())
                .orElseThrow(() -> new IllegalStateException("No response bounded to current thread"));
    }

}

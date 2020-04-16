package com.central.sentinel.config;

import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.WebFluxCallbackManager;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.central.common.model.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * Sentinel配置类
 *
 * @author zlt
 * @date 2019/1/22
 * <p>
 * Blog: https://blog.csdn.net/zlt2000
 * Github: https://github.com/zlt2000
 */
public class SentinelAutoConfigure {
    /**
     * 限流、熔断统一处理类
     */
    @Configuration
    @ConditionalOnClass(HttpServletRequest.class)
    public static class WebmvcHandler {
        public WebmvcHandler(SentinelWebMvcConfig config) {
            config.setBlockExceptionHandler(webmvcBlockExceptionHandler());
        }

        public BlockExceptionHandler webmvcBlockExceptionHandler() {
            return (request, response, e) -> {
                response.setStatus(429);
                Result result = Result.failed(e.getMessage());
                response.getWriter().print(JSONUtil.toJsonStr(result));
            };
        }
    }


    /**
     * 限流、熔断统一处理类
     */
    @Configuration
    @ConditionalOnClass(ServerResponse.class)
    public static class WebfluxHandler {
        public WebfluxHandler() {
            WebFluxCallbackManager.setBlockHandler(webfluxBlockExceptionHandler());
        }

        public BlockRequestHandler webfluxBlockExceptionHandler() {
            return (exchange, t) ->
                    ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(BodyInserters.fromObject(Result.failed(t.getMessage())));
        }
    }
}

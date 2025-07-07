package com.jiawa.train.gateway.config;

import com.jiawa.train.gateway.util.JWTUtilMy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @BelongsProject: yuanql-project-train
 * @BelongsPackage: top.yuanql.train.gateway.config
 * @BelongsClassName: LoginMemberFilter
 * @Author: yuanql
 * @CreateTime: 2023-07-20  15:31
 * @Description: 拦截器
 * @Version: 1.0
 */

@Component
public class LoginMemberFilter implements GlobalFilter, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 排除不需要拦截的请求
        if (path.contains("/admin")
                || path.contains("/redis")
                || path.contains("/hello")
                || path.contains("member/member/login")
                || path.contains("/member/member/send-code")) {
            LOG.info("不需要登录验证：{}", path);
            return chain.filter(exchange);
        }

        LOG.info("需要登录验证：{}", path);

        // 获取header的token参数
        String token = exchange.getRequest().getHeaders().getFirst("token");
        LOG.info("token是: {}", token);
        LOG.info("会员登录验证开始，token：{}", token);
        if (token == null || token.isEmpty()) {
            LOG.info("token为空，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 验证token是否有效，包括token是否被改过，是否过期
        boolean validate = JWTUtilMy.validate(token);
        if (validate) {
            LOG.info("token有效，放行该请求");
            return chain.filter(exchange);
        } else {
            LOG.info("token无效，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return 0; // 用于拍过滤器的顺序
    }
}

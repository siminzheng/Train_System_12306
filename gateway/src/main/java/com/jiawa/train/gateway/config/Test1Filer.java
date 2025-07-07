//package com.jiawa.train.gateway.config;//package top.yuanql.train.gateway.config;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
///**
// * @BelongsProject: yuanql-project-train
// * @BelongsPackage: top.yuanql.train.gateway.config
// * @BelongsClassName: Test1Filer
// * @Author: yuanql
// * @CreateTime: 2023-07-20  15:31
// * @Description: 拦截器
// * @Version: 1.0
// */
//
//@Component
//public class Test1Filer implements GlobalFilter, Ordered {
//
//    private static final Logger LOG = LoggerFactory.getLogger(Test1Filer.class);
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        LOG.info("Test1Filer");
//        return chain.filter(exchange);
//    }
//
//    @Override
//    public int getOrder() {
//        return 0; // 用于拍过滤器的顺序
//    }
//}

# Train_System_12306

# 12306火车调度与售票系统

本项目旨在打造一个高可用、高并发的仿真12306火车调度与售票平台，采用微服务+全栈协同的开发模式，全面覆盖运力调度、余票查询、订单管理、用户认证等核心业务流程，真实还原线上场景。

## 技术栈详解

### 后端

- **Java 17 + Spring Boot 3**：
  - 依托 Spring Boot 3 的自动化配置和约定优于配置，实现快速开发和统一管理。
  - 模块化 Starter 和 Actuator 提供微服务健康检测和监控。
- **Spring Cloud**：
  - **Spring Cloud Gateway** 统一网关层，负责路由转发、限流、鉴权等。
  - **Spring Cloud Eureka** 服务注册与发现，动态管理微服务实例。
  - **Spring Cloud OpenFeign** 简化服务间 HTTP 调用，支持负载均衡和容错。
- **MyBatis**：
  - 采用 XML/注解混合映射，实现灵活的 SQL 定制和复杂查询。
  - 支持分页插件和二级缓存，提升查询性能。
- **Redis缓存 + Spring Cache**：
  - 双层缓存策略：本地 Caffeine + 分布式 Redis，保障高并发场景下的超低延迟读取。
  - Ticketing Service 热点数据（车次信息、余票数量）实时缓存，显著减轻数据库压力。
- **redis分布式锁**：
  - 定义了两种redis分布式锁方法(setnx 与 redisson)，使用了redis分布式锁来同步线程以解决车票超卖的问题。
  - 定义了setnx方法，更简单易用来为线程提供分布式锁，但是没有办法解决线程超时自动销毁的问题
  - 定义了redisson方法，在实现同步线程的同时，使用了redisson提供的watch dog方法来为获得锁的线程自动延时(最终采用)
- **阿里云 RDS MySQL**：
  - 生产级托管数据库服务，自动备份、主从热备、多可用区部署，保障数据安全高可用。
  - 数据库分库分表预留方案，便于后续扩展。
- **.http 文件**：
  - 在 IDE 中使用 HTTP Client 插件（如 VS Code REST Client）直接发起接口请求，覆盖 GET/POST/PUT/DELETE，简化测试流程。

### 前端

- **Node.js + Vue.js 3**：
  - 基于 Composition API 和 Vite 构建开发环境，享受极速热更新体验。
  - 采用 Vue Router 管理路由、Pinia 维护全局状态。
- **Ant Design Vue**：
  - 一致性的 UI 设计语言，丰富的表单、表格、模态框组件，减少二次开发成本。
  - 提供主题定制与按需加载，优化打包体积。

 
### 基础设施 & 运维

- **部署与配置**：
  - 提供针对不同环境（开发、测试、生产）的统一配置模板（`application-*.yml`），可快速切换。

- **扩展与容错**：
  - 后端服务层面支持负载均衡，可与 Nginx、Spring Cloud Gateway 或第三方负载均衡器协同工作。
  - 前端静态资源可部署于 CDN 提升访问速度和可用性；也可与后端 API 网关统一管理。

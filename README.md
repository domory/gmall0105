# gmall0105 本地修改版本


gmall-user-web服务8080
gmall-user-service服务8070


gmall-manage-web服务8081
gmall-manage-service服务8071

gmall-item-web前台商品详情的展示服务8082
#gmall-item-service前台商品详情服务8072

gmall-serach-web 搜索服务的前台8083
gmall-search-service 搜索服务的后台8073

gmall-cart-web 搜索服务的前台8084
gmall-cart-service 搜索服务的后台8074

gmall-passport-web 用户认证中心 8085
gmall-user-service服务8070
#对于一个新的技术需要整合到spring中，1.需要引入pom依赖 2.写一个使用的工具类和spring整合的配置类。(在service-util中创建两个类RedisConfig
#和RedisUtil，RedisConfig负责在spring容器启动时自动注入，而RedisUtil就是被注入的工具类以供其他模块调用。任何模块想要调用redis都必须在
#application.properties配置，否则不会进行注入)
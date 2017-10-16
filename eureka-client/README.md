Eureka-Server ：通过 REST 协议暴露服务，提供应用服务的注册和发现的功能。
Application Provider ：应用服务提供者，内嵌 Eureka-Client，通过它向 Eureka-Server 注册自身服务。
Application Consumer ：应用服务消费者，内嵌 Eureka-Client，通过它从 Eureka-Server 获取服务列表。
请注意下，Application Provider 和 Application Consumer 强调扮演的角色，实际可以在同一 JVM 进程，
即是服务的提供者，又是服务的消费者。

com.netflix.appinfo 包：Eureka-Client 的应用配置。此处的应用指的就是上文提到的 Application Provider，Application Consumer


com.netflix.discovery 包：Eureka-Client 的注册与发现相关功能。

com.netflix.discovery.DiscoveryClient 类：注册发现客户端实现类。

com.netflix.discovery.guice 包：Eureka 计划使用 Google Guice 实现依赖注入。
一方面 Guice 是轻量级的依赖注入框架，另一方面避免和业务代码的 Spring 版本冲突。

com.netflix.discovery.converters 包：Eureka 内部传输数据编解码转换器，支持 XML / JSON 格式。

com.netflix.discovery.endpoint 包：目前该包正在重构，和下文的 
com.netflix.discovery.shared.dns 和 com.netflix.discovery.shared.resolver 用途相近。

com.netflix.disvoery.provider 包：目前仅有 DiscoveryJerseyProvider 类。
该类声明自定义的 Jersey 请求和响应的序列化和反序列化实现。

com.netflix.disvoery.providers 包：目前仅有 DefaultEurekaClientConfigProvider 类。
该类实现 javax.inject.Provider 接口，设置 EurekaClientConfig ( Eureka 客户端配置 ) 的生成工厂。

com.netflix.discovery.shared 包：Eureka-Client 和 Eureka-Server 注册发现相关的共享重用的代码。
Eureka-Server 通过 eureka-core 模块实现，eureka-core 依赖 eureka-client。
Eureka-Server 代码依赖 Eureka-Client 代码！？这个和 Eureka-Server 多节点注册信息 P2P 同步的实现有关。
一个 Eureka-Server 收到 Eureka-Client 注册请求后，Eureka-Server 会自己模拟 Eureka-Client 发送注册请求到其它的 Eureka-Server，
因此部分实现代码就使用到了这个包。

com.netflix.discovery.shared.transport 包：Eureka-Client 对 Eureka-Server RESTful 的 HTTP 客户端，基于 Jersey Client 实现。

com.netflix.discovery.shared.dns 包 ：DNS 解析器。

com.netflix.discovery.shared.resolver 包：EurekaEndpoint 解析器。

com.netflix.discovery.util 包 ：工具类。


1. 创建 EurekaInstanceConfig对象
2. 使用 EurekaInstanceConfig对象 创建 InstanceInfo对象
3. 使用 EurekaInstanceConfig对象 + InstanceInfo对象 创建 ApplicationInfoManager对象
4. 创建 EurekaClientConfig对象
5. 使用 ApplicationInfoManager对象 + EurekaClientConfig对象 创建 EurekaClient对象
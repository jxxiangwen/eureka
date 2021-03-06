/**
 * Eureka-Client 和 Eureka-Server 注册发现相关的共享重用的代码。
 * Eureka-Server 通过 eureka-core 模块实现，eureka-core 依赖 eureka-client。
 * Eureka-Server 代码依赖 Eureka-Client 代码！？这个和 Eureka-Server 多节点注册信息 P2P 同步的实现有关。
 * 一个 Eureka-Server 收到 Eureka-Client 注册请求后，Eureka-Server 会自己模拟
 * Eureka-Client 发送注册请求到其它的 Eureka-Server，
 * 因此部分实现代码就使用到了这个包。
 *
 * @author jxxiangwen
 * mail: :xiangwen.zou@ymm56.com
 * Time: 17-10-16 下午4:55.
 */
package com.netflix.discovery.shared;
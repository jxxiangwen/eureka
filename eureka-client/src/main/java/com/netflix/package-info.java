/**
 * Eureka-Server ：通过 REST 协议暴露服务，提供应用服务的注册和发现的功能。
 * Application Provider ：应用服务提供者，内嵌 Eureka-Client，通过它向 Eureka-Server 注册自身服务。
 * Application Consumer ：应用服务消费者，内嵌 Eureka-Client，通过它从 Eureka-Server 获取服务列表。
 * 请注意下，Application Provider 和 Application Consumer 强调扮演的角色，实际可以在同一 JVM 进程，
 * 即是服务的提供者，又是服务的消费者。
 *
 * @author jxxiangwen
 * mail: :xiangwen.zou@ymm56.com
 * Time: 17-10-16 下午4:56.
 */
package com.netflix;
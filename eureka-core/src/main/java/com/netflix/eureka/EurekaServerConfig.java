/*
 * Copyright 2012 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.netflix.eureka;

import com.netflix.eureka.aws.AwsBindingStrategy;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Configuration information required by the eureka server to operate.
 * <p>
 * <p>
 * Most of the required information is provided by the default configuration
 * {@link com.netflix.eureka.DefaultEurekaServerConfig}.
 * <p>
 * Note that all configurations are not effective at runtime unless and
 * otherwise specified.
 * </p>
 *
 * @author Karthik Ranganathan
 */
public interface EurekaServerConfig {

    /**
     * Gets the <em>AWS Access Id</em>. This is primarily used for
     * <em>Elastic IP Biding</em>. The access id should be provided with
     * appropriate AWS permissions to bind the EIP.
     *
     * @return
     */
    // 跳过：AWS 使用
    String getAWSAccessId();

    /**
     * Gets the <em>AWS Secret Key</em>. This is primarily used for
     * <em>Elastic IP Biding</em>. The access id should be provided with
     * appropriate AWS permissions to bind the EIP.
     *
     * @return
     */
    // 跳过：AWS 使用
    String getAWSSecretKey();

    /**
     * Gets the number of times the server should try to bind to the candidate
     * EIP.
     * <p>
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return the number of times the server should try to bind to the
     * candidate EIP.
     */
    // 跳过：AWS 使用
    int getEIPBindRebindRetries();

    /**
     * Get the interval with which the server should check if the EIP is bound
     * and should try to bind in the case if it is already not bound, iff the EIP
     * is not currently bound.
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return the time in milliseconds.
     */
    // 跳过：AWS 使用
    int getEIPBindingRetryIntervalMsWhenUnbound();

    /**
     * Gets the interval with which the server should check if the EIP is bound
     * and should try to bind in the case if it is already not bound, iff the EIP
     * is already bound. (so this refresh is just for steady state checks)
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return the time in milliseconds.
     */
    // 跳过：AWS 使用
    int getEIPBindingRetryIntervalMs();

    /**
     * Checks to see if the eureka server is enabled for self preservation.
     * <p>
     * <p>
     * When enabled, the server keeps track of the number of <em>renewals</em>
     * it should receive from the server. Any time, the number of renewals drops
     * below the threshold percentage as defined by
     * {@link #getRenewalPercentThreshold()}, the server turns off expirations
     * to avert danger.This will help the server in maintaining the registry
     * information in case of network problems between client and the server.
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return true to enable self preservation, false otherwise.
     */
    // 是否开启自我保护模式。
    // 当Eureka Server节点在短时间内丢失过多客户端时（可能发生了网络分区故障），那么这个节点就会进入自我保护模式。
    // 一旦进入该模式，Eureka Server就会保护服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务）。
    // 当网络故障恢复后，该Eureka Server节点会自动退出自我保护模式。
    boolean shouldEnableSelfPreservation();

    /**
     * The minimum percentage of renewals that is expected from the clients in
     * the period specified by {@link #getRenewalThresholdUpdateIntervalMs()}.
     * If the renewals drop below the threshold, the expirations are disabled if
     * the {@link #shouldEnableSelfPreservation()} is enabled.
     * <p>
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return value between 0 and 1 indicating the percentage. For example,
     * <code>85%</code> will be specified as <code>0.85</code>.
     */
    // 开启自我保护模式比例，超过该比例后开启自我保护模式。
    // 0 - 1 之间
    double getRenewalPercentThreshold();

    /**
     * The interval with which the threshold as specified in
     * {@link #getRenewalPercentThreshold()} needs to be updated.
     *
     * @return time in milliseconds indicating the interval.
     */
    // 自我保护模式比例更新频率，单位：毫秒。
    int getRenewalThresholdUpdateIntervalMs();

    /**
     * The interval with which the information about the changes in peer eureka
     * nodes is updated. The user can use the DNS mechanism or dynamic
     * configuration provided by <a href="https://github.com/Netflix/archaius">Archaius</a> to
     * change the information dynamically.
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return timer in milliseconds indicating the interval.
     */
    // Eureka-Server 集群节点更新频率，单位：毫秒。
    int getPeerEurekaNodesUpdateIntervalMs();

    /**
     * If set to true, the replicated data send in the request will be always compressed.
     * This does not define response path, which is driven by "Accept-Encoding" header.
     */
    // 是否开启 Eureka-Server 集群间请求压缩
    boolean shouldEnableReplicatedRequestCompression();

    /**
     * Get the number of times the replication events should be retried with
     * peers.
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return the number of retries.
     */
    // 复制事件重试次数
    int getNumberOfReplicationRetries();

    /**
     * Gets the interval with which the status information about peer nodes is
     * updated.
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return time in milliseconds indicating the interval.
     */
    int getPeerEurekaStatusRefreshTimeIntervalMs();

    /**
     * Gets the time to wait when the eureka server starts up unable to get
     * instances from peer nodes. It is better not to start serving rightaway
     * during these scenarios as the information that is stored in the registry
     * may not be complete.
     * <p>
     * When the instance registry starts up empty, it builds over time when the
     * clients start to send heartbeats and the server requests the clients for
     * registration information.
     *
     * @return time in milliseconds.
     */
    // Eureka-Server 启动时，从远程 Eureka-Server 读取不到注册信息时，多长时间不允许 Eureka-Client 访问。
    int getWaitTimeInMsWhenSyncEmpty();

    /**
     * Gets the timeout value for connecting to peer eureka nodes for
     * replication.
     *
     * @return timeout value in milliseconds.
     */
    // 远程 Eureka-Server 请求连接超时时间，单位：毫秒
    int getPeerNodeConnectTimeoutMs();

    /**
     * Gets the timeout value for reading information from peer eureka nodes for
     * replication.
     *
     * @return timeout value in milliseconds.
     */
    // 远程 Eureka-Server 请求读取超时时间，单位：毫秒
    int getPeerNodeReadTimeoutMs();

    /**
     * Gets the total number of <em>HTTP</em> connections allowed to peer eureka
     * nodes for replication.
     *
     * @return total number of allowed <em>HTTP</em> connections.
     */
    // 全部远程 Eureka-Server 请求总连接数
    int getPeerNodeTotalConnections();

    /**
     * Gets the total number of <em>HTTP</em> connections allowed to a
     * particular peer eureka node for replication.
     *
     * @return total number of allowed <em>HTTP</em> connections for a peer
     * node.
     */
    // 单个远程 Eureka-Server 请求总连接数
    int getPeerNodeTotalConnectionsPerHost();

    /**
     * Gets the idle time after which the <em>HTTP</em> connection should be
     * cleaned up.
     *
     * @return idle time in seconds.
     */
    // 远程 Eureka-Server 请求空闲超时时间，单位：毫秒
    int getPeerNodeConnectionIdleTimeoutSeconds();

    /**
     * Get the time for which the delta information should be cached for the
     * clients to retrieve the value without missing it.
     *
     * @return time in milliseconds
     */
    long getRetentionTimeInMSInDeltaQueue();

    /**
     * Get the time interval with which the clean up task should wake up and
     * check for expired delta information.
     *
     * @return time in milliseconds.
     */
    long getDeltaRetentionTimerIntervalInMs();

    /**
     * Get the time interval with which the task that expires instances should
     * wake up and run.
     *
     * @return time in milliseconds.
     */
    // 租约过期定时任务执行频率，单位：毫秒。
    long getEvictionIntervalTimerInMs();

    /**
     * Get the timeout value for querying the <em>AWS</em> for <em>ASG</em>
     * information.
     *
     * @return timeout value in milliseconds.
     */
    // 跳过：AWS 使用
    int getASGQueryTimeoutMs();

    /**
     * Get the time interval with which the <em>ASG</em> information must be
     * queried from <em>AWS</em>.
     *
     * @return time in milliseconds.
     */
    // 跳过：AWS 使用
    long getASGUpdateIntervalMs();

    /**
     * Get the expiration value for the cached <em>ASG</em> information
     *
     * @return time in milliseconds.
     */
    // 跳过：AWS 使用
    long getASGCacheExpiryTimeoutMs();

    /**
     * Gets the time for which the registry payload should be kept in the cache
     * if it is not invalidated by change events.
     *
     * @return time in seconds.
     */
    // 读写缓存写入后过期时间，单位：秒。
    long getResponseCacheAutoExpirationInSeconds();

    /**
     * Gets the time interval with which the payload cache of the client should
     * be updated.
     *
     * @return time in milliseconds.
     */
    // 只读缓存更新频率，单位：毫秒。
    // 只读缓存定时更新任务只更新读取过请求 (com.netflix.eureka.registry.Key)，
    // 因此虽然永不过期，也会存在读取不到的情况。
    long getResponseCacheUpdateIntervalMs();

    /**
     * The {@link com.netflix.eureka.registry.ResponseCache} currently uses a two level caching
     * strategy to responses. A readWrite cache with an expiration policy, and a readonly cache
     * that caches without expiry.
     *
     * @return true if the read only cache is to be used
     */
    // 否开启只读请求响应缓存。
    // 响应缓存 ( ResponseCache ) 机制目前使用两层缓存策略。
    // 优先读取永不过期的只读缓存，读取不到后读取固定过期的读写缓存。
    boolean shouldUseReadOnlyResponseCache();

    /**
     * Checks to see if the delta information can be served to client or not.
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return true if the delta information is allowed to be served, false
     * otherwise.
     */
    boolean shouldDisableDelta();

    /**
     * Get the idle time for which the status replication threads can stay
     * alive.
     *
     * @return time in minutes.
     */
    long getMaxIdleThreadInMinutesAgeForStatusReplication();

    /**
     * Get the minimum number of threads to be used for status replication.
     *
     * @return minimum number of threads to be used for status replication.
     */
    int getMinThreadsForStatusReplication();

    /**
     * Get the maximum number of threads to be used for status replication.
     *
     * @return maximum number of threads to be used for status replication.
     */
    // 同步应用实例状态最大线程数。
    int getMaxThreadsForStatusReplication();

    /**
     * Get the maximum number of replication events that can be allowed to back
     * up in the status replication pool.
     * <p>
     * Depending on the memory allowed, timeout and the replication traffic,
     * this value can vary.
     * </p>
     *
     * @return the maximum number of replication events that can be allowed to
     * back up.
     */
    // 待执行同步应用实例状态事件缓冲最大数量
    int getMaxElementsInStatusReplicationPool();

    /**
     * Checks whether to synchronize instances when timestamp differs.
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return true, to synchronize, false otherwise.
     */
    // 是否同步应用实例信息，当应用实例信息最后更新时间戳( lastDirtyTimestamp )发生改变。
    boolean shouldSyncWhenTimestampDiffers();

    /**
     * Get the number of times that a eureka node would try to get the registry
     * information from the peers during startup.
     *
     * @return the number of retries
     */
    // Eureka-Server 启动时，从远程 Eureka-Server 读取失败重试次数。
    int getRegistrySyncRetries();

    /**
     * Get the wait/sleep time between each retry sync attempts, if the prev retry failed and there are
     * more retries to attempt.
     *
     * @return the wait time in ms between each sync retries
     */
    // Eureka-Server 启动时，从远程 Eureka-Server 读取失败等待( sleep )间隔，单位：毫秒。
    long getRegistrySyncRetryWaitMs();

    /**
     * Get the maximum number of replication events that can be allowed to back
     * up in the replication pool. This replication pool is responsible for all
     * events except status updates.
     * <p>
     * Depending on the memory allowed, timeout and the replication traffic,
     * this value can vary.
     * </p>
     *
     * @return the maximum number of replication events that can be allowed to
     * back up.
     */
    // 待执行同步应用实例信息事件缓冲最大数量
    int getMaxElementsInPeerReplicationPool();

    /**
     * Get the idle time for which the replication threads can stay alive.
     *
     * @return time in minutes.
     */
    long getMaxIdleThreadAgeInMinutesForPeerReplication();

    /**
     * Get the minimum number of threads to be used for replication.
     *
     * @return minimum number of threads to be used for replication.
     */
    // 同步应用实例信息最小线程数
    int getMinThreadsForPeerReplication();

    /**
     * Get the maximum number of threads to be used for replication.
     *
     * @return maximum number of threads to be used for replication.
     */
    // 同步应用实例信息最大线程数
    int getMaxThreadsForPeerReplication();

    /**
     * Get the minimum number of available peer replication instances
     * for this instance to be considered healthy. The design of eureka allows
     * for an instance to continue operating with zero peers, but that would not
     * be ideal.
     * <p>
     * The default value of -1 is interpreted as a marker to not compare
     * the number of replicas. This would be done to either disable this check
     * or to run eureka in a single node configuration.
     *
     * @return minimum number of available peer replication instances
     * for this instance to be considered healthy.
     */
    int getHealthStatusMinNumberOfAvailablePeers();

    /**
     * Get the time in milliseconds to try to replicate before dropping
     * replication events.
     *
     * @return time in milliseconds
     */
    // 执行单个同步应用实例信息状态任务最大时间
    int getMaxTimeForReplication();

    /**
     * Checks whether the connections to replicas should be primed. In AWS, the
     * firewall requires sometime to establish network connection for new nodes.
     *
     * @return true, if connections should be primed, false otherwise.
     */
    boolean shouldPrimeAwsReplicaConnections();

    /**
     * Checks to see if the delta information can be served to client or not for
     * remote regions.
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return true if the delta information is allowed to be served, false
     * otherwise.
     */
    boolean shouldDisableDeltaForRemoteRegions();

    /**
     * Gets the timeout value for connecting to peer eureka nodes for remote
     * regions.
     *
     * @return timeout value in milliseconds.
     */
    // 远程 Eureka-Server 请求连接超时时间，单位：毫秒
    int getRemoteRegionConnectTimeoutMs();

    /**
     * Gets the timeout value for reading information from peer eureka nodes for
     * remote regions.
     *
     * @return timeout value in milliseconds.
     */
    // 远程 Eureka-Server 请求读取超时时间，单位：毫秒
    int getRemoteRegionReadTimeoutMs();

    /**
     * Gets the total number of <em>HTTP</em> connections allowed to peer eureka
     * nodes for remote regions.
     *
     * @return total number of allowed <em>HTTP</em> connections.
     */
    // 所有远程 Eureka-Server 请求总连接数
    int getRemoteRegionTotalConnections();

    /**
     * Gets the total number of <em>HTTP</em> connections allowed to a
     * particular peer eureka node for remote regions.
     *
     * @return total number of allowed <em>HTTP</em> connections for a peer
     * node.
     */
    // 单个远程 Eureka-Server 请求总连接数
    int getRemoteRegionTotalConnectionsPerHost();

    /**
     * Gets the idle time after which the <em>HTTP</em> connection should be
     * cleaned up for remote regions.
     *
     * @return idle time in seconds.
     */
    // 远程 Eureka-Server 请求空闲超时时间，单位：毫秒
    int getRemoteRegionConnectionIdleTimeoutSeconds();

    /**
     * Indicates whether the content fetched from eureka server has to be
     * compressed for remote regions whenever it is supported by the server. The
     * registry information from the eureka server is compressed for optimum
     * network traffic.
     *
     * @return true, if the content need to be compressed, false otherwise.
     */
    // 是否 GZIP 解压 从远程 Eureka-Server 的内容
    boolean shouldGZipContentFromRemoteRegion();

    /**
     * Get a map of region name against remote region discovery url.
     *
     * @return - An unmodifiable map of remote region name against remote region discovery url. Empty map if no remote
     * region url is defined.
     */
    // 远程 Eureka-Server 映射
    // key：Eureka-Server 名
    // value：Eureka-Server 地址
    Map<String, String> getRemoteRegionUrlsWithName();

    /**
     * Get the list of remote region urls.
     *
     * @return - array of string representing {@link java.net.URL}s.
     * @deprecated Use {@link #getRemoteRegionUrlsWithName()}
     */
    String[] getRemoteRegionUrls();

    /**
     * Returns a list of applications that must be retrieved from the passed remote region. <br/>
     * This list can be <code>null</code> which means that no filtering should be applied on the applications
     * for this region i.e. all applications must be returned. <br/>
     * A global whitelist can also be configured which can be used when no setting is available for a region, such a
     * whitelist can be obtained by passing <code>null</code> to this method.
     *
     * @param regionName Name of the region for which the application whitelist is to be retrieved. If null a global
     *                   setting is returned.
     * @return A set of application names which must be retrieved from the passed region. If <code>null</code> all
     * applications must be retrieved.
     */
    // 从远程 Eureka-Server 拉取应用注册信息集合。
    @Nullable
    Set<String> getRemoteRegionAppWhitelist(@Nullable String regionName);

    /**
     * Get the time interval for which the registry information need to be fetched from the remote region.
     *
     * @return time in seconds.
     */
    // 远程 Eureka-Server 拉取注册信息的间隔，单位：秒
    int getRemoteRegionRegistryFetchInterval();

    /**
     * Size of a thread pool used to execute remote region registry fetch requests. Delegating these requests
     * to internal threads is necessary workaround to https://bugs.openjdk.java.net/browse/JDK-8049846 bug.
     */
    // 远程 Eureka-Server 拉取注册信息的线程池大小
    int getRemoteRegionFetchThreadPoolSize();

    /**
     * Gets the fully qualified trust store file that will be used for remote region registry fetches.
     *
     * @return
     */
    // 远程 Eureka-Server 信任存储文件
    String getRemoteRegionTrustStore();

    /**
     * Get the remote region trust store's password.
     */
    // 远程 Eureka-Server 信任存储文件的密码
    String getRemoteRegionTrustStorePassword();

    /**
     * Old behavior of fallback to applications in the remote region (if configured) if there are no instances of that
     * application in the local region, will be disabled.
     *
     * @return {@code true} if the old behavior is to be disabled.
     */
    // 是否禁用本地读取不到注册信息，从远程 Eureka-Server 读取。
    boolean disableTransparentFallbackToOtherRegion();

    /**
     * Indicates whether the replication between cluster nodes should be batched for network efficiency.
     *
     * @return {@code true} if the replication needs to be batched.
     */
    boolean shouldBatchReplication();

    /**
     * Indicates whether the eureka server should log/metric clientAuthHeaders
     *
     * @return {@code true} if the clientAuthHeaders should be logged and/or emitted as metrics
     */
    // 打印访问的客户端名和版本号，配合 Netflix Servo 实现监控信息采集。
    boolean shouldLogIdentityHeaders();

    /**
     * Indicates whether the rate limiter should be enabled or disabled.
     */
    // 请求限流是否开启
    boolean isRateLimiterEnabled();

    /**
     * Indicate if rate limit standard clients. If set to false, only non standard clients
     * will be rate limited.
     */
    // 是否限制非标准客户端的访问。
    // 标准客户端通过请求头( header )的 "DiscoveryIdentity-Name" 来判断，是否在标准客户端名集合里。
    boolean isRateLimiterThrottleStandardClients();

    /**
     * A list of certified clients. This is in addition to standard eureka Java clients.
     */
    // 标准客户端名集合
    // 标准客户端名集合。默认包含"DefaultClient" 和 "DefaultServer" 。
    Set<String> getRateLimiterPrivilegedClients();

    /**
     * Rate limiter, token bucket algorithm property. See also {@link #getRateLimiterRegistryFetchAverageRate()}
     * and {@link #getRateLimiterFullFetchAverageRate()}.
     */
    // 速率限制的 burst size ，使用令牌桶算法。
    int getRateLimiterBurstSize();

    /**
     * Rate limiter, token bucket algorithm property. Specifies the average enforced request rate.
     * See also {@link #getRateLimiterBurstSize()}.
     */
    // 增量拉取注册信息的速率限制
    int getRateLimiterRegistryFetchAverageRate();

    /**
     * Rate limiter, token bucket algorithm property. Specifies the average enforced request rate.
     * See also {@link #getRateLimiterBurstSize()}.
     */
    // 全量拉取注册信息的速率限制
    int getRateLimiterFullFetchAverageRate();

    /**
     * Name of the Role used to describe auto scaling groups from third AWS accounts.
     */
    String getListAutoScalingGroupsRoleName();

    /**
     * @return the class name of the full json codec to use for the server. If none set a default codec will be used
     */
    // JSON 编解码器名
    String getJsonCodecName();

    /**
     * @return the class name of the full xml codec to use for the server. If none set a default codec will be used
     */
    // XML 编解码器名
    String getXmlCodecName();

    /**
     * Get the configured binding strategy EIP or Route53.
     *
     * @return the configured binding strategy
     */
    AwsBindingStrategy getBindingStrategy();

    /**
     * @return the ttl used to set up the route53 domain if new
     */
    long getRoute53DomainTTL();

    /**
     * Gets the number of times the server should try to bind to the candidate
     * Route53 domain.
     * <p>
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return the number of times the server should try to bind to the
     * candidate Route53 domain.
     */
    int getRoute53BindRebindRetries();

    /**
     * Gets the interval with which the server should check if the Route53 domain is bound
     * and should try to bind in the case if it is already not bound.
     * <p>
     * <em>The changes are effective at runtime.</em>
     * </p>
     *
     * @return the time in milliseconds.
     */
    int getRoute53BindingRetryIntervalMs();

    /**
     * To avoid configuration API pollution when trying new/experimental or features or for the migration process,
     * the corresponding configuration can be put into experimental configuration section.
     *
     * @return a property of experimental feature
     */
    // 获得实验性属性值
    String getExperimental(String name);
}

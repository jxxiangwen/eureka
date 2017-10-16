/**
 * 
 */
package com.netflix.discovery;

/**
 * Class to be extended by all discovery events. Abstract as it
 * doesn't make sense for generic events to be published directly.
 */
// 记录事件发生时间
public abstract class DiscoveryEvent implements EurekaEvent {

    // 事件发生时间
    // System time when the event happened
    private final long timestamp;
    
    protected DiscoveryEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * @return Return the system time in milliseconds when the event happened.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }
}

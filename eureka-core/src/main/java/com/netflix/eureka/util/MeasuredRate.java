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
package com.netflix.eureka.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for getting a count in last X milliseconds.
 *
 * @author Karthik Ranganathan,Greg Kim
 */

/**
 * 续租每分钟次数
 * 配合 Netflix Servo 实现监控信息采集续租每分钟次数。
 * Eureka-Server 运维界面的显示续租每分钟次数。
 */
public class MeasuredRate {
    private static final Logger logger = LoggerFactory.getLogger(MeasuredRate.class);
    /**
     * 上一个间隔次数
     */
    private final AtomicLong lastBucket = new AtomicLong(0);
    /**
     * 当前间隔次数
     */
    private final AtomicLong currentBucket = new AtomicLong(0);
    /**
     * 间隔
     */
    private final long sampleInterval;
    /**
     * 定时器,负责每个 sampleInterval 间隔重置当前次数( currentBucket )，
     * 并将原当前次数设置到上一个次数( lastBucket )。
     */
    private final Timer timer;

    private volatile boolean isActive;

    /**
     * @param sampleInterval in milliseconds
     */
    public MeasuredRate(long sampleInterval) {
        this.sampleInterval = sampleInterval;
        this.timer = new Timer("Eureka-MeasureRateTimer", true);
        this.isActive = false;
    }

    public synchronized void start() {
        if (!isActive) {
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    try {
                        // Zero out the current bucket.
                        lastBucket.set(currentBucket.getAndSet(0));
                    } catch (Throwable e) {
                        logger.error("Cannot reset the Measured Rate", e);
                    }
                }
            }, sampleInterval, sampleInterval);

            isActive = true;
        }
    }

    public synchronized void stop() {
        if (isActive) {
            timer.cancel();
            isActive = false;
        }
    }

    /**
     * Returns the count in the last sample interval.
     * 返回上一个次数
     */
    public long getCount() {
        return lastBucket.get();
    }

    /**
     * Increments the count in the current sample interval.
     * 返回当前次数
     */
    public void increment() {
        currentBucket.incrementAndGet();
    }
}

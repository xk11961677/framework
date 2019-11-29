/*
 * The MIT License (MIT)
 * Copyright © 2019 <sky>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sky.framework.rpc.monitor;


import io.micrometer.core.instrument.*;
import io.micrometer.jmx.JmxConfig;
import io.micrometer.jmx.JmxMeterRegistry;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author
 */
public class MetricsMonitor {

    private static final String MONITOR_NAME = "sky-framework-rpc-monitor";

    @Getter
    private static AtomicInteger serverChannelCount = new AtomicInteger();

    static {
        JmxMeterRegistry jmxMeterRegistry = jmxMeterRegistry();
        Metrics.addRegistry(jmxMeterRegistry);

        List<Tag> tags = new ArrayList<Tag>();

        tags = new ArrayList<Tag>();
        tags.add(new ImmutableTag("module", "remoting"));
        tags.add(new ImmutableTag("name", "serverChannelCount"));
        Metrics.gauge(MONITOR_NAME, tags, serverChannelCount);

        /*
        tags = new ArrayList<Tag>();
        tags.add(new ImmutableTag("module", "remoting"));
        tags.add(new ImmutableTag("name", "clientChannelCount"));
        Metrics.gauge(MONITOR_NAME, tags, clientChannelCount);
        */

    }

    private static JmxMeterRegistry jmxMeterRegistry() {
        return new JmxMeterRegistry(JmxConfig.DEFAULT, Clock.SYSTEM);
    }

    public static Counter getServerChannelCounter() {
        return Metrics.counter("rpc_channel_server",
                "module", "remoting", "name", "serverChannelCount");
    }
}

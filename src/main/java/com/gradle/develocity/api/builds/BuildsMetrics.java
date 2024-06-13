package com.gradle.develocity.api.builds;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BuildsMetrics {

    private final AtomicInteger buildCount = new AtomicInteger(0);
    private final AtomicLong buildOverallDurationMs = new AtomicLong(0);

    public void addBuild(long buildDurationMs) {
        buildCount.incrementAndGet();
        buildOverallDurationMs.addAndGet(buildDurationMs);
    }

    public int getBuildCount() {
        return buildCount.get();
    }

    public long getBuildOverallDuration() {
        return buildOverallDurationMs.get();
    }
}

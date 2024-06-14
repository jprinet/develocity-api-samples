package com.gradle.develocity.api.builds;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

final class BuildsMetrics {

    private final AtomicInteger buildCount = new AtomicInteger(0);
    private final AtomicLong buildOverallDurationInMs = new AtomicLong(0);

    public void addBuild(long buildDurationInMs) {
        buildCount.incrementAndGet();
        buildOverallDurationInMs.addAndGet(buildDurationInMs);
    }

    public int getBuildCount() {
        return buildCount.get();
    }

    public long getBuildOverallDurationInMs() {
        return buildOverallDurationInMs.get();
    }
}

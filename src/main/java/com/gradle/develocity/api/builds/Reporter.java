package com.gradle.develocity.api.builds;

import java.time.Duration;

public class Reporter {

    private final String query;
    private final BuildsMetrics buildsMetrics;

    Reporter(String query, BuildsMetrics buildsMetrics) {
        this.query = query;
        this.buildsMetrics = buildsMetrics;
    }

    private String getHumanReadableDuration(long durationInMs) {
        return Duration.ofMillis(durationInMs).toString();
    }

    void report() {
        System.out.println("----------------------");
        System.out.println("Query: " + query);
        System.out.println("----------------------");
        System.out.println("Builds processed: " + buildsMetrics.getBuildCount());
        System.out.println("Overall build duration: " + getHumanReadableDuration(buildsMetrics.getBuildOverallDurationInMs()));
        System.out.println("Average build duration: " + getHumanReadableDuration(buildsMetrics.getBuildOverallDurationInMs() / buildsMetrics.getBuildCount()));
    }
}

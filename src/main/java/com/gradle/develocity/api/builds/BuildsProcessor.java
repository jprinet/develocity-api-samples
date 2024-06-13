package com.gradle.develocity.api.builds;

import com.gradle.enterprise.api.GradleEnterpriseApi;
import com.gradle.enterprise.api.client.ApiException;
import com.gradle.enterprise.api.model.*;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

final class BuildsProcessor {

    private final GradleEnterpriseApi api;
    private final BuildProcessor buildProcessor;
    private final int maxBuilds;
    private final String startTime;
    private final String endTime;
    private final List<String> tags;

    BuildsProcessor(GradleEnterpriseApi api, BuildProcessor buildProcessor, int maxBuilds, String startTime, String endTime, List<String> tags) {
        this.api = api;
        this.buildProcessor = buildProcessor;
        this.maxBuilds = maxBuilds;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tags = tags;
    }

    // API overall build time by tag, by time window
    void process() throws ApiException {
        BuildsMetrics buildsMetrics = new BuildsMetrics();

        String query = getQuery();
        System.out.println("Query: " + query);

        BuildsQuery buildsQuery = new BuildsQuery();
        buildsQuery.setFromInstant(0L);
        buildsQuery.setQuery(query);
        buildsQuery.setMaxBuilds(maxBuilds);
        buildsQuery.setModels(Collections.singletonList(BuildModelName.GRADLE_ATTRIBUTES));

        List<Build> builds = api.getBuilds(buildsQuery);
        builds.forEach(build -> buildProcessor.process(build, buildsMetrics));

        System.out.println("----------------------");
        System.out.println("----------------------");
        System.out.println("Builds processed: " + buildsMetrics.getBuildCount());
        Duration overallDuration = Duration.ofMillis(buildsMetrics.getBuildOverallDuration());
        Duration avgDuration = Duration.ofMillis(buildsMetrics.getBuildOverallDuration() / buildsMetrics.getBuildCount());
        System.out.println("Overall build duration: " + overallDuration);
        System.out.println("Average build duration: " + avgDuration);
    }

    private String getQuery() {
        return String.format("buildTool:gradle buildStartTime:[%s to %s] %s", startTime, endTime, getTagsAsString());
    }

    private String getTagsAsString() {
        StringBuilder tagsAsString = new StringBuilder();
        for(String tag : tags) {
            tagsAsString.append("tag:").append(tag).append(" ");
        }
        return tagsAsString.toString();
    }

    public static void main(String[] args) {

    }
}

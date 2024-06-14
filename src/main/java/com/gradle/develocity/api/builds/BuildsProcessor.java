package com.gradle.develocity.api.builds;

import com.gradle.enterprise.api.GradleEnterpriseApi;
import com.gradle.enterprise.api.client.ApiException;
import com.gradle.enterprise.api.model.*;

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
        String query = getQuery();
        System.out.println("Query: " + query);
        System.out.println("Max build = " + maxBuilds);

        BuildsMetrics buildsMetrics = new BuildsMetrics();
        BuildsQuery buildsQuery = new BuildsQuery();
        buildsQuery.setFromInstant(0L);
        buildsQuery.setQuery(query);
        buildsQuery.setMaxBuilds(1000);
        buildsQuery.setModels(Collections.singletonList(BuildModelName.GRADLE_ATTRIBUTES));

        int queryCount = 1;
        while(buildsMetrics.getBuildCount() < maxBuilds) {
            // Log current query state
            System.out.println("----------------------");
            System.out.println("Running query " + queryCount++);
            if(buildsQuery.getFromBuild() != null) System.out.println("From build = " + buildsQuery.getFromBuild());

            // collect builds
            List<Build> builds = api.getBuilds(buildsQuery);
            System.out.println("----------------------");
            int currentBuildCount = builds.size();
            System.out.println("Retrieved " + currentBuildCount + " builds");
            if(currentBuildCount == 0) {
                System.out.println("No more builds to collect");
                break;
            }

            String lastBuildId = builds.get(builds.size() - 1).getId();

            // Trim the list of builds to the maximum number of builds
            if(buildsMetrics.getBuildCount() + currentBuildCount > maxBuilds) {
                int nbBuildsToCollect = maxBuilds - buildsMetrics.getBuildCount();
                System.out.println("Trim current list to " + nbBuildsToCollect + " first builds");
                builds = builds.subList(0, nbBuildsToCollect);
            }

            // process builds
            builds.forEach(build -> buildProcessor.process(build, buildsMetrics));

            // prepare for the next query
            buildsQuery.setFromBuild(lastBuildId);
        }

        new Reporter(query, buildsMetrics).report();
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
}

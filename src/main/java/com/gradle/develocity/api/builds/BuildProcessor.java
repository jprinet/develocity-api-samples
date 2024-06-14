package com.gradle.develocity.api.builds;

import com.gradle.enterprise.api.GradleEnterpriseApi;
import com.gradle.enterprise.api.client.ApiException;
import com.gradle.enterprise.api.model.*;

import java.time.Duration;
import java.time.Instant;

final class BuildProcessor {

    private final GradleEnterpriseApi api;

    BuildProcessor(GradleEnterpriseApi api) {
        this.api = api;
    }

    void process(Build build, BuildsMetrics buildsMetrics) {
        try {
            processGradleBuildWithGradleAttributesModel(build, buildsMetrics);
        } catch (ApiException e) {
            reportError(build, e);
        }
    }

    // API overall build time by tag, by time window
    private void processGradleBuildWithGradleAttributesModel(Build build, BuildsMetrics buildsMetrics) throws ApiException {
        long buildDurationMs = getBuildDurationInMs(build);
        reportBuild(build, getBuildStartTimeInMs(build), buildDurationMs, buildsMetrics.getBuildCount());
        buildsMetrics.addBuild(buildDurationMs);
    }

    // The duration of the build, as milliseconds since Epoch.
    private Long getBuildDurationInMs(Build build) {
        if(null != build.getModels() && null != build.getModels().getGradleAttributes() && null != build.getModels().getGradleAttributes().getModel()) {
            return build.getModels().getGradleAttributes().getModel().getBuildDuration();
        } else {
            return 0L;
        }
    }

    // The start time of the build, as milliseconds since Epoch.
    private Long getBuildStartTimeInMs(Build build) {
        if(null != build.getModels() && null != build.getModels().getGradleAttributes() && null != build.getModels().getGradleAttributes().getModel()) {
            return build.getModels().getGradleAttributes().getModel().getBuildStartTime();
        } else {
            return 0L;
        }
    }

    private void reportBuild(Build build, long buildStartTimeInMs, long buildDurationInMs, int buildCount) {
        System.out.println("--- build " + (buildCount+1) + " ---");
        System.out.println(build.getId());
        System.out.println("Start time = " + Instant.ofEpochMilli(buildStartTimeInMs));
        System.out.println("Duration = " + Duration.ofMillis(buildDurationInMs));
    }

    private void reportError(Build build, ApiException e) {
        System.err.printf("API Error %s for Build Scan ID %s%n%s%n", e.getCode(), build.getId(), e.getResponseBody());
        ApiProblemParser.maybeParse(e, api.getApiClient().getObjectMapper())
            .ifPresent(apiProblem -> {
                // Types of API problems can be checked as following
                if (apiProblem.getType().equals("urn:gradle:enterprise:api:problems:build-deleted")) {
                    // Handle the case when the Build Scan is deleted.
                    System.err.println(apiProblem.getDetail());
                }
            });
    }

}

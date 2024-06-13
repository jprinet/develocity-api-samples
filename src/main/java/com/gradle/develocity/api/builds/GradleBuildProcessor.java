package com.gradle.develocity.api.builds;

import com.gradle.enterprise.api.GradleEnterpriseApi;
import com.gradle.enterprise.api.client.ApiException;
import com.gradle.enterprise.api.model.*;

import java.time.Duration;

final class GradleBuildProcessor implements BuildProcessor {

    private final GradleEnterpriseApi api;

    GradleBuildProcessor(GradleEnterpriseApi api) {
        this.api = api;
    }

    @Override
    public void process(Build build, BuildsMetrics buildsMetrics) {
        try {
            processGradleBuildWithGradleAttributesModel(build, buildsMetrics);
        } catch (ApiException e) {
            reportError(build, e);
        }
    }

    // API overall build time by tag, by time window
    private void processGradleBuildWithGradleAttributesModel(Build build, BuildsMetrics buildsMetrics) throws ApiException {
        long buildDurationMs = getBuildDurationMs(build);
        reportBuild(build, buildDurationMs);
        buildsMetrics.addBuild(buildDurationMs);
    }

    // The duration of the build, as milliseconds since Epoch.
    private static Long getBuildDurationMs(Build build) {
        if(null != build.getModels() && null != build.getModels().getGradleAttributes() && null != build.getModels().getGradleAttributes().getModel()) {
            return build.getModels().getGradleAttributes().getModel().getBuildDuration();
        } else {
            return 0L;
        }
    }

    private void reportBuild(Build build, long buildDurationMs) {
        System.out.println("----------------------");
        System.out.println(build.getId());
        Duration duration = Duration.ofMillis(buildDurationMs);
        System.out.println(duration.toString());
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

package com.gradle.develocity.api.builds;

import com.gradle.develocity.api.shared.GradleEnterpriseApiProvider;
import com.gradle.enterprise.api.GradleEnterpriseApi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import java.util.Arrays;
import java.util.concurrent.Callable;

@Command(
    name = "builds",
    description = "A program using the Develocity API to extract build data",
    synopsisHeading = "%n@|bold Usage:|@ ",
    optionListHeading = "%n@|bold Options:|@%n",
    commandListHeading = "%n@|bold Commands:|@%n",
    parameterListHeading = "%n@|bold Parameters:|@%n",
    descriptionHeading = "%n",
    synopsisSubcommandLabel = "COMMAND",
    usageHelpAutoWidth = true,
    usageHelpWidth = 120
)
public final class BuildsApiCollector implements Callable<Integer> {

    @Mixin
    GradleEnterpriseApiProvider apiProvider;

    @Option(
        names = "--reverse",
        description = "A boolean indicating the time direction of the query. A value of true indicates a backward query, and returned builds will be sorted from most to least recent. A value of false indicates a forward query, and returned builds will be sorted from least to most recent (default: ${DEFAULT-VALUE}).",
        defaultValue = "false",
        order = 3
    )
    boolean reverse;

    @Option(
        names = "--max-builds",
        description = "The maximum number of builds to return by a single query. The number may be lower if --max-wait-secs is reached (default: ${DEFAULT-VALUE})",
        defaultValue = "1000",
        order = 4
    )
    int maxBuilds;

    @Option(
            names = "--start-time",
            description = "Start time to collect builds from. The time should be in the format of 'yyyy-MM-ddThh:mm:ss'",
            defaultValue = "2024-06-01T00:00:00",
            order = 5
    )
    String startTime;

    @Option(
            names = "--end-time",
            description = "End time to collect builds to. The time should be in the format of 'yyyy-MM-ddThh:mm:ss'",
            defaultValue = "2024-06-03T23:59:59",
            order = 6
    )
    String endTime;

    @Option(
            names = "--tags",
            description = "Build tags",
            defaultValue = "CI",
            order = 7
    )
    String tags;

    @Option(
        names = "--max-wait-secs",
        description = "The maximum number of seconds to wait until a query returns. If the query returns before --max-builds is reached, it returns with already processed builds (default: ${DEFAULT-VALUE})",
        defaultValue = "3",
        order = 8
    )
    int maxWaitSecs;

    @Override
    public Integer call() throws Exception {
        GradleEnterpriseApi api = apiProvider.create();
        BuildProcessor gradleBuildProcessor = new BuildProcessor(api);
        BuildsProcessor buildsProcessor = new BuildsProcessor(api, gradleBuildProcessor, maxBuilds, startTime, endTime, Arrays.asList(tags.split(",")));

        System.out.println("Processing builds ...");
        buildsProcessor.process();

        return 0;
    }

}

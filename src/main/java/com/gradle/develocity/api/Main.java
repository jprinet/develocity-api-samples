package com.gradle.develocity.api;

import com.gradle.develocity.api.builds.BuildsApiCollector;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;

@Command(
    name = "develocity-api-samples",
    description = "A program that demonstrates using the Develocity API to extract build and tests data",
    synopsisHeading = "%n@|bold Usage:|@ ",
    optionListHeading = "%n@|bold Options:|@%n",
    commandListHeading = "%n@|bold Commands:|@%n",
    parameterListHeading = "%n@|bold Parameters:|@%n",
    descriptionHeading = "%n",
    synopsisSubcommandLabel = "COMMAND",
    usageHelpAutoWidth = true,
    usageHelpWidth = 120,
    subcommands = {BuildsApiCollector.class, HelpCommand.class}
)
public final class Main {

    public static void main(final String[] args) {
        //noinspection InstantiationOfUtilityClass
        System.exit(new CommandLine(new Main()).execute(args));
    }

}

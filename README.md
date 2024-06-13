# Develocity API Samples

This repository demonstrates using the Develocity API and generating client code from its OpenAPI specification.

The sample contains two scenarios:
* The `builds` scenario which collects average build duration by startTime, endTime and tags.
* The `tests` scenario determines previously stable test classes that have recently become unstable, and creates a report pointing to example builds published to the given Develocity instance.

## How to build

Execute:

```
$ ./gradlew install
```

This builds and installs the program into `build/install/develocity-api-samples`.
You can use the `build/install/develocity-api-samples/bin/develocity-api-samples` script to run the sample.

### Note on Java 11

The current version of the OpenAPI generator requires Java 11 to generate the client code. Even though this sample uses Java 11 to generate the client, but the generated **client code is based on Java 8**.
Therefore, the generated client is still compatible with Java 8 based projects.

## How to run

A Develocity access key with the “Export build data via the API” permission is required.

To create an access key:

1. Sign in to Develocity.
2. Access "My settings" from the user menu in the top right-hand corner of the page.
3. Access "Access keys" from the left-hand menu.
4. Click "Generate" on the right-hand side and copy the generated access key.

The access key should be saved to a file, which will be supplied as a parameter to the program.

### Builds API sample

After provisioning the access key, execute:

```
$ build/install/develocity-api-samples/bin/develocity-api-samples builds --server-url=«serverUrl» --access-key-file=«accessKeyFile» --start-time 2024-06-10T00:00:00 --end-time 2024-06-13T23:59:59 --tags CI,main,Linux
```

- `«serverUrl»`: The address of your Develocity server (e.g. `https://develocity.mycompany.com`)
- `«accessKeyFile»`: The path to the file containing the access key
- `--start-time`: The start time of the period to collect data for (ISO 8601 format)
- `--end-time`: The end time of the period to collect data for (ISO 8601 format)
- `--tags`: A comma-separated list of tags to filter builds by

To stop the program, use <kbd>Ctrl</kbd> + <kbd>C</kbd>.

## Console output sample
```
$ build/install/develocity-api-samples/bin/develocity-api-samples builds --server-url=https://my-develocity-url.com --access-key-file=my-access.key --start-time 2024-06-01T15:00:00 --end-time 2024-06-04T15:00:00 --tags CI,main,Linux
Processing builds ...
Query: buildTool:gradle buildStartTime:[2024-06-01T15:00:00 to 2024-06-04T15:00:00] tag:CI tag:main tag:Linux
----------------------
k7dnvpf6yrxjq
PT7M59.507S
----------------------
...
----------------------
2n6ifcg67el3m
PT9M46.022S
----------------------
----------------------
Builds processed: 316
Overall build duration: PT20H57M35.557S
Average build duration: PT3M58.783S
```

## Further documentation

The Develocity API manual and reference documentation for each version of the API can be found [here](https://docs.gradle.com/enterprise/api-manual).

## License

The Develocity API Samples project is open-source software released under the [Apache 2.0 License][apache-license].

[apache-license]: https://www.apache.org/licenses/LICENSE-2.0.html

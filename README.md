# Micrometer & OpenTelemetry Integration

## Components for seamlessly integration

    - look like Micrometer **ObservationRegistry** is an important component that is being used to register different Observation such as the one provided by OpenTelemetry
    - OtlpConfig is the interface to configure the OTEL regsitry with the exporters, sampling etc. This same configuration can be achieved via configuration

        ``` 
        management:
            otlp:
                metrics:
                export:
                    # Supported configs
                    url: "https://otlp.example.com:4318/v1/metrics"
                    batchSize: 15000
                    aggregationTemporality: "cumulative"
                    headers:
                    header1: value1
                    step: 30s
                    resourceAttributes:
                    key1: value1
        ```

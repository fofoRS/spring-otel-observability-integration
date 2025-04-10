# Spring Boot OpenTelemetry Integration Demo

## Introduction

This project demonstrates the integration of Micrometer, OpenTelemetry, and Spring Boot for distributed tracing and metrics collection across three microservices:

1. **Event Publisher**: A service that generates user click events and publishes them to RabbitMQ
2. **Event Consumer**: Processes the events, enriches them with user data, and logs them
3. **User Service**: Provides user information for event enrichment

The project is made up by three simple yet fully capable to emit traces, metrics and logs with traceId and Span so we can properly observe the inner state of each application as well as aquire a complete picture of the distributed workflow, hops, context data, errors and more.

### Configuration Approach

For this demo I wanted to demostrate the seamlessly integration between Micrometer (Facade observability framework for JVM based apps) and Opentelemetry which is a CNCF project. This approach will allow us to observe Spring boot application with minimal effor by leveraging Micrometer. And by using OpenTelemetry as the protocol to emit this telemetry data will gain high flexibility as OpenTelemetry is widely adopted by a vast applications and libraries and visulization tools.

#### Observability Stack

1. Micrometer as Observability Facade
2. Opentelemetry (OTLP) as protocol to emit telemetry data
3. Opentelemetry Collector as Central gateway to receive process and export telemetry data to backends that will visualize the data
4. Zipkin & Jaeger as visualization tools

### High Level Archicture Arragement

    1. Docker running in host machine, it hosts Jaeger, Zipkin, Rabbitmq and Postgresql communication with external services.
    2. Minikube: it run the following applications
        - event-publisher, 
        - event-consumer
        - user-service: service accessible from outside of minkube via ingress.
        - otel-collect
    3. RabbitMq 
    4. Postgresql
    5. Jaeger
    6. Zipkin

## Prerequisites

Before running the project, ensure you have the following installed:

1. **Docker**
   - Install Docker Desktop from [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)

2. **Minikube**
   - Install Minikube from [https://minikube.sigs.k8s.io/docs/start/](https://minikube.sigs.k8s.io/docs/start/)
   - Start Minikube: `minikube start`
   - Enable Ingress addon: `minikube addons enable ingress`
   - Start Minikube tunnel: `minikube tunnel` ** tunnel must be open in a separate terminal, and keep it open in order to enable communication from host machine to applications running inside minikube (this is required if you want to interact with user-service)

## Deployment Steps

1. **Run Container in local docker**
    - go to otel > k8s
    - run ` docker-compose up` or `docker-compose up -d` to run service in detached mode.

2. **Run and deploy Otel Collector**
    - go to otel folder
    - run `kubectl appl -f k8s/`

1. **Build the Projects**
   ```bash
   # Build event-publisher
   cd event-publisher
   ./mvnw clean package
   
   # Build event-consumer
   cd ../event-consumer
   ./mvnw clean package
   
   # Build user-service
   cd ../user-service
   ./mvnw clean package
   ```

2. **Build Docker Images**
   ```bash
   # Build event-publisher image
   docker build -t event-publisher:latest ./event-publisher
   
   # Build event-consumer image
   docker build -t event-consumer:latest ./event-consumer
   
   # Build user-service image
   docker build -t user-service:latest ./user-service
   ```

3. **Deploy to Minikube**
   ```bash
   # Apply Kubernetes resources
   kubectl apply -f event-publisher/k8s/
   kubectl apply -f event-consumer/k8s/
   kubectl apply -f user-service/k8s/
   
   # Verify deployments
   kubectl get pods
   kubectl get services
   ```

4. **Access user-service**
    user-service can be accessed from host machine. user-service is configured with a service and an ingress object to expose service in a consistent way using a reverse proxy approach leveraging the nginx reverse proxy.

    - Endpoint Spec
       Endpoint will return user information for ids from 1 to 5, any id greater than 5 will return a 404 status code.
        - METHOD: GET
        - URL: http:localhost/user/api/v1/users/{id} - you must have **minikube tunnel open in a separate terminal**


## How to access Tracing Data.

Once you have everything setup, event-publisher application will start emitting mock click event, that will then consumed by event-consumer application and enriched by calling user-service via http to retrieve information about the user. Each application logs information with the traceId and SpanId attached to each log for example `[user-service] [io-8080-exec-10] [70bea9ed2a707680e69818a0f6add5b1-3f8e5bc944fc274b] o.p.p.e.o.user.ApiController             : User found with id: 5`, in this example `0bea9ed2a707680e69818a0f6add5b1` is the traceId and `3f8e5bc944fc274b` is the spanId.  You can use the traceId to look for that specific trace in any of the available visualization tools (jaeger or zipkin).

You can also get the traces filter by service name in those visualization tools which will give you the operation observed in that application.

### Accessing Jaeger and Zipkin

    - Jaeger Dashboard: http://localhost:16686/
    - Zipkin Dashboard: http://localhost:9411/zipkin/

    

# ECS Deployment Notes

This application is prepared to run on Amazon ECS with the `ecs` Spring profile.

## Runtime profile

Run the application with:

```bash
SPRING_PROFILES_ACTIVE=ecs
```

The `ecs` profile enables:

- `server.address=0.0.0.0`
- `server.port=${SERVER_PORT:8080}`
- graceful shutdown
- readiness and liveness health probes

## Health checks

Use the readiness endpoint for the load balancer or container health check:

```text
/actuator/health/readiness
```

The general health endpoint remains available at:

```text
/actuator/health
```

## Build an OCI image

Spring Boot can build an OCI image directly through Cloud Native Buildpacks, without a custom Dockerfile:

```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=poc-mcp-server:local
```

For Amazon ECR, replace the image name with your registry URL, for example:

```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=<account>.dkr.ecr.<region>.amazonaws.com/poc-mcp-server:latest
```

## Recommended next AWS steps

- store credentials in AWS Secrets Manager or SSM Parameter Store
- terminate TLS at an Application Load Balancer
- run the image on ECS before considering direct EC2 Docker management
- replace the current in-memory Basic Auth with a production-grade auth model before public exposure

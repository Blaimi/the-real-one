# DHBW-WE2

## Installation

To install this project, copy the file `kustomization.yaml` and run:

```shell
kubectl apply -k . --prune -l app.kubernetes.io/part-of=biletado -n biletado
kubectl wait pods -n biletado -l app.kubernetes.io/part-of=biletado --for condition=Ready --timeout=120s
```

You can also find the builds on dockerhub [here](https://hub.docker.com/r/derfrzocker/the-real-one).

## Configuration

This project uses Spring boot as the framework, for a list of all properties supported by spring click [here.](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
To configure this project, you can either use env variables or provide a property file, for more info click [here](https://docs.spring.io/spring-boot/docs/2.1.17.RELEASE/reference/html/boot-features-external-config.html) or [here.](https://docs.spring.io/spring-boot/docs/2.1.17.RELEASE/reference/html/howto-properties-and-configuration.html)

### Useful properties

| Name                     | Description                        | Default       |
|--------------------------|------------------------------------|---------------|
| SPRING_SERVER_PORT       | The port of the application        | 9000          |
| JERSEY_APPLICATION_PATH  | The base path of the application   | api/v2/assets |
| POSTGRES_ASSETS_HOST     | Database host name                 | -             |
| POSTGRES_ASSETS_PORT     | Database port                      | -             |
| POSTGRES_ASSETS_DBNAME   | Database name                      | -             |
| POSTGRES_ASSETS_USER     | Database user name                 | -             |
| POSTGRES_ASSETS_PASSWORD | Database password                  | -             |
| LOGGING_LEVEL_ROOT       | Debug level of default logger      | -             |
| LOGGING_CONFIG           | Log4j2 configuration file location | -             |
| KEYCLOAK_HOST            | Keycloak host name                 | keycloak      |
| KEYCLOAK_REALM           | Keycloak realm                     | biletado      |

For a list of log level click [here.](https://logging.apache.org/log4j/2.x/manual/customloglevels.html)
To see how to configure logging click [here.](https://logging.apache.org/log4j/2.x/manual/configuration.html)
To log messages in ecs format set the env variable `LOGGING_CONFIG` to `classpath:log4j2-ecs.xml`.

## License

This Project is licensed under MIT.

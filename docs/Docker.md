A very basic Docker image has been created to simplify the distribution of the Home-and-Garden-Shop application.

The image can be found on [Docker Hub](https://hub.docker.com/r/yuliavladimirov/hoga-shop).

The image contains the Temurin Java runtime, the Home-and-Garden-Shop application itself and rudimentary application.properties file.

## Running the Application

The easiest way of running the application is through Docker Compose. Here is the minimal Compose file:

```yaml
name: "shop"
services:
  shop-db:
    image: mysql
    environment:
      MYSQL_ROOT_PASSWORD: "p455W0Rd"
    healthcheck:
      test: ["CMD-SHELL", "mysqladmin ping -h localhost -u root -p'p455W0Rd'"]
      timeout: 20s
      retries: 10

  shop-app:
    image: yuliavladimirov/hoga-shop
    restart: on-failure
    build: .
    ports:
      - "8080:8080"
    depends_on:
      shop-db:
        condition: service_healthy
```

The image can of course be used with some other database (MySQL or similar). In this case, a custom __application.properties__ file needs to be mounted into the image:

`docker run -v ./custom.application.properties:/app/application.properties -p 8080:8080 -d yuliavladimirov/hoga-shop`



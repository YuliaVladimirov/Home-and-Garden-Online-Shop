# Home and Garden Online Shop
This web-application implements the basic functionality of an online-store backend.

This project is the final project of Tel-Ran back-end developer course.

## General Description

The application allows customers to select a product from the catalog, add it to the cart, place an order and track its status in real time. For administrators the application provides tools for managing the product catalog, orders, promotions and sales analytics.

## Detailed Project Documentation

- [Database Structure](docs/DB.md)
- [Docker Specifications](docs/Docker.md)
- The application's API in OpenAPI format is exposed via [Swagger](http://localhost:8080/swagger-ui/index.html#/) 
- 

## Technology Stack

The project was designed as Spring Boot Web Application:
- Java 21
- Spring Boot 3.3.0

The data is stored in the database:
- MySQL 8.x / MariaDB 10.x (or later)

To automate the application deployment, a Docker image and a basic Compose definition have been created. For details see [Docker.md](docs/Docker.md).
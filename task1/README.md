# IDT Programming Task

This repository contains a simple http server with REST API.

## Technology - Kotlin

As a server I chose [Ktor](https://ktor.io/) - it's very lightweight and performant solution that utilizes Kotlin coroutines and thus is
fully asynchronous and scales well. Moreover, the idea of "server as a function" is exactly how I imagine modern and clean code should
look like.

To document created API, I use [Swagger](https://swagger.io/), because it's pretty much the industry standard how to document APIs.
Sadly, Ktor does not provide this out of the box (will be added in the [next version](https://github.com/ktorio/ktor/pull/3147)), so I'm
using my [own fork of library](https://github.com/LukasForst/ktor-openapi-generator) that autogenerates OpenAPI definition and servers
Swagger UI.

Because Ktor is only http server, we're free to use any framework for dependency injection - in this case I
chose [Kodein](https://github.com/kosi-libs/Kodein) as again, it's very light, does its job well and has direct integration with Ktor.

Because assignment suggested not to use DB and just store data in memory, we don't have database engine. However, I tend to
use [Exposed](https://github.com/JetBrains/Exposed) framework, because it gives me flexibility to write strongly typed SQL as well as
use ORM approach when needed.

## Project Structure

Standard architecture with service layer taking care of business logic
([UsersService](src/main/kotlin/dev/forst/idt/service/UsersService.kt)) - in our case also storage, because we skip the DAO part.
Then we have routing (or controllers) that handle HTTP requests and data parsing
([Routing](src/main/kotlin/dev/forst/idt/routing/Routing.kt)).
Application setup & startup is then in [Application](src/main/kotlin/dev/forst/idt/Application.kt).

There are also two tests - one for `UsersService` in [UsersServiceTest](src/test/kotlin/dev/forst/idt/service/UsersServiceTest.kt) - and
one for the API and routing in [ApiTest](src/test/kotlin/dev/forst/idt/routing/ApiTest.kt). I used [JUnit](https://junit.org/junit5/) as
a unit testing framework (again industry standard) and [Mockk](https://mockk.io/) for mocking - this one is probably the best mocking
library for the Kotlin as it supports mocking everything (including static functions).

## How to run it

### Server

To start up the application run:

```bash
./gradlew run
```

The app should be available on `http://localhost:8080`

### Tests

```bash
./gradlew test
```

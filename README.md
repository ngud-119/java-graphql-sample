# java-graphql-sample

<p>
<a href="https://twitter.com/hardyscchk"><img src="https://img.shields.io/twitter/follow/hardyscchk.svg?style=social&label=Follow"></a>
</p>

## Description

A starter project that makes creating a deployable spring-boot code-first graphql project.

## Technologies

- [spring-boot](https://spring.io/projects/spring-boot)
- [graphql-spqr](https://github.com/leangen/graphql-spqr)

## DB setup
```bash
docker run -d -e "MYSQL_ROOT_PASSWORD=Admin12345" -e "MYSQL_USER=usr" -e "MYSQL_PASSWORD=User12345" -e "MYSQL_DATABASE=development" -p 3306:3306 --name some-mysql bitnami/mysql:5.7.27
```
## Migration
```bash
// ./mvnw package
./migrate up/down
```

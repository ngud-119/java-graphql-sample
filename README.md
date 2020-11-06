# Java GraphQL Backend Tutorial

## Overview

Build a basic CRUD backend application in Java Spring Boot with GraphQL & MySQL.

## Technologies

1. [Java](https://www.oracle.com/java/)
2. [Spring Boot](https://spring.io/projects/spring-boot)
3. [Maven](https://maven.apache.org/)
4. [GraphQL Spring Framework Boot Starters](https://github.com/graphql-java-kickstart/graphql-spring-boot)
5. [MySQL](https://www.mysql.com/)
6. [MyBatis Migrations](http://mybatis.org/migrations/)

## Create an Empty Project

### Download the template of an Empty Project

Setup an empty project using [Spring Initializr](https://start.spring.io/).

1. Update the field "Artifact" as `java-graphql-sample`.

2. Update the field "Package name" as `com.example.jgs`.

3. Press `ADD DEPENDENCIES...` button to add the following dependency: `Spring Web`.

> Please use Maven Project in Java 8, packaging as JAR for this tutorial.

Press the `GENERATE` button to download an empty project template.
Extract the zip folder to start editing.

### Add a Default Controller

Create the following file `src/main/java/com/example/jgs/controller/HealthController.java`.

```java
package com.example.jgs.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping
    public String check() {
        return "OK";
    }
}
```

Start server with below command:

```bash
mvnw spring-boot:run
```

Goto http://localhost:8080 You should see the `OK` message.

## Add GraphQL Dependencies

### Add JPA and GraphQL dependency

Update the `pom.xml` as follow.

```xml
    <properties>
        ...
        <graphql-java-kickstart.version>7.1.0</graphql-java-kickstart.version>
        <graphql-spqr.version>0.10.1</graphql-spqr.version>
    </properties>

    <dependencies>
        ...
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>com.graphql-java-kickstart</groupId>
            <artifactId>graphql-spring-boot-starter</artifactId>
            <version>${graphql-java-kickstart.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>com.graphql-java</groupId>
                    <artifactId>graphql-java</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.graphql-java-kickstart</groupId>
            <artifactId>playground-spring-boot-starter</artifactId>
            <version>${graphql-java-kickstart.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.leangen.graphql</groupId>
            <artifactId>spqr</artifactId>
            <version>${graphql-spqr.version}</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
...
```

### Create the User Entity and Service

Create the following file `src/main/java/com/example/jgs/input/CreateUser.java`.

```java
package com.example.jgs.input;

import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Data;

@Data
public class CreateUser {
    @GraphQLNonNull
    private String name;

    private String nickName;
}
```

Create the following file `src/main/java/com/example/jgs/model/User.java`.

```java
package com.example.jgs.model;

import com.example.jgs.input.CreateUser;
import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    @Type(type = "uuid-char")
    @GraphQLNonNull
    @Column(length = 36)
    private UUID id;

    @GraphQLNonNull
    private String name;

    private String nickName;

    public static User from(CreateUser createUser) {
        return User.builder().name(createUser.getName())
                .nickName(createUser.getNickName())
                .build();
    }
}
```

Create the following file `src/main/java/com/example/jgs/repository/UserRepository.java`.

```java
package com.example.jgs.repository;

import com.example.jgs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
}
```

Create the following file `src/main/java/com/example/jgs/service/UserService.java`.

```java
package com.example.jgs.service;

import com.example.jgs.input.CreateUser;
import com.example.jgs.model.User;
import com.example.jgs.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> find() {
        return this.userRepository.findAll();
    }

    public User findOneById(UUID id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public User create(CreateUser createUser) {
        return this.userRepository.save(User.from(createUser));
    }

    public boolean delete(UUID id) {
        if (this.userRepository.existsById(id)) {
            this.userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
```

### Implement the User Resolver

Create the following file `src/main/java/com/example/jgs/resolver/UserResolver.java`.

```java
package com.example.jgs.resolver;

import com.example.jgs.input.CreateUser;
import com.example.jgs.model.User;
import com.example.jgs.service.UserService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserResolver {

    private final UserService userService;

    @GraphQLQuery
    @GraphQLNonNull
    public List<@GraphQLNonNull User> users() {
        return this.userService.find();
    }

    @GraphQLQuery
    public User user(
            @GraphQLArgument(name = "id") UUID id) {
        return this.userService.findOneById(id);
    }

    @GraphQLMutation
    @GraphQLNonNull
    public User createUser(
            @GraphQLArgument(name = "input") @GraphQLNonNull CreateUser createUser) {
        return this.userService.create(createUser);
    }

    @GraphQLMutation
    public UUID deleteUser(
            @GraphQLArgument(name = "id") UUID id) {
        return this.userService.delete(id) ? id : null;
    }
}
```

### Add Config for GraphQL

Create the following file `src/main/java/com/example/jgs/config/GraphqlConfig.java`.

```java
package com.example.jgs.config;

import com.example.jgs.resolver.UserResolver;
import graphql.schema.GraphQLSchema;
import io.leangen.graphql.GraphQLSchemaGenerator;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class GraphqlConfig {

    private final UserResolver userResolver;

    @Bean
    GraphQLSchema schema() {
        return new GraphQLSchemaGenerator()
                .withBasePackages("com.example.jgs")
                .withOperationsFromSingleton(this.userResolver)
                .generate();
    }
}
```

Rename `src/main/resources/application.properties` to `src/main/resources/application.yml`. Then Add the following:

```yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
```

### Test the GraphQL Endpoint

Start the server.

```bash
mvnw spring-boot:run
```

Goto the GraphQL Playground - http://localhost:8080/playground.

1. Create a new user

   ```graphql
   mutation {
     createUser(input: { name: "Tommy" }) {
       id
     }
   }
   ```

   Output :

   ```json
   {
     "data": {
       "createUser": {
         "id": "8ec8105e-1ffc-4fe3-81fb-882b3dd33f82"
       }
     }
   }
   ```

2. Query the users

   ```graphql
   query {
     users {
       id
       name
     }
   }
   ```

   Output :

   ```json
   {
     "data": {
       "users": [
         {
           "id": "8ec8105e-1ffc-4fe3-81fb-882b3dd33f82",
           "name": "Tommy"
         }
       ]
     }
   }
   ```

## Setup DB and DB migration

### Config MySQL and MyBatis Migrations

Update the following file `pom.xml`

```xml
...
    <properties>
        ...
        <mybatis-migrations-autoconfigure.version>0.0.2</mybatis-migrations-autoconfigure.version>
    </properties>

    <dependencies>
        ...
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>de.bessonov</groupId>
            <artifactId>mybatis-migrations-spring-boot-autoconfigure</artifactId>
            <version>${mybatis-migrations-autoconfigure.version}</version>
        </dependency>
    </dependencies>
...
```

Update the following file `src/main/resources/application.yml`.

```yml
spring:
  datasource:
    url: jdbc:${DATABASE_URL:mysql://usr:User12345@localhost:3306/development}

logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

server:
  port: 4000
```

### Initialize the migration scripts

Create a folder `scripts` under `src/main/resources`

Create the following file `src/main/resources/scripts/<yyyymmddHHmmss>_create_changelog.sql` </br> (e.g. 20200728095409_create_changelog.sql )

```sql
-- // Create Changelog

-- Default DDL for changelog table that will keep
-- a record of the migrations that have been run.

-- You can modify this to suit your database before
-- running your first migration.

-- Be sure that ID and DESCRIPTION fields exist in
-- BigInteger and String compatible fields respectively.

CREATE TABLE ${changelog} (
  ID NUMERIC(20,0) NOT NULL,
  APPLIED_AT VARCHAR(25) NOT NULL,
  DESCRIPTION VARCHAR(255) NOT NULL,
  PRIMARY KEY(ID)
);

-- //@UNDO
DROP TABLE ${changelog};
```

Create the following file `src/main/resources/scripts/<yyyymmddHHmmss>_first_migration.sql` </br> (e.g. 20200728095410_first_migration.sql )

```sql
-- // First migration.
-- Migration SQL that makes the change goes here.
CREATE TABLE user (
  id VARCHAR(36) NOT NULL,
  name VARCHAR(255) NOT NULL,
  nick_name VARCHAR(255) NULL,
  PRIMARY KEY(id)
);

-- //@UNDO
-- SQL to undo the change goes here.
DROP TABLE user;
```

> Please note the following points when creating new script: <br /> 1. The naming convention is `<yyyymmddHHmmss>_<description>.sql` <br /> 2. Put undo SQL under `-- //@UNDO` section.

### Test the migration script

#### Build the jar

Create the following file `src/test/resources/application.yml`.

```yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb

graphql:
  servlet:
    websocket:
      enabled: false
```

Update the dependency of `com.h2database` in `pom.xml` as follow:

```xml
...
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>test</scope>
		</dependency>
...
```

Package the jar by command:

```bash
mvnw package
```

#### Execution of migration command

Start a MySQL docker instance.

```bash
docker run -d -e "MYSQL_ROOT_PASSWORD=Admin12345" -e "MYSQL_USER=usr" -e "MYSQL_PASSWORD=User12345" -e "MYSQL_DATABASE=development" -p 3306:3306 --name some-mysql bitnami/mysql:5.7.27
```

Run the below command and look at migration status before executing the scripts:

```bash
>java -jar target/java-graphql-sample-0.0.1-SNAPSHOT.jar migrations status

ID             Applied At          Description
================================================================================
20200812020020    ...pending...    create changelog
20200812020021    ...pending...    first migration
```

Execution of the migration script:

```bash
java -jar target/java-graphql-sample-0.0.1-SNAPSHOT.jar migrations up
```

Run the below command again to check the applied time:

```bash
>java -jar target/java-graphql-sample-0.0.1-SNAPSHOT.jar migrations status

ID             Applied At          Description
================================================================================
20200812020020 2020-08-12 11:43:46 create changelog
20200812020021 2020-08-12 11:43:46 first migration
```

### Test the GraphQL Endpoint

Start the server.

```bash
mvnw spring-boot:run
```

Goto the GraphQL Playground - http://localhost:4000/playground.

1. Create some users

   ```graphql
   mutation {
     a: createUser(input: { name: "John" }) {
       id
     }
     b: createUser(input: { name: "Mary" }) {
       id
     }
   }
   ```

   Output:

   ```json
   {
     "data": {
       "a": {
         "id": "94ae9dd3-c739-4c04-8bfb-6a7c94552cd6"
       },
       "b": {
         "id": "2aa8e496-0c9d-41b4-a0c8-467beaf14247"
       }
     }
   }
   ```

2. Query the users

   ```graphql
   query {
     users {
       id
       name
     }
   }
   ```

   Output :

   ```json
   {
     "data": {
       "users": [
         {
           "id": "2aa8e496-0c9d-41b4-a0c8-467beaf14247",
           "name": "Mary"
         },
         {
           "id": "94ae9dd3-c739-4c04-8bfb-6a7c94552cd6",
           "name": "John"
         }
       ]
     }
   }
   ```

3. Delete one of the user by the id

   ```graphql
   mutation {
     deleteUser(id: "94ae9dd3-c739-4c04-8bfb-6a7c94552cd6")
   }
   ```

   Output :

   ```json
   {
     "data": {
       "deleteUser": "94ae9dd3-c739-4c04-8bfb-6a7c94552cd6"
     }
   }
   ```

4. Test the MySQL database

   Run the mysql command using the same docker instance.

   ```bash
   docker exec -it some-mysql mysql -uroot -p"Admin12345"
   ```

   Select the data from user table.

   ```sql
   mysql> use development;
   mysql> select * from user;
   +--------------------------------------+------+----------+
   | id                                   | name | nickName |
   +--------------------------------------+------+----------+
   | 2aa8e496-0c9d-41b4-a0c8-467beaf14247 | Mary | NULL     |
   +--------------------------------------+------+----------+
   ```

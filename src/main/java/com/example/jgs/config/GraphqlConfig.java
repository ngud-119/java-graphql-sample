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
                .withBasePackages("com.example.jqs")
                .withOperationsFromSingleton(this.userResolver)
                .generate();
    }
}

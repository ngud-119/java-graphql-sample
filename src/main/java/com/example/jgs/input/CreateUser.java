package com.example.jgs.input;

import io.leangen.graphql.annotations.GraphQLNonNull;
import lombok.Data;

@Data
public class CreateUser {
    @GraphQLNonNull
    private String name;

    private String nickName;
}

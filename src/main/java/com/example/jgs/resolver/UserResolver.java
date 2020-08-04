package com.example.jgs.resolver;

import com.example.jgs.input.CreateUser;
import com.example.jgs.model.User;
import com.example.jgs.service.UserService;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
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
    public List<User> users() {
        return this.userService.find();
    }

    @GraphQLQuery
    public User user(
            @GraphQLArgument(name = "id") UUID id) {
        return this.userService.findOneById(id);
    }

    @GraphQLMutation
    public User createUser(
            @GraphQLArgument(name = "input") CreateUser createUser) {
        return this.userService.create(createUser);
    }

    @GraphQLMutation
    public UUID deleteUser(
            @GraphQLArgument(name = "id") UUID id) {
        return this.userService.delete(id) ? id : null;
    }
}

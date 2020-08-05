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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
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
        return User.builder()
                .name(createUser.getName())
                .nickName(createUser.getNickName())
                .build();
    }
}

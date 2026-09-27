package com.example.wiki.api.user;

import java.util.List;
import java.util.stream.StreamSupport;

import com.example.wiki.entity.UserEntity;

public record UserRs(
        String login,
        String role) {

    public static UserRs from(UserEntity entity) {
        return new UserRs(
                entity.getLogin(),
                entity.getRole().name());
    }

    public static List<UserRs> fromList(Iterable<UserEntity> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(UserRs::from)
                .toList();
    }
}

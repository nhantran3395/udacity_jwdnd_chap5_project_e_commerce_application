package com.example.demo.model.mapper;

import com.example.demo.model.Role;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.mapstruct.*;

import static java.util.stream.Collectors.toSet;

@Mapper(componentModel = "spring")
public abstract class CreateUserRequestToUserEntityMapper {

    @Mapping(target = "authorities", ignore = true)
    public abstract User create(CreateUserRequest request);

    @AfterMapping
    protected void afterCreate(CreateUserRequest request, @MappingTarget User user) {
        if (request.getAuthorities() != null) {
            user.setAuthorities(request.getAuthorities().stream().map(Role::new).collect(toSet()));
        }
    }

}

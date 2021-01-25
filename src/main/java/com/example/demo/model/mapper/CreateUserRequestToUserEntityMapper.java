package com.example.demo.model.mapper;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class CreateUserRequestToUserEntityMapper {

    public abstract User create(CreateUserRequest request);
}

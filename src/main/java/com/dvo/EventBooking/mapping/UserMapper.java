package com.dvo.EventBooking.mapping;

import com.dvo.EventBooking.entity.User;
import com.dvo.EventBooking.web.model.request.UpdateUserRequest;
import com.dvo.EventBooking.web.model.request.UpsertUserRequest;
import com.dvo.EventBooking.web.model.response.UserResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "roleType", ignore = true)
    })
    User requestToUser(UpsertUserRequest request);

    @Mapping(target = "role", source = "roleType")
    UserResponse userToResponse(User user);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "username", ignore = true),
            @Mapping(target = "roleType", ignore = true)
    })
    void updateRequestToUser(UpdateUserRequest request, @MappingTarget User user);
}

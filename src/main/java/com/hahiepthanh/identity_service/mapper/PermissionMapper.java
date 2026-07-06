package com.hahiepthanh.identity_service.mapper;

import com.hahiepthanh.identity_service.dto.request.PermissionRequest;
import com.hahiepthanh.identity_service.dto.response.PermissionResponse;
import com.hahiepthanh.identity_service.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse (Permission permission);
}

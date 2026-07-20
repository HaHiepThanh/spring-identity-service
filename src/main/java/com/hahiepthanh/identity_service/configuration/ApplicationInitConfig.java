package com.hahiepthanh.identity_service.configuration;

import java.util.HashSet;

import com.hahiepthanh.identity_service.entity.Permission;
import com.hahiepthanh.identity_service.entity.User;
import com.hahiepthanh.identity_service.enums.Role;
import com.hahiepthanh.identity_service.repository.PermissionRepository;
import com.hahiepthanh.identity_service.repository.RoleRepository;
import com.hahiepthanh.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(
            UserRepository userRepository,
            org.springframework.transaction.PlatformTransactionManager transactionManager) {
        log.info("Init Application.............................");
        return args -> {
            new org.springframework.transaction.support.TransactionTemplate(transactionManager)
                    .executeWithoutResult(status -> {
                        Permission approvePostPermission = permissionRepository
                                .findById("APPROVE_POST")
                                .orElseGet(() -> permissionRepository.save(Permission.builder()
                                        .name("APPROVE_POST")
                                        .description("Approve post permission")
                                        .build()));

                        com.hahiepthanh.identity_service.entity.Role adminRole = roleRepository
                                .findById(Role.ADMIN.name())
                                .orElseGet(() -> {
                                    var permissions = new HashSet<Permission>();
                                    permissions.add(approvePostPermission);
                                    return roleRepository.save(com.hahiepthanh.identity_service.entity.Role.builder()
                                            .name(Role.ADMIN.name())
                                            .description("Administrator role")
                                            .permissions(permissions)
                                            .build());
                                });

                        var userOpt = userRepository.findByUsername("admin");
                        if (userOpt.isEmpty()) {
                            var roles = new HashSet<com.hahiepthanh.identity_service.entity.Role>();
                            roles.add(adminRole);

                            User user = User.builder()
                                    .username("admin")
                                    .password(passwordEncoder.encode("admin"))
                                    .roles(roles)
                                    .build();

                            userRepository.save(user);
                            log.warn("admin user has been created with default password: admin, please change it");
                        } else {
                            User user = userOpt.get();
                            if (user.getRoles() == null || !user.getRoles().contains(adminRole)) {
                                var roles = user.getRoles() != null
                                        ? new HashSet<>(user.getRoles())
                                        : new HashSet<com.hahiepthanh.identity_service.entity.Role>();
                                roles.add(adminRole);
                                user.setRoles(roles);
                                userRepository.save(user);
                                log.info("admin user role updated to include ADMIN");
                            }
                        }
                    });
        };
    }
}

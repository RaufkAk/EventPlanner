package com.yeditepe.dto;

import com.yeditepe.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String email;
    private Set<Role> roles;

}

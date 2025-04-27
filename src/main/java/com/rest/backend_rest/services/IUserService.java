package com.rest.backend_rest.services;

import com.rest.backend_rest.models.Users;

public interface IUserService {
    Users register(Users user);
    String loginUser(Users user);
}
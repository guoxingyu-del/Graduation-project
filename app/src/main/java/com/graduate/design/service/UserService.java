package com.graduate.design.service;

public interface UserService {
    int login(String username, String password);

    int register(String username, String password, String email);
}

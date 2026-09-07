package com.ai.spring_ai.service_impl;

import com.ai.spring_ai.model.User;
import org.springframework.stereotype.Service;

@Service
public class IdentityService {

    public static final User STUB_USER = new User("stub-user", "POC User");

    public User currentUser() {
        return STUB_USER;
    }
}
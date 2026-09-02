package com.ai.spring_ai.identity;

import org.springframework.stereotype.Service;

@Service
public class IdentityService {

    public static final User STUB_USER = new User("stub-user", "POC User");

    public User currentUser() {
        return STUB_USER;
    }
}
package edu.miu.blog.app.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class UserContext {

    private static final CurrentUser HOLDER = new CurrentUser();

    private UserContext() {}

    public static CurrentUser get() {
        return (CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

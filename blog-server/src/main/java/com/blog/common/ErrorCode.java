package com.blog.common;

public enum ErrorCode {
    SUCCESS(200, "OK"),
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    TOO_MANY_REQUESTS(429, "Too many requests"),
    INTERNAL_ERROR(500, "Internal server error"),
    VALIDATION_ERROR(1001, "Validation failed"),
    ARTICLE_NOT_FOUND(2001, "Article not found"),
    CATEGORY_NOT_FOUND(2002, "Category not found"),
    TAG_NOT_FOUND(2003, "Tag not found"),
    USER_NOT_FOUND(2004, "User not found"),
    INVALID_CREDENTIALS(2005, "Invalid username or password"),
    RSA_DECRYPT_FAILED(2006, "RSA decrypt failed"),
    STORAGE_ERROR(2007, "Storage operation failed"),
    USERNAME_ALREADY_EXISTS(2008, "Username already exists"),
    ROLE_FORBIDDEN(2009, "Role forbidden"),
    EMAIL_ALREADY_EXISTS(2010, "Email already exists"),
    COMMENT_NOT_FOUND(2011, "Comment not found"),
    COMMENT_TOO_FREQUENT(2012, "Commenting too frequently"),
    SUBSCRIPTION_ALREADY_EXISTS(2013, "Subscription already exists"),
    SUBSCRIPTION_TOO_FREQUENT(2014, "Submitting too frequently"),
    ARTICLE_INTERACTION_FORBIDDEN(2015, "Interaction is not available");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

package com.morzevichka.auth_service.model.token;

public enum RedisTokenType {
    EMAIL_VERIFICATION("email:verify:"),
    ACCOUNT_RECOVERY("account:recovery:");
    private final String prefix;

    RedisTokenType(String prefix) {
        this.prefix = prefix;
    }

    public String buildTokenKey(String token) {
       return prefix + token;
    }

    public String buildUserKey(String userId) {
        return prefix + userId;

    }

//    email:verify:{token}:{userId}
//    email:verify:{userId}:{token}
}

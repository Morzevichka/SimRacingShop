package com.morzevichka.auth_service.model.token;

public enum RedisTokenType {
    EMAIL_VERIFICATION("email:verify:"),
    ACCOUNT_RECOVERY("account:recovery:");

    private final String prefix;

    RedisTokenType(String prefix) {
        this.prefix = prefix;
    }

    public String buildKey(String token) {
       return prefix + token;
    }
}

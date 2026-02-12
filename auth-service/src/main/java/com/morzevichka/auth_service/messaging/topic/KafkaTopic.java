package com.morzevichka.auth_service.messaging.topic;

public final class KafkaTopic {
    public static final String EMAIL_VERIFICATION = "email-verification-request-event-topic";
    public static final String ACCOUNT_RECOVERY = "account-recovery-event-topic";

    private KafkaTopic() {}
}

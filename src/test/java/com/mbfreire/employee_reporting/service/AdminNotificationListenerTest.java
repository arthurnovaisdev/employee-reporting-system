package com.mbfreire.employee_reporting.service;

import com.mbfreire.employee_reporting.config.AdminNotificationProperties;
import com.mbfreire.employee_reporting.event.AdminNotificationEvent;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AdminNotificationListenerTest {

    private final RecordingEmailService emailService = new RecordingEmailService();
    private final AdminNotificationProperties properties = new AdminNotificationProperties();
    private final AdminNotificationListener listener = new AdminNotificationListener(emailService, properties);

    @Test
    void sendsOnlyWhenEnabled() {
        properties.setEmail("admin@example.com");
        listener.handle(new AdminNotificationEvent("DEN-2026-ABCD2345"));
        assertNull(emailService.recipient);

        properties.setEnabled(true);
        listener.handle(new AdminNotificationEvent("DEN-2026-ABCD2345"));
        assertEquals("admin@example.com", emailService.recipient);
        assertEquals("DEN-2026-ABCD2345", emailService.protocol);
    }

    @Test
    void emailFailureDoesNotEscapeListener() {
        properties.setEnabled(true);
        properties.setEmail("admin@example.com");
        emailService.fail = true;

        listener.handle(new AdminNotificationEvent("DEN-2026-ABCD2345"));
    }

    @Test
    void listenerRunsOnlyAfterCommit() throws Exception {
        var annotation = AdminNotificationListener.class
                .getMethod("handle", AdminNotificationEvent.class)
                .getAnnotation(TransactionalEventListener.class);
        assertEquals(TransactionPhase.AFTER_COMMIT, annotation.phase());
    }

    private static final class RecordingEmailService extends EmailService {
        private String recipient;
        private String protocol;
        private boolean fail;

        private RecordingEmailService() {
            super(new RestTemplate());
        }

        @Override
        public void sendNewReportAdminNotification(String toEmail, String reportProtocol) {
            if (fail) {
                throw new IllegalStateException("Simulated provider failure");
            }
            recipient = toEmail;
            protocol = reportProtocol;
        }
    }
}

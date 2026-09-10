package com.municipality.waste.service;

import com.municipality.waste.entity.Message;
import com.municipality.waste.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Sends "you've got a new message" emails. This is a best-effort side
 * channel to the in-app badges, never a dependency the core app relies on:
 * every send happens off the request thread (@Async) and every failure is
 * caught and logged, never rethrown. A misconfigured or unreachable SMTP
 * server must never prevent someone from posting a message in the app.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.email.from}")
    private String fromAddress;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final int BODY_SNIPPET_LENGTH = 220;

    @Async
    public void notifyNewMessage(Message message, List<User> recipients, int attachmentCount) {
        if (!emailEnabled) {
            return;
        }
        if (recipients == null || recipients.isEmpty()) {
            return;
        }

        String senderName = message.getSender() != null ? message.getSender().getFullName() : "Someone";
        String businessName = message.getBusiness() != null ? message.getBusiness().getName() : "your municipality";
        String snippet = snippet(message.getBody());
        String link = frontendUrl + "/messages";

        for (User recipient : recipients) {
            if (recipient.getEmail() == null || recipient.getEmail().isBlank()) {
                continue;
            }
            try {
                sendOne(recipient.getEmail(), senderName, businessName, message.getSubject(), snippet, attachmentCount, link);
            } catch (Exception e) {
                // Never let one bad address, or a down SMTP server, block the
                // rest of the recipients or bubble up to the caller.
                log.warn("Failed to send new-message email to {}: {}", recipient.getEmail(), e.getMessage());
            }
        }
    }

    private void sendOne(String to, String senderName, String businessName, String subject,
                          String snippet, int attachmentCount, String link) throws MailException {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject("New message: " + subject);

            String attachmentLine = attachmentCount > 0
                    ? "<p style=\"color:#555;font-size:13px;\">" + attachmentCount
                        + (attachmentCount == 1 ? " attachment" : " attachments") + " included.</p>"
                    : "";

            String html = "<div style=\"font-family:Arial,sans-serif;max-width:480px;\">"
                    + "<h2 style=\"color:#1b5e20;margin-bottom:4px;\">New message</h2>"
                    + "<p style=\"color:#333;\"><strong>" + escape(senderName) + "</strong> ("
                    + escape(businessName) + ") sent a new message:</p>"
                    + "<p style=\"background:#f4f4f4;border-left:3px solid #1b5e20;padding:10px 14px;color:#222;\">"
                    + "<strong>" + escape(subject) + "</strong><br/>" + escape(snippet) + "</p>"
                    + attachmentLine
                    + "<p><a href=\"" + link + "\" style=\"background:#1b5e20;color:#fff;padding:8px 16px;"
                    + "text-decoration:none;border-radius:4px;display:inline-block;\">View message</a></p>"
                    + "<p style=\"color:#999;font-size:12px;margin-top:24px;\">You're receiving this because "
                    + "email notifications are enabled on your account. You can turn them off in Account Settings.</p>"
                    + "</div>";

            helper.setText(html, true);
            mailSender.send(mime);
        } catch (jakarta.mail.MessagingException e) {
            log.warn("Could not build email message: {}", e.getMessage());
        }
    }

    private String snippet(String body) {
        if (body == null || body.isBlank()) {
            return "(no message body)";
        }
        String trimmed = body.trim();
        return trimmed.length() > BODY_SNIPPET_LENGTH
                ? trimmed.substring(0, BODY_SNIPPET_LENGTH) + "..."
                : trimmed;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

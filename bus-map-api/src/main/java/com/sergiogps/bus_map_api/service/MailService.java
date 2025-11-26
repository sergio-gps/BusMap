package com.sergiogps.bus_map_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;

    @Value("${mail.from.email}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email using Spring's JavaMailSender
     * 
     * @param toEmail Recipient email address
     * @param subject Email subject
     * @param body    Email body (can be HTML)
     * @throws MessagingException if email sending fails
     */
    public void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML content

            mailSender.send(message);
            logger.info("Email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new MessagingException("Failed to send email", e);
        }
    }

    /**
     * Sends a password reset email with the new password
     * 
     * @param toEmail     Recipient email address
     * @param username    Username of the account
     * @param newPassword The new temporary password
     * @throws MessagingException if email sending fails
     */
    public void sendPasswordResetEmail(String toEmail, String username, String newPassword) throws MessagingException {
        String subject = "Password Reset - Bus Map API";
        String body = String.format(
                "<html>" +
                        "<body>" +
                        "<h2>Password Reset Request</h2>" +
                        "<p>Hello <strong>%s</strong>,</p>" +
                        "<p>Your password has been reset successfully. Here is your new temporary password:</p>" +
                        "<p><strong>New Password:</strong> <code>%s</code></p>" +
                        "<p>Please log in with this password and change it immediately for security reasons.</p>" +
                        "<br>" +
                        "<p>If you did not request this password reset, please contact support immediately.</p>" +
                        "<br>" +
                        "<p>Best regards,<br>Bus Map API Team</p>" +
                        "</body>" +
                        "</html>",
                username, newPassword);

        sendEmail(toEmail, subject, body);
    }
}

package pl.kk.services.reporting.service;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private static final String DEFAULT_CONTENT_TYPE = "text/plain";
    private static final String HTML_CONTENT_TYPE = "text/html";
    private static final String RECIPIENT_PROPERTY = "reporting.mail.recipient";

    private final JavaMailSender emailSender;
    private final Environment environment;
    private final List<MimeMessage> messagesToSend = Lists.newCopyOnWriteArrayList();

    @Autowired
    public EmailService(JavaMailSender emailSender, Environment environment) {
        this.emailSender = emailSender;
        this.environment = environment;
    }

    public void send(String subject, String content) {
        send(subject, content, DEFAULT_CONTENT_TYPE);
    }

    public void sendHtml(String subject, String content) {
        send(subject, content, HTML_CONTENT_TYPE);
    }

    @Scheduled(cron = "0 0 */1 * * * ")
    public void tryResendFailedMessages() {
        for (MimeMessage message : messagesToSend) {
            try {
                emailSender.send(message);
                messagesToSend.remove(message);
            } catch (Exception e) {
                LOGGER.error("Exception occurred while trying to resend email", e);
            }
        }
    }

    private void send(String subject, String content, String type) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(environment.getProperty(RECIPIENT_PROPERTY)));
            message.setSubject(subject);
            message.setContent(content, type);
            emailSender.send(message);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while trying to send email", e);
            messagesToSend.add(message);
        }
    }
}


package com.company.ems.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendProjectAssignmentEmail(String toEmail, String employeeName, String projectName, LocalDate startDate, LocalDate endDate) {
        try {
            logger.info("Sending project assignment email to: {}", toEmail);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("New Project Assigned: " + projectName);

            String content = String.format(
                "Dear %s,<br><br>" +
                "We are pleased to inform you that you have been assigned to the project <b>%s</b>. " +
                "Your role in this project will begin from <b>%s</b> and is expected to continue until <b>%s</b>.<br><br>" +
                "Please find the project details below:<br>" +
                "Project Name: <b>%s</b><br>" +
                "Start Date: <b>%s</b><br>" +
                "End Date: <b>%s</b><br><br>" +
                "Kindly review the project requirements and ensure timely completion of your assigned tasks. " +
                "If you have any questions or require further clarification, please feel free to reach out.<br><br>" +
                "We wish you the best in successfully contributing to this project.<br><br>" +
                "Best Regards,<br>Employee Management System",
                employeeName, projectName, startDate, endDate, projectName, startDate, endDate
            );

            helper.setText(content, true);

            mailSender.send(message);
            logger.info("Email sent successfully to: {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}

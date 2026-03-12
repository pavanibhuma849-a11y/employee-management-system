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
                "<html>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;'>" +
                "  <h2 style='color: #2c3e50; border-bottom: 2px solid #2c3e50; padding-bottom: 10px;'>Project Assignment Notification</h2>" +
                "  <p>Dear <strong>%s</strong>,</p>" +
                "  <p>We are pleased to inform you that you have been assigned to the project <strong>%s</strong>. " +
                "  Your role in this project will begin from <strong>%s</strong> and is expected to continue until <strong>%s</strong>.</p>" +
                "  <div style='background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 5px solid #2c3e50;'>" +
                "    <h3 style='margin-top: 0; color: #2c3e50;'>Project Details:</h3>" +
                "    <p style='margin: 5px 0;'><strong>Project Name:</strong> %s</p>" +
                "    <p style='margin: 5px 0;'><strong>Start Date:</strong> %s</p>" +
                "    <p style='margin: 5px 0;'><strong>End Date:</strong> %s</p>" +
                "  </div>" +
                "  <p>Kindly review the project requirements and ensure timely completion of your assigned tasks. " +
                "  If you have any questions or require further clarification, please feel free to reach out.</p>" +
                "  <p>We wish you the best in successfully contributing to this project.</p>" +
                "  <hr style='border: 0; border-top: 1px solid #ddd; margin: 20px 0;'>" +
                "  <p style='font-size: 0.9em; color: #7f8c8d;'>Best Regards,<br><strong>Employee Management System</strong></p>" +
                "</div>" +
                "</body>" +
                "</html>",
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

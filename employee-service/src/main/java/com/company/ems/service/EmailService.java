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
                "<body style='background-color: #f4f7f6; font-family: Arial, sans-serif; margin: 0; padding: 20px;'>" +
                "  <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>" +
                "    <div style='background-color: #243e60; color: #ffffff; padding: 25px; text-align: center; font-size: 28px; font-weight: bold; letter-spacing: 2px;'>" +
                "      EMS" +
                "    </div>" +
                "    <div style='padding: 35px; color: #333333; line-height: 1.6;'>" +
                "      <h2 style='color: #243e60; margin-top: 0; border-bottom: 1px solid #eee; padding-bottom: 10px;'>Project Assignment Notification</h2>" +
                "      <p>Dear <strong>%s</strong>,</p>" +
                "      <p>We are pleased to inform you that you have been assigned to the project <strong>%s</strong>. " +
                "      Your role in this project will begin from <strong>%s</strong> and is expected to continue until <strong>%s</strong>.</p>" +
                "      " +
                "      <div style='background-color: #f8f9fa; border: 1px solid #e9ecef; border-radius: 8px; padding: 25px; margin: 25px 0; border-left: 5px solid #243e60;'>" +
                "        <h3 style='margin-top: 0; color: #243e60;'>Project Details:</h3>" +
                "        <p style='margin: 10px 0;'><strong>Project Name:</strong> %s</p>" +
                "        <p style='margin: 10px 0;'><strong>Start Date:</strong> %s</p>" +
                "        <p style='margin: 10px 0;'><strong>End Date:</strong> %s</p>" +
                "      </div>" +
                "" +
                "      <p>Kindly review the project requirements and ensure timely completion of your assigned tasks. " +
                "      If you have any questions or require further clarification, please feel free to reach out.</p>" +
                "      <p>We wish you the best in successfully contributing to this project.</p>" +
                "      " +
                "      <p style='margin-top: 35px; border-top: 1px solid #eee; padding-top: 20px;'>" +
                "        Best Regards,<br>" +
                "        <strong>Employee Management System</strong>" +
                "      </p>" +
                "    </div>" +
                "    <div style='background-color: #f8f9fa; color: #777777; padding: 20px; text-align: center; font-size: 12px; border-top: 1px solid #eeeeee;'>" +
                "      © 2026 EMS Platform. All rights reserved." +
                "    </div>" +
                "  </div>" +
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

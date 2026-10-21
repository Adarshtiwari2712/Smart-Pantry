package com.smartpantry.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine; // HTML template render karne ke liye

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendOtpEmail(String toEmail, String otp) {
        try {
            // 1. HTML Template me dynamic data (OTP) pass karna
            Context context = new Context();
            context.setVariable("otp", otp);

            // 2. resources/templates/otp-email.html ko process karna
            String htmlBody = templateEngine.process("otp-email", context);

            // 3. MimeMessage (HTML Mail) create karna
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "SmartPantry Verification");
            helper.setTo(toEmail);
            helper.setSubject("Your SmartPantry Verification Code");
            helper.setText(htmlBody, true); // true = HTML render hoga

            mailSender.send(message);
            System.out.println("HTML OTP Email sent successfully to: " + toEmail);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send HTML OTP Email: " + e.getMessage());
            return false;
        }
    }
    public boolean sendExpiryAlertEmail(String toEmail, String itemName, String expiryDate, int daysLeft) {
        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("SmartPantry Alert: " + itemName + " is expiring soon!");
            message.setText("Hello,\n\nYour item '" + itemName + "' is expiring on " + expiryDate
                    + " (" + daysLeft + " day(s) left).\n\nPlease consume or restock it!");

            mailSender.send(message);
            System.out.println("Expiry alert email sent successfully to: " + toEmail);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send expiry alert email: " + e.getMessage());
            return false;
        }
    }
}


/*package com.smartpantry.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine; // HTML template render karne ke liye

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendOtpEmail(String toEmail, String otp) {
        try {
            // 1. HTML Template me dynamic data (OTP) pass karna
            Context context = new Context();
            context.setVariable("otp", otp);

            // 2. resources/templates/otp-email.html ko process karna
            String htmlBody = templateEngine.process("otp-email", context);

            // 3. MimeMessage (HTML Mail) create karna
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "SmartPantry Verification");
            helper.setTo(toEmail);
            helper.setSubject("Your SmartPantry Verification Code");
            helper.setText(htmlBody, true); // true = HTML render hoga

            mailSender.send(message);
            System.out.println("HTML OTP Email sent successfully to: " + toEmail);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send HTML OTP Email: " + e.getMessage());
            return false;
        }
    }
}

 */
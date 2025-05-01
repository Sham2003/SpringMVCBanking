package com.banking.service;

import com.banking.exceptions.exps.AuthExceptions.InvalidOtpException;
import com.banking.exceptions.exps.AuthExceptions.NoSuchRequestException;
import com.banking.exceptions.exps.AuthExceptions.ExpiredOtpException;
import com.banking.model.OtpRequest;
import com.banking.repository.OtpRequestRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class OtpService {
    @Getter
    public enum EMAIL_TYPE {
        ACTIVATION_CODE ("activation_code","Activation Code"),
        PASSWORD_RESET ("password_reset" ,"Password Reset"),
        ACCOUNT_LOCKED ("account_locked","Account Locked"),
        TRANSACTION_PWD_RESET ("transaction_password_reset","Transaction Password Reset"),
        TRANSACTION_OTP ("transaction_otp","Transaction OTP"),;
        private final String template;
        private final String subject;

        EMAIL_TYPE(String template, String subject) {
            this.template = template;
            this.subject = subject;
        }
    }

    private final JavaMailSender mailSender;
    private final OtpRequestRepository otpRequestRepository;
    private final SpringTemplateEngine springTemplateEngine;


    public void verifyOtp(String email,String reqId,String otp){

        OtpRequest otpRequest = otpRequestRepository.findById(UUID.fromString(reqId))
                .orElseThrow(() -> new NoSuchRequestException("Request not found"));
        if(otpRequest.isExpired())
            throw new ExpiredOtpException("Otp Request Expired !!!");
        if(!otpRequest.checkOtp(otp))
            throw new InvalidOtpException("OTP does not match");
        if(!otpRequest.checkEmail(email))
            throw new NoSuchRequestException("Email does not match");
        otpRequest.setStatus(OtpRequest.OTP_STATUS.EXPIRED);
    }

    public UUID makePasswordResetRequest(String email) {
        OtpRequest otpRequest = OtpRequest.forEmail(email,OtpRequest.OTP_TYPE.PASSWORD_RESET,"FORGOT PASSWORD OTP REQUEST");
        otpRequestRepository.save(otpRequest);
        System.out.println("Sent OTP to " + email + " OTP :" + otpRequest.getOtp());
        Map<String, Object> context = new HashMap<>();
        context.put("username", email);
        context.put("otp", otpRequest.getOtp());
        sendEmail(email,EMAIL_TYPE.PASSWORD_RESET,context);
        return otpRequest.getId();
    }


    public UUID makeActivationCodeRequest(String email) {
        OtpRequest otpRequest = OtpRequest.forEmail(email,OtpRequest.OTP_TYPE.ACTIVATION_CODE,"ACTIVATION CODE REQUEST");
        otpRequestRepository.save(otpRequest);
        System.out.println("Sent Activation code to " + email + " OTP :" + otpRequest.getOtp());
        Map<String, Object> context = new HashMap<>();
        context.put("username", email);
        context.put("activation_code", otpRequest.getOtp());
        context.put("confirmationUrl", "https://localhost:4200");
        sendEmail(email,EMAIL_TYPE.ACTIVATION_CODE,context);
        return otpRequest.getId();
    }

    public UUID makeTransactionPasswordRequest(String email, String accountNumber) {
        OtpRequest otpRequest = OtpRequest.forEmail(email, OtpRequest.OTP_TYPE.TRANSACTION_PASSWORD_CHANGE,"CHANGE TRANSACTION PASSWORD REQUEST FOR " + accountNumber);
        otpRequestRepository.save(otpRequest);
        System.out.println("Sent OTP to " + email + " OTP :" + otpRequest.getOtp());
        Map<String, Object> context = new HashMap<>();
        context.put("username", email);
        context.put("accountNumber", accountNumber);
        context.put("otp", otpRequest.getOtp());
        sendEmail(email,EMAIL_TYPE.TRANSACTION_PWD_RESET,context);
        return otpRequest.getId();
    }


    @Async
    @SneakyThrows
    public void sendEmail(String to,EMAIL_TYPE emailType, Map<String,Object> parameters){
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                UTF_8.name()
        );
        helper.setFrom("contact@gmail.com");
        helper.setTo(to);
        helper.setSubject(emailType.getSubject());
        Context context = new Context();
        context.setVariables(parameters);

        String template = springTemplateEngine.process(emailType.getTemplate(), context);
        helper.setText(template, true);
        try {
            mailSender.send(mimeMessage);
        }catch (MailException e){
            System.out.println(e.getMessage());
        }
    }
}

package com.banking.service;

import com.banking.exceptions.exps.AuthExceptions.InvalidOtpException;
import com.banking.exceptions.exps.AuthExceptions.NoSuchRequestException;
import com.banking.exceptions.exps.AuthExceptions.ExpiredOtpException;
import com.banking.model.OtpRequest;
import com.banking.repository.OtpRequestRepository;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpService {

    private final JavaMailSender mailSender;
    private final OtpRequestRepository otpRequestRepository;

    @Autowired
    public OtpService(JavaMailSender mailSender, OtpRequestRepository otpRequestRepository) {
        this.mailSender = mailSender;
        this.otpRequestRepository = otpRequestRepository;
    }

    public static String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Always 6 digits
        return String.valueOf(otp);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        System.out.println("===================================" );
        System.out.println("Sending email to " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("===================================" );
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("sham.testing1997@gmail.com"); // Use your verified sender email
        mailSender.send(message);
    }


    public void sendAccountLockedEmail(String toEmail, LocalDateTime unlockTime) {
        String subject = "Account Locked Due to Failed Login Attempts";
        String body = "Your account has been locked due to 3 failed login attempts.\n\n" +
                "You can try again after: " + unlockTime + ".\n\n" +
                "If this wasn't you, please secure your account immediately.";
        sendEmail(toEmail, subject, body);
    }


    public void sendOtpEmail(String to, String otp)  {
        String subject = "Your OTP for Verification";
        String body = "Hello,\n\n" +
                "Your OTP for verification is:\n\n" +
                otp + "\n\n" +
                "This OTP is valid for one-time use only.\n\n" +
                "Regards,\nSample App Team";
        sendEmail(to, subject, body);
    }

    public void sendPasswordRestMail(String email,String otp)  {
        String subject = "Your OTP for Reset Password Verification";
        String body = "Hello,\n\n" +
                "Your OTP for Reset Password is:\n\n" +
                otp + "\n\n" +
                "This OTP is valid for one-time use only.\n\n" +
                "Regards,\nSample App Team";
        sendEmail(email, subject, body);
    }

    public UUID makePasswordResetRequest(String email) {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(email);
        otpRequest.setOtp(generateOtp());
        otpRequest.setType(OtpRequest.OTP_TYPE.PASSWORD_RESET);
        otpRequest.setStatus(OtpRequest.OTP_STATUS.ONGOING);
        otpRequest.setMessage("INITIAL PASSWORD RESET REQUEST");
        otpRequestRepository.save(otpRequest);
        System.out.println("Sent OTP to " + email + " OTP :" + otpRequest.getOtp());
        //sendPasswordRestMail(email,otpRequest.getOtp());
        return otpRequest.getId();
    }

    public void verifyOtp(String email,String reqId,String otp){

        OtpRequest otpRequest = otpRequestRepository.findById(UUID.fromString(reqId))
                .orElseThrow(() -> new NoSuchRequestException("Request not found"));
        if(otpRequest.getStatus() != OtpRequest.OTP_STATUS.ONGOING)
            throw new ExpiredOtpException("Otp Request Expired Retry !!!");
        if(!otpRequest.getOtp().equals(otp))
            throw new InvalidOtpException("OTP does not match");
        if(!otpRequest.getEmail().equals(email))
            throw new NoSuchRequestException("Email does not match");
        otpRequest.setStatus(OtpRequest.OTP_STATUS.EXPIRED);
    }

    public UUID makeActivationCodeRequest(@Email String email) {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(email);
        otpRequest.setOtp(generateOtp());
        otpRequest.setType(OtpRequest.OTP_TYPE.ACTIVATION_CODE);
        otpRequest.setStatus(OtpRequest.OTP_STATUS.ONGOING);
        otpRequest.setMessage("INITIAL PASSWORD RESET REQUEST");
        otpRequestRepository.save(otpRequest);
        System.out.println("Sent OTP to " + email + " OTP :" + otpRequest.getOtp());
        //sendOtpEmail(email, otpRequest.getOtp());
        return otpRequest.getId();
    }

    public UUID makeTransactionPasswordRequest(String email, String accountNumber) {
        OtpRequest otpRequest = new OtpRequest();
        otpRequest.setEmail(email);
        otpRequest.setOtp(generateOtp());
        otpRequest.setType(OtpRequest.OTP_TYPE.TRANSACTION_PASSWORD_CHANGE);
        otpRequest.setStatus(OtpRequest.OTP_STATUS.ONGOING);
        otpRequest.setMessage("CHANGE TRANSACTION PASSWORD REQUEST FOR " + accountNumber);
        otpRequestRepository.save(otpRequest);
        System.out.println("Sent OTP to " + email + " OTP :" + otpRequest.getOtp());
        //sendOtpEmail(email, otpRequest.getOtp());
        return otpRequest.getId();
    }
}

package com.masil.backend.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.masil.backend.repository.MasilMemberRepository;
import com.masil.backend.util.Redis.RedisUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MasilMailSendService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private  RedisUtil redisUtil;

    @Autowired
    private MasilMemberRepository memberRepository;

    private int authNumber;

    // 이메일이 데이터베이스에 존재하는지 확인
    public boolean isEmailExists(String email) {
        return memberRepository.existsByUserEmail(email);
    }

    //임의의 6자리 양수를 반환합니다.
    public void makeRandomNumber() {
        Random r = new Random();
        String randomNumber = "";
        for (int i = 0; i < 6; i++) {
            randomNumber += Integer.toString(r.nextInt(10));
        }

        authNumber = Integer.parseInt(randomNumber);
    }

    public String joinEmail(String email) {
        makeRandomNumber();
        String setFrom = "dkrlskfk1@gmail.com"; // email-config에 설정한 자신의 이메일 주소를 입력
        String toMail = email;
        String title = "Masil 회원 가입 인증 이메일 입니다."; // 이메일 제목
        String content = "Masil을 방문해주셔서 감사합니다." +
            "<br><br>" +
            "인증 번호는 " + authNumber + "입니다." +
            "<br>" +
            "인증번호를 제대로 입력해주세요"; //이메일 내용
        mailSend(setFrom, toMail, title, content);
        return Integer.toString(authNumber);
    }

    //이메일을 전송합니다.
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(setFrom); //이메일의 발신자 주소 설정
            helper.setTo(toMail); //이메일의 수신자 주소 설정
            helper.setSubject(title); //이메일의 제목을 설정
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) { // 이메일 오류시
            e.printStackTrace();
        }
        redisUtil.setDataExpire(Integer.toString(authNumber),toMail,60*5L); // 인증번호 유효시간을 5분으로 제한
    }

    // 이메일 인증번호 확인
    public boolean CheckAuthNum(String email, String authNum) {
    	if(redisUtil.getData(authNum) != null && redisUtil.getData(authNum).equals(email)){
    		return true;
    	}else {
    		return false;
    	}
    }

    // 이메일 인증번호 재발송
    public String resendEmail(String email) {
        // 기존 인증번호 삭제
        String existingAuthNum = redisUtil.getDataByValue(email);
        if (existingAuthNum != null) {
            redisUtil.deleteData(existingAuthNum);
        }
        // 새로운 인증번호 생성 및 발송
        return joinEmail(email);
    }


}
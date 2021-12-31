package cn.raxcl.controller.test;

import cn.raxcl.model.vo.Result;
import cn.raxcl.util.MailUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author c-long.chan
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final MailUtils mailUtils;
    private final JavaMailSender javaMailSender;


    public TestController(MailUtils mailUtils, JavaMailSender javaMailSender) {
        this.mailUtils = mailUtils;
        this.javaMailSender = javaMailSender;
    }

    @GetMapping("/mail")
    public Result mail() throws MessagingException {
                MimeMessage mimeMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
                messageHelper.setFrom("raxcl@qq.com");
                messageHelper.setTo("raxcl@qq.com");
                messageHelper.setSubject("你好");
                messageHelper.setText("测试内容");
                javaMailSender.send(mimeMessage);
        return Result.ok("密码正确");
    }
}

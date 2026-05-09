package in.vk.main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Override
	public boolean sendEmail(String to, String subject, String body) {
		boolean isSent = false;
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(to);
			message.setSubject(subject);
			message.setText(body);
			message.setFrom("vishalk35976@gmail.com");

			mailSender.send(message);
			isSent = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSent;
	}

    @Override
    public boolean sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String attachmentName) {
        boolean isSent = false;
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true);
            
            helper.setFrom("vishalk35976@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);
            
            helper.addAttachment(attachmentName, new org.springframework.core.io.ByteArrayResource(attachment));
            
            mailSender.send(message);
            isSent = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSent;
    }
}

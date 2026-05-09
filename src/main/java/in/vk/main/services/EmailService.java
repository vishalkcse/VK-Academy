package in.vk.main.services;

public interface EmailService {
	public boolean sendEmail(String to, String subject, String body);
	public boolean sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String attachmentName);
}

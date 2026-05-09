package in.vk.main.controllers;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.vk.main.entities.User;
import in.vk.main.repositories.UserRepository;
import in.vk.main.services.EmailService;
import in.vk.main.services.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class PasswordResetController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;

	@GetMapping("/forgotPassword")
	public String openForgotPasswordPage() {
		return "forgot-password";
	}

	@PostMapping("/forgotPasswordForm")
	public String handleForgotPassword(@RequestParam("email") String email, Model model, HttpSession session) {
		System.out.println("Forgot Password Request for Email: " + email);
		try {
			if (userRepository == null) {
				System.out.println("DEBUG: userRepository is NULL!");
				model.addAttribute("errorMsg", "System Error: Repository not found.");
				return "forgot-password";
			}

			User user = userRepository.findFirstByEmail(email);
			if (user != null) {
				// Generate 6-digit OTP
				Random random = new Random();
				int otp = 100000 + random.nextInt(900000);
				
				session.setAttribute("otp", String.valueOf(otp));
				session.setAttribute("resetEmail", email);

				String subject = "Password Reset OTP - VK Academy";
				String body = "Hi " + user.getName() + ",\n\n" +
				              "Your OTP for resetting your VK Academy password is: " + otp + "\n" +
						      "Please do not share this OTP with anyone.\n\n" +
				              "Best Regards,\n" +
						      "VK Academy Team";
				
				System.out.println("Sending OTP to: " + email);
				boolean isSent = emailService.sendEmail(email, subject, body);
				
				if(!isSent) {
					model.addAttribute("errorMsg", "Failed to send OTP. Please check your email configuration.");
					return "forgot-password";
				}

				model.addAttribute("email", email);
				return "reset-password";
			} else {
				model.addAttribute("errorMsg", "Email id not registered with us!");
				return "forgot-password";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "An unexpected error occurred: " + e.getMessage());
			return "forgot-password";
		}
	}

	@PostMapping("/resetPasswordForm")
	public String handleResetPassword(@RequestParam("email") String email, 
									  @RequestParam("otp") String userOtp,
									  @RequestParam("newPassword") String newPassword, 
									  Model model, 
									  HttpSession session) {
		try {
			String sessionOtp = (String) session.getAttribute("otp");
			String sessionEmail = (String) session.getAttribute("resetEmail");

			if (sessionOtp != null && sessionOtp.equals(userOtp) && sessionEmail != null && sessionEmail.equals(email)) {
				userService.resetPassword(email, newPassword);
				
				session.removeAttribute("otp");
				session.removeAttribute("resetEmail");
				
				model.addAttribute("successMsg", "Password reset successfully! Please login.");
				return "login";
			} else {
				model.addAttribute("email", email);
				model.addAttribute("errorMsg", "Invalid OTP or Email! Please try again.");
				return "reset-password";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "An unexpected error occurred during reset: " + e.getMessage());
			return "reset-password";
		}
	}
}

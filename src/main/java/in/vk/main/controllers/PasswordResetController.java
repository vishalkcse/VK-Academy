package in.vk.main.controllers;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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

	// Thread-safe in-memory cache for OTPs to prevent session loss issues
	private static final ConcurrentHashMap<String, String> otpCache = new ConcurrentHashMap<>();

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

			String cleanEmail = email != null ? email.trim() : "";
			User user = userRepository.findFirstByEmail(cleanEmail);
			if (user == null && cleanEmail.contains("@")) {
				user = userRepository.findFirstByEmail(cleanEmail.toLowerCase());
			}

			if (user != null) {
				// Generate 6-digit OTP
				Random random = new Random();
				int otp = 100000 + random.nextInt(900000);
				String otpStr = String.valueOf(otp);
				
				session.setAttribute("otp", otpStr);
				session.setAttribute("resetEmail", user.getEmail());
				otpCache.put(user.getEmail().toLowerCase(), otpStr);

				String subject = "Password Reset OTP - VK Academy";
				String body = "Hi " + user.getName() + ",\n\n" +
				              "Your OTP for resetting your VK Academy password is: " + otpStr + "\n" +
						      "Please do not share this OTP with anyone.\n\n" +
				              "Best Regards,\n" +
						      "VK Academy Team";
				
				System.out.println("Sending OTP to: " + user.getEmail());
				boolean isSent = emailService.sendEmail(user.getEmail(), subject, body);
				
				if(!isSent) {
					model.addAttribute("errorMsg", "Failed to send OTP. Please check your email configuration.");
					return "forgot-password";
				}

				model.addAttribute("email", user.getEmail());
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
	public String handleResetPassword(@RequestParam(name = "email", required = false) String email, 
									  @RequestParam("otp") String userOtp,
									  @RequestParam("newPassword") String newPassword, 
									  Model model, 
									  HttpSession session) {
		try {
			String sessionEmail = (String) session.getAttribute("resetEmail");
			String targetEmail = (email != null && !email.trim().isEmpty()) ? email.trim() : sessionEmail;
			if (targetEmail != null) targetEmail = targetEmail.trim();

			String cleanTargetKey = targetEmail != null ? targetEmail.toLowerCase() : "";
			String cleanOtp = userOtp != null ? userOtp.trim() : "";

			String sessionOtp = (String) session.getAttribute("otp");
			String cachedOtp = !cleanTargetKey.isEmpty() ? otpCache.get(cleanTargetKey) : null;

			boolean isOtpValid = (sessionOtp != null && sessionOtp.trim().equals(cleanOtp)) ||
								 (cachedOtp != null && cachedOtp.trim().equals(cleanOtp));

			if (targetEmail != null && isOtpValid) {
				userService.resetPassword(targetEmail, newPassword);
				
				if (!cleanTargetKey.isEmpty()) {
					otpCache.remove(cleanTargetKey);
				}
				session.removeAttribute("otp");
				session.removeAttribute("resetEmail");
				
				model.addAttribute("successMsg", "Password reset successfully! Please login with your new password.");
				return "login";
			} else {
				model.addAttribute("email", targetEmail);
				model.addAttribute("errorMsg", "Invalid OTP or Email! Please check your OTP and try again.");
				return "reset-password";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "An unexpected error occurred during reset: " + e.getMessage());
			return "reset-password";
		}
	}
}

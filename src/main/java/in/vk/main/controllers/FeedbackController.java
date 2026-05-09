package in.vk.main.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import in.vk.main.entities.Feedback;
import in.vk.main.entities.User;
import in.vk.main.services.FeedbackService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@SessionAttributes("sessionUser")
public class FeedbackController 
{
	@Autowired
	private FeedbackService feedbackService;
	
	@GetMapping("/provideFeedback")
	public String openProvideFeedbackPage(Model model)
	{
		model.addAttribute("feedback", new Feedback());
		
		return "provide-feedback";
	}
	
	@PostMapping("/feedbackForm")
	public String handleFeedbackForm(@ModelAttribute("feedback") Feedback feedback, Model model)
	{
		feedback.setDateOfFeedback(LocalDate.now().toString());
	    feedback.setTimeOfFeedback(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
	    feedback.setReadStatus("unread");
	    
	    try
	    {
	        feedbackService.sendFeedback(feedback);
	        model.addAttribute("successMsg", "Feedback sent successfully, thank you..!!");
	    }
	    catch(Exception e)
	    {
	        model.addAttribute("errorMsg", "Feedback not sent due to some error, please try again later..!!");
	        e.printStackTrace();
	    }

	    return "provide-feedback";
	}

	@GetMapping("/enrollBatch")
	public String enrollBatch(@RequestParam("batch") String batch, HttpSession session, RedirectAttributes redirectAttributes) {
	    User user = (User) session.getAttribute("sessionUser");
	    if (user != null) {
	        Feedback f = new Feedback();
	        f.setUserName(user.getName());
	        f.setUserEmail(user.getEmail());
	        f.setUserFeedback("Live Batch Enrollment Request for: " + batch + ". Phone: " + user.getPhoneno());
	        f.setDateOfFeedback(LocalDate.now().toString());
	        f.setTimeOfFeedback(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
	        f.setReadStatus("unread");
	        
	        try {
	            feedbackService.sendFeedback(f);
	            redirectAttributes.addFlashAttribute("successMsg", "Enrollment request sent successfully! Our team will contact you shortly.");
	        } catch(Exception e) {
	            redirectAttributes.addFlashAttribute("errorMsg", "Enrollment request failed, please try again.");
	            e.printStackTrace();
	        }
	    } else {
	        redirectAttributes.addFlashAttribute("errorMsg", "To enroll in a live batch, please register an account first or login below.");
	        return "redirect:/login";
	    }
	    return "redirect:/index";
	}

	@GetMapping("/api/notifications")
	@ResponseBody
	public List<Feedback> getMyNotifications(HttpSession session) {
	    User user = (User) session.getAttribute("sessionUser");
	    if (user != null) {
	        return feedbackService.getFeedbackByUserEmail(user.getEmail());
	    }
	    return null;
	}
}

package in.vk.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.vk.main.entities.Course;
import in.vk.main.entities.Batch;
import in.vk.main.entities.Coupon;
import in.vk.main.entities.Feedback;
import in.vk.main.entities.TickerContent;
import in.vk.main.entities.User;
import in.vk.main.services.CourseService;
import in.vk.main.services.BatchService;
import in.vk.main.services.CouponService;
import in.vk.main.services.FeedbackService;
import in.vk.main.services.UserService;
import in.vk.main.repositories.TickerRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * AdminController handles restricted management operations.
 * It provides the interface for staff to moderate content and users.
 */
@Controller
public class AdminController 
{
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private CouponService couponService;

	@Autowired
	private TickerRepository tickerRepository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private BatchService batchService;
	
	@GetMapping("/adminLogin")
	public String openAdminLoginPage()
	{
		return "admin-login";
	}
	
	@PostMapping("/adminLoginForm")
	public String adminLoginForm(@RequestParam("adminemail") String aemail, @RequestParam("adminpass") String apass, Model model)
	{
		/* Admin credentials are kept separate from the student database for enhanced security */
		if(aemail.equals("admin@gmail.com") && apass.equals("admin123"))
		{
			return "redirect:/adminProfile";
		}
		else
		{
			model.addAttribute("errorMsg", "Invalid email id or password");
			return "admin-login";
		}
	}
	
	@GetMapping("/courseManagement")
	public String openCourseManagementPage(Model model,
					@RequestParam(name="page", defaultValue = "0") int page,
					@RequestParam(name="size", defaultValue = "4") int size)
	{
		/* We use Pagination here to ensure the admin dashboard remains fast as the course catalog grows */
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Course> coursesPage = courseService.getAllCourseDetailsByPagination(pageable);
		
		model.addAttribute("coursesPage", coursesPage);
		
		return "course-management";
	}
	
	//-------------admin logout------------------------
	@GetMapping("/adminLogout")
	public String adminLogout(SessionStatus sessionStatus)
	{
		sessionStatus.setComplete();
		return "admin-login";
	}

	//-------------registered users-------------------
	@GetMapping("/adminUsers")
	public String openUsersPage(Model model)
	{
		model.addAttribute("usersList", userService.getAllUsers());
		return "view-users";
	}

	@GetMapping("/deleteUser")
	public String deleteUser(@RequestParam("id") Long id, RedirectAttributes redirectAttributes)
	{
		try {
			userService.deleteUserById(id);
			redirectAttributes.addFlashAttribute("successMsg", "User deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMsg", "Failed to delete user. They might have active orders.");
		}
		return "redirect:/adminUsers";
	}
	
	
	//-------------feedback----------------------------
	@GetMapping("/adminFeedback")
	public String openFeedbackPage(Model model,
			@RequestParam(name="page", defaultValue = "0") int page,
			@RequestParam(name="size", defaultValue = "4") int size)
	{
		Pageable pageable = PageRequest.of(page, size);
		
		Page<Feedback> feedbackPage = feedbackService.getAllFeedbacksByPagination(pageable);
		
		model.addAttribute("feedbackPage", feedbackPage);
		
		return "view-feedbacks";
	}
	
    @PostMapping("/updateFeedbackStatus")
    public String updateFeedbackStatus(@RequestParam("id") Long id, @RequestParam("status") String status, RedirectAttributes redirectAttributes)
    {
        boolean success = feedbackService.updateFeedbackStatus(id, status);
        if (success) {
            redirectAttributes.addFlashAttribute("successMsg", "Feedback updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to update feedback status.");
        }
        return "redirect:/adminFeedback"; // Redirect to the page where feedbacks are listed
    }

	@GetMapping("/deleteFeedback")
	public String deleteFeedback(@RequestParam("id") Long id, RedirectAttributes redirectAttributes)
	{
		try {
			feedbackService.deleteFeedbackById(id);
			redirectAttributes.addFlashAttribute("successMsg", "Feedback deleted successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMsg", "Failed to delete feedback");
		}
		return "redirect:/adminFeedback";
	}

	//-------------Coupon Management-------------------
	@GetMapping("/couponManagement")
	public String openCouponManagementPage(Model model)
	{
		List<Coupon> coupons = couponService.getAllCoupons();
		model.addAttribute("coupons", coupons);
		return "coupon-management";
	}

	@PostMapping("/addCoupon")
	public String addCoupon(@RequestParam("code") String code, @RequestParam("discount") int discount, RedirectAttributes redirectAttributes)
	{
		Coupon coupon = new Coupon();
		coupon.setCode(code);
		coupon.setDiscountPercentage(discount);
		couponService.addCoupon(coupon);
		redirectAttributes.addFlashAttribute("successMsg", "Coupon added successfully");
		return "redirect:/couponManagement";
	}

	@GetMapping("/deleteCoupon")
	public String deleteCoupon(@RequestParam("id") Long id, RedirectAttributes redirectAttributes)
	{
		couponService.deleteCouponById(id);
		redirectAttributes.addFlashAttribute("successMsg", "Coupon deleted successfully");
		return "redirect:/couponManagement";
	}

	//-------------Ticker Management-------------------
	@GetMapping("/tickerManagement")
	public String openTickerManagementPage(Model model)
	{
		List<TickerContent> list = tickerRepository.findAll();
		if(!list.isEmpty()) {
			model.addAttribute("tickerContent", list.get(0).getContent());
		} else {
			model.addAttribute("tickerContent", "");
		}
		return "ticker-management";
	}

	@PostMapping("/updateTicker")
	public String updateTicker(@RequestParam("content") String content, RedirectAttributes redirectAttributes)
	{
		try {
			List<TickerContent> list = tickerRepository.findAll();
			TickerContent ticker;
			if(!list.isEmpty()) {
				ticker = list.get(0);
			} else {
				ticker = new TickerContent();
			}
			ticker.setContent(content);
			tickerRepository.save(ticker);
			redirectAttributes.addFlashAttribute("successMsg", "Ticker updated successfully");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMsg", "Failed to update ticker");
		}
		return "redirect:/tickerManagement";
	}

	//-------------batch management-------------------
	@GetMapping("/batchManagement")
	public String openBatchManagementPage(Model model)
	{
		model.addAttribute("batchesList", batchService.getAllBatches());
		return "batch-management";
	}

	@PostMapping("/addBatch")
	public String addBatch(@ModelAttribute("batch") Batch batch, @RequestParam(value="batchImg", required=false) MultipartFile batchImg, RedirectAttributes redirectAttributes)
	{
		try {
			if(batchImg != null && !batchImg.isEmpty()) {
				Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
					"cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
					"api_key", System.getenv("CLOUDINARY_API_KEY"),
					"api_secret", System.getenv("CLOUDINARY_API_SECRET")
				));
				Map uploadResult = cloudinary.uploader().upload(batchImg.getBytes(), ObjectUtils.emptyMap());
				batch.setImageUrl((String) uploadResult.get("secure_url"));
			}
			batchService.saveBatch(batch);
			redirectAttributes.addFlashAttribute("successMsg", "Batch added successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMsg", "Failed to add batch.");
		}
		return "redirect:/batchManagement";
	}

	@PostMapping("/updateBatch")
	public String updateBatch(@ModelAttribute("batch") Batch batch, @RequestParam(value="batchImg", required=false) MultipartFile batchImg, RedirectAttributes redirectAttributes)
	{
		try {
			Batch oldBatch = batchService.getBatchById(batch.getId());
			if(batchImg != null && !batchImg.isEmpty()) {
				Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
					"cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
					"api_key", System.getenv("CLOUDINARY_API_KEY"),
					"api_secret", System.getenv("CLOUDINARY_API_SECRET")
				));
				Map uploadResult = cloudinary.uploader().upload(batchImg.getBytes(), ObjectUtils.emptyMap());
				batch.setImageUrl((String) uploadResult.get("secure_url"));
			} else {
				batch.setImageUrl(oldBatch.getImageUrl());
			}
			batchService.saveBatch(batch);
			redirectAttributes.addFlashAttribute("successMsg", "Batch updated successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMsg", "Failed to update batch.");
		}
		return "redirect:/batchManagement";
	}

	@GetMapping("/deleteBatch")
	public String deleteBatch(@RequestParam("id") Long id, RedirectAttributes redirectAttributes)
	{
		try {
			batchService.deleteBatchById(id);
			redirectAttributes.addFlashAttribute("successMsg", "Batch deleted successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMsg", "Failed to delete batch.");
		}
		return "redirect:/batchManagement";
	}
}

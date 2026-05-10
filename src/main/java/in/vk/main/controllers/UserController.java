package in.vk.main.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import in.vk.main.dto.PurchasedCourse;
import in.vk.main.entities.Course;
import in.vk.main.entities.Batch;
import in.vk.main.entities.User;
import in.vk.main.repositories.OrdersRepository;
import in.vk.main.repositories.UserRepository;
import in.vk.main.services.CourseService;
import in.vk.main.services.BatchService;
import in.vk.main.services.EmailService;
import in.vk.main.services.UserService;
import in.vk.main.services.CouponService;
import in.vk.main.services.InvoiceService;
import in.vk.main.services.WishlistService;
import in.vk.main.entities.Orders;
import in.vk.main.entities.Wishlist;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * UserController manages the primary student-facing application flows.
 * It follows the MVC pattern to separate request handling from business logic.
 */
@Controller
@SessionAttributes("sessionUser")
public class UserController
{
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CourseService courseService;
	
	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private InvoiceService invoiceService;
	
	@Autowired
	private WishlistService wishlistService;
	
	@Autowired
	private CouponService couponService;
	
	@Autowired
	private BatchService batchService;
	
	@GetMapping({"/", "/index"})
	public String openIndexPage(
			@org.springframework.web.bind.annotation.RequestParam(name="category", required=false) String category,
			@org.springframework.web.bind.annotation.RequestParam(name="search", required=false) String search,
			Model model, HttpSession session)
	{
		try {
			/* 
			 * We track the session user here to ensure the UI can dynamically 
			 * show/hide login buttons and personalized content.
			 */
			User sessionUser = (User) session.getAttribute("sessionUser");
			model.addAttribute("sessionUser", sessionUser);
			
			model.addAttribute("batchesList", batchService.getAllBatches());
			
			List<Course> coursesList;
			if(category != null && !category.isEmpty())
			{
				coursesList = courseService.getCoursesByCategory(category);
			}
			else if(search != null && !search.isEmpty())
			{
				coursesList = courseService.searchCoursesByName(search);
			}
			else
			{
				coursesList = courseService.getAllCourseDetails();
			}
			
			model.addAttribute("coursesList", coursesList);
			model.addAttribute("categoriesList", courseService.getAllCategories());
			model.addAttribute("category", category);
			model.addAttribute("search", search);
			
			/* 
			 * We fetch purchased course names separately to prevent students 
			 * from buying the same course twice by disabling the 'Buy' button in the UI.
			 */
			List<String> purchasedCoursesNameList = new ArrayList<>();
			if(sessionUser != null)
			{
				List<Object[]> purchasedCourseList = ordersRepository.findPurchasedCoursesByEmail(sessionUser.getEmail());
				
				for(Object[] course : purchasedCourseList)
				{
					if(course != null && course.length > 3) {
						String courseName = (String) course[3];
						purchasedCoursesNameList.add(courseName);
					}
				}
			}
			model.addAttribute("purchasedCoursesNameList", purchasedCoursesNameList);
			
			List<Long> wishlistCourseIds = new ArrayList<>();
			if(sessionUser != null)
			{
				List<Wishlist> wishlist = wishlistService.getWishlistByUser(sessionUser.getEmail());
				for(Wishlist item : wishlist)
				{
					wishlistCourseIds.add(item.getCourseId());
				}
			}
			model.addAttribute("wishlistCourseIds", wishlistCourseIds);
			
			model.addAttribute("availableCoupons", couponService.getAllCoupons());
			
			return "index";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "Exception in openIndexPage: " + e.toString());
			return "error";
		}
	}
	
	@GetMapping("/register")
	public String openRegisterPage(Model model)
	{
		model.addAttribute("user", new User());
		return "register";
	}
	
	@PostMapping("/regForm")
	public String handleRegForm(@Valid @ModelAttribute("user") User user, BindingResult result, Model model)
	{
		/* We use BindingResult to catch validation errors (like invalid email) before attempting DB storage */
		if(result.hasErrors())
		{
			return "register";
		}
		else
		{
			try
			{
				userService.registerUserService(user);
				model.addAttribute("successMsg", "Registered Successfully");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				model.addAttribute("errorMsg", "Registration Failed");
				return "error";
			}

			/* Send welcome email separately — if email fails, registration is still successful */
			try
			{
				String subject = "Welcome to VK Academy!";
				String body = "Hi " + user.getName() + ",\n\n" +
				              "Congratulations! Your registration with VK Academy is successful.\n" +
					          "You can now log in and start learning.\n\n" +
				              "Best Regards,\n" +
					          "Vishal Kumar (Founder, VK Academy)";
				emailService.sendEmail(user.getEmail(), subject, body);
			}
			catch(Exception e)
			{
				// Email sending failed silently — registration is already done
				e.printStackTrace();
			}

			return "register";
		}
	}
	
	@GetMapping("/login")
	public String openLoginPage(Model model)
	{
		model.addAttribute("user", new User());
		return "login";
	}
	
	@PostMapping("/loginForm")
	public String handleLoginForm(@ModelAttribute("user") User user, Model model)
	{
		try {
			boolean isAuthenticated = userService.loginUserService(user.getEmail(), user.getPassword());
			if(isAuthenticated)
			{
				User authenticatedUser = userRepository.findFirstByEmail(user.getEmail());
				
				if(authenticatedUser.isBanStatus())
				{
					model.addAttribute("errorMsg", "Sorry, your account is banned, please contact admin, thank you...!!");
					return "login";
				}
				model.addAttribute("sessionUser", authenticatedUser);
				
				return "redirect:/index";
			}
			else
			{
				model.addAttribute("errorMsg", "Incorrect Email id or Password");
				return "login";
			}
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "Login Error: " + e.getMessage());
			return "error";
		}
	}
	
	@GetMapping("/logout")
	public String logout(SessionStatus sessionStatus)
	{
		/* Clearing sessionStatus ensures all @SessionAttributes are properly destroyed for security */
		sessionStatus.setComplete();
		return "login";
	}
	
	@GetMapping("/userProfile")
	public String openUserProfile()
	{
		return "user-profile";
	}
	
	@GetMapping("/myCourses")
	public String myCoursesPage(@SessionAttribute("sessionUser") User sessionUser, Model model)
	{
		List<Object[]> pcDbList = ordersRepository.findPurchasedCoursesByEmail(sessionUser.getEmail());
		
		List<PurchasedCourse> purchasedCoursesList = new ArrayList<>();
		
		for(Object[] course : pcDbList)
		{
			PurchasedCourse purchasedCourse = new PurchasedCourse();
			
			purchasedCourse.setPurchasedOn((String)course[0]);
			purchasedCourse.setDescription((String)course[1]);
			purchasedCourse.setImageUrl((String)course[2]);
			purchasedCourse.setCourseName((String)course[3]);
			purchasedCourse.setUpdatedOn((String)course[4]);
			
			purchasedCoursesList.add(purchasedCourse);
		}
		
		model.addAttribute("purchasedCoursesList", purchasedCoursesList);
		
		return "my-courses";
	}

	@GetMapping("/downloadInvoice")
	public void downloadInvoice(@RequestParam("courseName") String courseName, 
								@SessionAttribute("sessionUser") User sessionUser, 
								HttpServletResponse response) {
		try {
			Orders order = ordersRepository.findByUserEmailAndCourseName(sessionUser.getEmail(), courseName);
			if (order != null) {
				response.setContentType("application/pdf");
				String headerKey = "Content-Disposition";
				String headerValue = "attachment; filename=invoice_" + courseName.replace(" ", "_") + ".pdf";
				response.setHeader(headerKey, headerValue);

				invoiceService.generateInvoice(order, sessionUser, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/addToWishlist")
	public String addToWishlist(@RequestParam("courseId") Long courseId, @RequestParam("courseName") String courseName, HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes)
	{
		User sessionUser = (User) session.getAttribute("sessionUser");
		if(sessionUser == null)
		{
			return "redirect:/login";
		}
		
		Wishlist wishlist = new Wishlist();
		wishlist.setUserEmail(sessionUser.getEmail());
		wishlist.setCourseId(courseId);
		wishlist.setCourseName(courseName);
		
		wishlistService.addToWishlist(wishlist);
		
		redirectAttributes.addFlashAttribute("successMsg", "Course added to wishlist");
		return "redirect:/index";
	}
	
	@GetMapping("/myWishlist")
	public String openMyWishlistPage(Model model, HttpSession session)
	{
		User sessionUser = (User) session.getAttribute("sessionUser");
		if(sessionUser == null)
		{
			return "redirect:/login";
		}
		
		List<Wishlist> wishlistItems = wishlistService.getWishlistByUser(sessionUser.getEmail());
		List<Course> wishlistCourses = new ArrayList<>();
		for(Wishlist item : wishlistItems)
		{
			Course course = courseService.getCourseDetailsById(item.getCourseId());
			if(course != null)
			{
				wishlistCourses.add(course);
			}
		}
		
		model.addAttribute("wishlistCourses", wishlistCourses);
		model.addAttribute("sessionUser", sessionUser);
		
		return "my-wishlist";
	}
	
	@GetMapping("/removeFromWishlist")
	public String removeFromWishlist(@RequestParam("courseId") Long courseId, HttpSession session)
	{
		User sessionUser = (User) session.getAttribute("sessionUser");
		if(sessionUser != null)
		{
			wishlistService.removeFromWishlist(sessionUser.getEmail(), courseId);
		}
		return "redirect:/myWishlist";
	}
}

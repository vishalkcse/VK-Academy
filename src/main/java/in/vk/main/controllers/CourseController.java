package in.vk.main.controllers;

import java.util.Map;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.vk.main.entities.Course;
import in.vk.main.services.CourseService;

@Controller
public class CourseController
{
	// Cloudinary configuration is picked from environment variables in the method
	@Autowired
	private CourseService courseService;
	
	//---------------add course starts-----------------------------
	@GetMapping("/addCourse")
	public String openAddCoursePage(Model model)
	{
		model.addAttribute("course", new Course());
		return "add-course";
	}
	
	@PostMapping("/addCourseForm")
	public String addCourseForm(@ModelAttribute("course") Course course, @RequestParam("courseImg") MultipartFile courseImg, Model model)
	{
		try
		{
			courseService.addCourse(course, courseImg);
			model.addAttribute("successMsg", "Course added successfully");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("errorMsg", "Course not added due to some error");
		}
		return "add-course";
	}
	//---------------add course ends-----------------------------
	
	
	//---------------edit course starts-----------------------------
	@GetMapping("/editCourse")
	public String openEditCoursePage(@RequestParam("id") Long id, Model model)
	{
		Course course = courseService.getCourseDetailsById(id);
		
		model.addAttribute("course", course);
		model.addAttribute("newCourseObj", new Course());
		
		return "edit-course";
	}
	
	@PostMapping("/updateCourseDetailsForm")
	public String updateCourseDetailsForm(@ModelAttribute("newCourseObj") Course newCourseObj, @RequestParam("courseImg") MultipartFile courseImg, RedirectAttributes redirectAttributes)
	{
		try
		{
			Course oldCourseObj = courseService.getCourseDetailsById(newCourseObj.getId());
			
			if(courseImg != null && !courseImg.isEmpty())
			{
				Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
					"cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
					"api_key", System.getenv("CLOUDINARY_API_KEY"),
					"api_secret", System.getenv("CLOUDINARY_API_SECRET")
				));
				Map uploadResult = cloudinary.uploader().upload(courseImg.getBytes(), ObjectUtils.emptyMap());
				newCourseObj.setImageUrl((String) uploadResult.get("secure_url"));
			}
			else
			{
				newCourseObj.setImageUrl(oldCourseObj.getImageUrl());
			}
			
			courseService.updateCourseDetails(newCourseObj);
			
			redirectAttributes.addFlashAttribute("successMsg", "Course details updated successfully");
		}
		catch(Exception e)
		{
			redirectAttributes.addFlashAttribute("errorMsg", "Course details not updated due to some error");
			e.printStackTrace();
		}
		
		return "redirect:/courseManagement";
	}
	//---------------edit course ends-----------------------------
	
	@GetMapping("/deleteCourseDetails")
	public String deleteCourseDetails(@RequestParam("id") Long id, RedirectAttributes redirectAttributes)
	{
		try
		{
			courseService.deleteCourseDetailsById(id);
			redirectAttributes.addFlashAttribute("successMsg", "Course deleted successfully");
		}
		catch(Exception e)
		{
			redirectAttributes.addFlashAttribute("errorMsg", "Course not deleted due to some error");
			e.printStackTrace();
		}
		return "redirect:/courseManagement";
	}
}

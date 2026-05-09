package in.vk.main.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import in.vk.main.services.OrdersChartService;

@Controller
public class OrdersChartController 
{
	@Autowired
	private OrdersChartService ordersChartService;
	
	@GetMapping("/adminProfile")
	public String openAdminProfilePage(Model model)
	{
		//-------graph 3------------------
		List<Object[]> listOfCoursesSoldPerDay = ordersChartService.findCoursesSoldPerDay();
		
		List<String> dates1 = new ArrayList<>();
		List<Long> counts1 = new ArrayList<>();
		
		int limit1 = Math.max(0, listOfCoursesSoldPerDay.size() - 15);
		for(int i = limit1; i < listOfCoursesSoldPerDay.size(); i++)
		{
			Object[] obj = listOfCoursesSoldPerDay.get(i);
			String rawDate = (String) obj[0];
			String normalizedDate = normalizeDate(rawDate);
			dates1.add(normalizedDate);
			counts1.add((Long) obj[1]);
		}
		
		model.addAttribute("dates1", dates1);
		model.addAttribute("counts1", counts1);
		
		
		//---------graph 2------------------
		List<Object[]> listOfCoursesTotalSales = ordersChartService.findCoursesTotalSales();
		
		List<String> coursename1 = new ArrayList<>();
		List<Long> coursecount1 = new ArrayList<>();
		
		for(Object[] obj : listOfCoursesTotalSales)
		{
			coursename1.add((String) obj[0]);
			coursecount1.add((Long) obj[1]);
		}
		
		model.addAttribute("coursename1", coursename1);
		model.addAttribute("coursecount1", coursecount1);
		
		
		
		//---------graph 1------------------
		List<Object[]> listOfCoursesAmountSales = ordersChartService.findCoursesAmountTotalSales();
		
		List<String> date11 = new ArrayList<>();
		List<Double> totalAmount11 = new ArrayList<>();
		
		int limit2 = Math.max(0, listOfCoursesAmountSales.size() - 15);
		for(int i = limit2; i < listOfCoursesAmountSales.size(); i++)
		{
			Object[] obj = listOfCoursesAmountSales.get(i);
			String rawDate = (String) obj[0];
			String normalizedDate = normalizeDate(rawDate);
			date11.add(normalizedDate);
			totalAmount11.add((Double) obj[1]);
		}
		
		model.addAttribute("date11", date11);
		model.addAttribute("totalAmount11", totalAmount11);

		//---------Summary Stats------------------
		model.addAttribute("totalRevenue", ordersChartService.getTotalRevenue());
		model.addAttribute("totalStudents", ordersChartService.getTotalStudents());
		model.addAttribute("totalCourses", ordersChartService.getTotalCourses());
		
		return "admin-profile";
	}
	
	private String normalizeDate(String rawDate) {
		if (rawDate == null) return "N/A";
		try {
			// Format 1: dd/MM/yyyy (New May Data)
			if (rawDate.contains("/")) {
				java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
				java.time.LocalDate date = java.time.LocalDate.parse(rawDate, dtf);
				return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
			}
			// Format 2: yyyy-MM-dd (Old Feb Data)
			if (rawDate.contains("-") && rawDate.length() == 10) {
				java.time.LocalDate date = java.time.LocalDate.parse(rawDate);
				return date.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
			}
		} catch (Exception e) {
			// Return raw if it's already formatted or unknown
		}
		return rawDate;
	}
}

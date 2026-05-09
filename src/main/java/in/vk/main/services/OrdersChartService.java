package in.vk.main.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.vk.main.repositories.OrdersChartRepository;

@Service
public class OrdersChartService
{
	@Autowired
	private OrdersChartRepository ordersChartRepository;
	
	public List<Object[]> findCoursesAmountTotalSales()
	{
		return ordersChartRepository.findCoursesAmountTotalSales();
	}
	
	public List<Object[]> findCoursesSoldPerDay()
	{
		return ordersChartRepository.findCoursesSoldPerDay();
	}
	
	public List<Object[]> findCoursesTotalSales()
	{
		return ordersChartRepository.findCoursesTotalSales();
	}

	public Double getTotalRevenue() {
		Double rev = ordersChartRepository.getTotalRevenue();
		return rev != null ? rev : 0.0;
	}

	public Long getTotalStudents() {
		return ordersChartRepository.getTotalStudents();
	}

	public Long getTotalCourses() {
		return ordersChartRepository.getTotalCourses();
	}
}

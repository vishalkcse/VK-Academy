package in.vk.main.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.vk.main.entities.Orders;

@Repository
public interface OrdersChartRepository extends JpaRepository<Orders, Long>
{
	@Query("SELECT SUM(o.courseAmount) FROM Orders o")
	Double getTotalRevenue();

	@Query("SELECT COUNT(DISTINCT o.userEmail) FROM Orders o")
	Long getTotalStudents();

	@Query(value = "SELECT COUNT(*) FROM course", nativeQuery = true)
	Long getTotalCourses();
	String SQL_QUERY1 = "SELECT TRIM(SUBSTRING_INDEX(date_of_purchase, ',', 1)) AS purchased_date, SUM(course_amount) AS total_sales_amount FROM orders GROUP BY purchased_date ORDER BY MAX(id) ASC";
	@Query(value = SQL_QUERY1, nativeQuery = true)
	List<Object[]> findCoursesAmountTotalSales();
	
	
	
	String SQL_QUERY2 = "SELECT course_name, COUNT(*) AS total_sold FROM orders WHERE course_name IN (SELECT name FROM course) GROUP BY course_name";
	@Query(value = SQL_QUERY2, nativeQuery = true)
	List<Object[]> findCoursesTotalSales();
	
	
	String SQL_QUERY3 = "SELECT TRIM(SUBSTRING_INDEX(date_of_purchase, ',', 1)) AS purchased_date, COUNT(*) AS number_of_courses_sold FROM orders GROUP BY purchased_date ORDER BY MAX(id) ASC";
	@Query(value = SQL_QUERY3, nativeQuery = true)
	List<Object[]> findCoursesSoldPerDay();
}

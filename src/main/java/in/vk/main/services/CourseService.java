package in.vk.main.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.vk.main.entities.Course;
import in.vk.main.repositories.CourseRepository;

@Service
public class CourseService 
{
	// Cloudinary configuration is picked from environment variables in the method
	@Autowired
	private CourseRepository courseRepository;
	
	public List<Course> getAllCourseDetails()
	{
		return courseRepository.findAll();
	}
	
	//Pageable is used to specify pagination information i.e. page number, page size, sorting order etc when querying with database
	//Page represents the chunk of data that is fetched according to pagination parameters defined by Pagable.
	public Page<Course> getAllCourseDetailsByPagination(Pageable pageable)
	{
		return courseRepository.findAll(pageable);
	}
	
	public void addCourse(Course course, MultipartFile courseImg) throws IOException
	{
		if(courseImg != null && !courseImg.isEmpty()) {
			Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", System.getenv("CLOUDINARY_CLOUD_NAME"),
				"api_key", System.getenv("CLOUDINARY_API_KEY"),
				"api_secret", System.getenv("CLOUDINARY_API_SECRET")
			));
			Map uploadResult = cloudinary.uploader().upload(courseImg.getBytes(), ObjectUtils.emptyMap());
			course.setImageUrl((String) uploadResult.get("secure_url"));
		}
		
		courseRepository.save(course);
	}
	
	public Course getCourseDetails(String courseName)
	{
		return courseRepository.findByName(courseName);
	}
	
	public Course getCourseDetailsById(Long id)
	{
		return courseRepository.findById(id).orElse(null);
	}
	
	public void updateCourseDetails(Course course)
	{
		courseRepository.save(course);
	}
	
	public void deleteCourseDetailsById(Long id)
	{
		Course course = courseRepository.findById(id).orElse(null);
		if(course != null)
		{
			courseRepository.delete(course);
		}
		else
		{
			throw new RuntimeException("Course not found with id : "+id);
		}
	}
	
//	public List<String> getAllCourseNames()
//	{
//		List<Course> coursesList = courseRepository.findAll();
//		
//		List<String> courseNameList = new ArrayList<>();
//		
//		for(Course course : coursesList)
//		{
//			String courseName = course.getName();
//			courseNameList.add(courseName);
//		}
//		
//		return courseNameList;
//	}
	
	public List<String> getAllCourseNames()
	{
		return courseRepository.findAll().stream()
				.map(Course::getName)
				.collect(Collectors.toList());
	}
	
	public List<Course> getCoursesByCategory(String category)
	{
		return courseRepository.findByCategory(category);
	}
	
	public List<Course> searchCoursesByName(String name)
	{
		return courseRepository.findByNameContaining(name);
	}
	
	public List<String> getAllCategories()
	{
		return courseRepository.findAll().stream()
				.map(Course::getCategory)
				.filter(c -> c != null && !c.isEmpty())
				.distinct()
				.collect(Collectors.toList());
	}
}

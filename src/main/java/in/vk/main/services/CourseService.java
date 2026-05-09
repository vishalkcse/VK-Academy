package in.vk.main.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	private String UPLOAD_DIR = "src/main/resources/static/uploads/";
	private String IMAGE_URL = "/uploads/";
	
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
		String imgName = courseImg.getOriginalFilename();
		Path imgPath = Paths.get(UPLOAD_DIR+imgName);
		Files.write(imgPath, courseImg.getBytes());
		
		String imgUrl = IMAGE_URL+imgName;
		course.setImageUrl(imgUrl);
		
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

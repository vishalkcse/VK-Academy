package in.vk.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.vk.main.entities.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>
{
	Course findByName(String courseName);
	java.util.List<Course> findByCategory(String category);
	java.util.List<Course> findByNameContaining(String name);
}

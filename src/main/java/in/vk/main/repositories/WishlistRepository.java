package in.vk.main.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import in.vk.main.entities.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> 
{
	List<Wishlist> findByUserEmail(String userEmail);
	Wishlist findByUserEmailAndCourseId(String userEmail, Long courseId);
	void deleteByUserEmailAndCourseId(String userEmail, Long courseId);
}

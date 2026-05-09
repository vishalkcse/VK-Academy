package in.vk.main.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import in.vk.main.entities.Wishlist;
import in.vk.main.repositories.WishlistRepository;

@Service
public class WishlistService 
{
	@Autowired
	private WishlistRepository wishlistRepository;
	
	public void addToWishlist(Wishlist wishlist)
	{
		Wishlist existing = wishlistRepository.findByUserEmailAndCourseId(wishlist.getUserEmail(), wishlist.getCourseId());
		if(existing == null)
		{
			wishlistRepository.save(wishlist);
		}
	}
	
	public List<Wishlist> getWishlistByUser(String email)
	{
		return wishlistRepository.findByUserEmail(email);
	}
	
	@Transactional
	public void removeFromWishlist(String email, Long courseId)
	{
		wishlistRepository.deleteByUserEmailAndCourseId(email, courseId);
	}
	
	public boolean isCourseInWishlist(String email, Long courseId)
	{
		return wishlistRepository.findByUserEmailAndCourseId(email, courseId) != null;
	}
}

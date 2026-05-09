package in.vk.main.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.vk.main.entities.User;
import in.vk.main.repositories.UserRepository;

@Service
public class UserService
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;
	
	public void registerUserService(User user)
	{
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}
	
	public boolean loginUserService(String email, String password)
	{
		User user = userRepository.findFirstByEmail(email);
		if(user != null)
		{
			return passwordEncoder.matches(password, user.getPassword());
		}
		return false;
	}

	public void resetPassword(String email, String newPassword) {
		User user = userRepository.findFirstByEmail(email);
		if (user != null) {
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
		}
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
	}
}

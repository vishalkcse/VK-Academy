package in.vk.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import in.vk.main.entities.User;

public interface CustomerRepository extends JpaRepository<User, Long> 
{
	User findByEmail(String email);
}

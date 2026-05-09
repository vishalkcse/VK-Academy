package in.vk.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.vk.main.entities.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
	User findFirstByEmail(String email);
}

package in.vk.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import in.vk.main.entities.TickerContent;

@Repository
public interface TickerRepository extends JpaRepository<TickerContent, Long> {
}

package in.vk.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import in.vk.main.entities.Batch;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {
}

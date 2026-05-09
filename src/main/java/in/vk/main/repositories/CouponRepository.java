package in.vk.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import in.vk.main.entities.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
	Coupon findByCode(String code);
}

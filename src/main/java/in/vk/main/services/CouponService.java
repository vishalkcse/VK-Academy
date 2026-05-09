package in.vk.main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.vk.main.entities.Coupon;
import in.vk.main.repositories.CouponRepository;

@Service
public class CouponService {
	@Autowired
	private CouponRepository couponRepository;

	public Coupon validateCoupon(String code) {
		return couponRepository.findByCode(code);
	}
	
	public Coupon addCoupon(Coupon coupon) {
		return couponRepository.save(coupon);
	}

	public java.util.List<Coupon> getAllCoupons() {
		return couponRepository.findAll();
	}

	public void deleteCouponById(Long id) {
		couponRepository.deleteById(id);
	}

	public Coupon getCouponById(Long id) {
		return couponRepository.findById(id).orElse(null);
	}
}

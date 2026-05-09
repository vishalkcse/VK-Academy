package in.vk.main.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.vk.main.entities.Coupon;
import in.vk.main.services.CouponService;

@RestController
@RequestMapping("/api")
public class CouponApi {

	@Autowired
	private CouponService couponService;

	@GetMapping("/validateCoupon")
	public ResponseEntity<Coupon> validateCoupon(@RequestParam("code") String code) {
		Coupon coupon = couponService.validateCoupon(code);
		if (coupon != null) {
			return ResponseEntity.ok(coupon);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}

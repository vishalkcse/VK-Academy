package in.vk.main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.vk.main.entities.Orders;
import in.vk.main.repositories.OrdersRepository;

@Service
public class OrderService
{
	@Autowired
	private OrdersRepository ordersRepository;
	
	public void storeUserOrders(Orders orders)
	{
		ordersRepository.save(orders);
	}

    public Orders findOrderByUserAndCourse(String email, String courseName) {
        return ordersRepository.findByUserEmailAndCourseName(email, courseName);
    }
}

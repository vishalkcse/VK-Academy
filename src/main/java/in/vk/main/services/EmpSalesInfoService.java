package in.vk.main.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.vk.main.repositories.EmpSalesInfoRepository;

@Service
public class EmpSalesInfoService
{
	@Autowired
	private EmpSalesInfoRepository empSalesInfoRepository;
	
	public String findTotalSalesByAllEmployees()
	{
		return empSalesInfoRepository.findTotalSalesByAllEmployees();
	}
	
	public List<Object[]> findTotalSalesByEachEmployee()
	{
		return empSalesInfoRepository.findTotalSalesByEachEmployee();
	}
}

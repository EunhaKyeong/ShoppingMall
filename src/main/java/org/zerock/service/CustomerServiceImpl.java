package org.zerock.service;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.zerock.domain.CustomerVO;
import org.zerock.mapper.CustomerMapper;

@Service
public class CustomerServiceImpl implements CustomerService {
	@Inject
	private CustomerMapper cm;

	@Override
	public void insertBuyer(CustomerVO customer) {
		cm.insertBuyer(customer);
		System.out.println("������ insert ����!");
	}

	@Override
	public HashMap<String, Object> getLoginInfo(String socialId) {
		HashMap<String, Object> loginInfo = cm.getLoginInfo(socialId);
		
		return loginInfo;
	}

	@Override
	public void insertSeller(CustomerVO customer) {
		cm.insertSeller(customer);
		System.out.println("�Ǹ��� insert ����!");
	}

	@Override
	public String getCustomerName(long customerCode) {
		String customerName = cm.getCustomerName(customerCode);
		
		return customerName;
	}
	
	@Override
	public String getCompanyName(long customerCode) {
		
		String CompanyName = cm.getCompanyName(customerCode);
		
		return CompanyName;
	}

	@Override
	public HashMap<String, Object> getBuyerProfile(long customerCode) {
		HashMap<String, Object> buyerProfile = cm.getBuyerProfile(customerCode);
		
		return buyerProfile;
	}

	@Override
	public HashMap<String, Object> getSellerProfile(long customerCode) {
		HashMap<String, Object> sellerProfile = cm.getSellerProfile(customerCode);
		
		return sellerProfile;
	}

	@Override
	public String getSocialId(long customerCode) {
		String socialId = cm.getSocialId(customerCode);
		
		return socialId;
	}

	@Override
	public void deleteCustomer(long customerCode) {
		cm.deleteCustomer(customerCode);
		System.out.println("customerCode : " + customerCode + " ȸ�� ������ �Ϸ��߽��ϴ�.");
	}

	@Override
	public void updateBuyer(CustomerVO updateCustomer) {
		cm.updateBuyer(updateCustomer);
		System.out.println("customerCode : " + updateCustomer.getCustomerCode() + "������ ȸ�� ���� �Ϸ�");
	}

	@Override
	public void updateSeller(CustomerVO updateCustomer) {
		cm.updateSeller(updateCustomer);
		System.out.println("customerCode : " + updateCustomer.getCustomerCode() + "�Ǹ��� ȸ�� ���� �Ϸ�");
	}

	@Override
	public int updatePoint(long customerCode, long totalPoint) {
		HashMap<String, Object> pointHm = new HashMap<String, Object>();
		
		pointHm.put("customerCode", customerCode);
		pointHm.put("totalPoint", totalPoint);
		
		return cm.updatePoint(pointHm);
	}
}

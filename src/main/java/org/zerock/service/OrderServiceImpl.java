package org.zerock.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.zerock.domain.OrderVO;
import org.zerock.mapper.OrderMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class OrderServiceImpl implements OrderService {
	@Resource
	private OrderMapper orderMapper;
	
	@Override
	public int createOrder(int totalPrice, long customerCode) {
		OrderVO orderVO = new OrderVO();
		orderVO.setTotalOrderPrice(totalPrice); //orderVO �ν��Ͻ��� totalPrice �ʵ尪 set.
		orderVO.setCustomerCode(customerCode); //orderVO �ν��Ͻ��� customerCode �ʵ尪 set.
		
		orderMapper.createOrder(orderVO);
		int orderCode = orderVO.getOrderCode();
		System.out.println("order ���̺� ������ ���� �Ϸ�\norderCode : " + orderCode);
	
		return orderCode;
	}

	//delivery ���ͼ��Ϳ��� �������� �� �α��ε� ������� ����ڵ����� �˾ƺ��� ���� ������
	@Override
	public long getCustomerCodeByDeliery(int deliveryCode) {
		long customerCode = orderMapper.getCustomerCodeByDeliery(deliveryCode);
		
		return customerCode;
	}

	@Override
	public int updateStatus(int orderCode) {
	
		return orderMapper.updateStatus(orderCode);
	}

	@Override
	public long getCustomerCodeByOrder(int orderCode) {
		
		return orderMapper.getCustomerCodeByOrder(orderCode);
	}

}

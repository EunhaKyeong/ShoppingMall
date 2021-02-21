package org.zerock.controller;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.zerock.service.DeliveryServiceImpl;
import org.zerock.service.OrderServiceImpl;

import lombok.extern.log4j.Log4j;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j
public class OrderControllerTests {
	@Resource
	private OrderServiceImpl orderService;
	@Resource
	private DeliveryServiceImpl deliveryService;
	
	public int testorderCancel() {
		int orderCode = 186;
		
		if (orderService.updateStatus(orderCode, "cancel")==0) {
			log.info("������Ʈ �� �����Ͱ� �������� �ʽ��ϴ�.");
			
			return 0;
		}
		else {
			log.info("orderStatus : cancel�� ����Ǿ����ϴ�. �ֹ��ڵ� : " + orderCode);
			
			return deliveryService.updateDeliveryStatus(orderCode, "cancel");
		}
	}
	
	@Test
	public void checkOrderCancel() {
		log.info(testorderCancel());
	}
}

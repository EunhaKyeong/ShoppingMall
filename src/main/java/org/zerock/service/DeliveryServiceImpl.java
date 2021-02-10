package org.zerock.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;
import org.zerock.domain.DeliveryVO;
import org.zerock.mapper.DeliveryMapper;

@Repository
public class DeliveryServiceImpl implements DeliveryService {
	@Resource
	DeliveryMapper deliveryMapper;
	
	@Override
	public int createDelivery(DeliveryVO deliveryVO) {
		deliveryMapper.createDelivery(deliveryVO);
		int deliveryCode = deliveryVO.getDeliveryCode();
		System.out.println("\ndelivery ���̺� ������ ���� �Ϸ�\ndeliveryCode : " + deliveryCode);
		
		return deliveryCode;
	}

}

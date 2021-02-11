package org.zerock.service;

import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;
import org.zerock.domain.DeliveryVO;
import org.zerock.mapper.DeliveryMapper;

import lombok.extern.log4j.Log4j;

@Repository
@Log4j
public class DeliveryServiceImpl implements DeliveryService {
	@Resource
	DeliveryMapper deliveryMapper;
	
	@Override
	public int createDelivery(int orderCode, long customerCode) {
		DeliveryVO deliveryVO = new DeliveryVO(orderCode);	//deliveryVO �ν��Ͻ� ����
		//DeliveryMapper�� createDelivery �޼����� �ؽ��� �Ķ���� �ν��Ͻ� ����
		HashMap<String, Object> parameterHm = new HashMap<String, Object>();
		parameterHm.put("deliveryVO", deliveryVO);
		parameterHm.put("customerCode", customerCode);
		
		deliveryMapper.createDelivery(parameterHm);
		deliveryVO = (DeliveryVO) parameterHm.get("deliveryVO");
		int deliveryCode = deliveryVO.getDeliveryCode();	//��� insert�� �������� deliveryCode ����.
		
		System.out.println("delivery ���̺� ������ ���� �Ϸ�\ndeliveryCode : " + deliveryCode);
		
		return deliveryCode;
	}

	@Override
	public HashMap<String, Object> getDelivery(int deliveryCode) {
		System.out.println("getDelivery() : " + deliveryCode);
		
		return deliveryMapper.getDelivery(deliveryCode);
	}

}

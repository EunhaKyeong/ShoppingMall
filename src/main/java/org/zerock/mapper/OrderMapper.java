package org.zerock.mapper;

import org.zerock.domain.OrderVO;

public interface OrderMapper {
	public int createOrder(OrderVO orderVO);
	//delivery ���ͼ��Ϳ��� �������� �� �α��ε� ������� ����ڵ����� �˾ƺ��� ���� ������
	public long getCustomerCode(int deliveryCode);
}

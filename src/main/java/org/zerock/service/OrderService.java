package org.zerock.service;

import java.util.HashMap;

public interface OrderService {
	public int createOrder(int totalPrice, long customerCode);
	//delivery ���ͼ��Ϳ��� �������� �� �α��ε� ������� ����ڵ����� �˾ƺ��� ���� ������
	public long getCustomerCodeByDeliery(int deliveryCode);
	//����� �Է��� �Ϸ���� ��(������̺��� delivery_status�� preparing���� �ٲ��� ��) �ֹ����̺��� order_status�� done���� �ٲ�.
	public int updateStatus(int orderCode);
	public long getCustomerCodeByOrder(int orderCode);
}

package org.zerock.service;

public interface OrderService {
	public int createOrder(int totalPrice, long customerCode);
	//delivery ���ͼ��Ϳ��� �������� �� �α��ε� ������� ����ڵ����� �˾ƺ��� ���� ������
	public long getCustomerCode(int deliveryCode);
}

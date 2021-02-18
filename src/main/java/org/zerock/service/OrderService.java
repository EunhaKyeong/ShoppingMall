package org.zerock.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.zerock.domain.DeliveryVO;

public interface OrderService {
	//delivery ���ͼ��Ϳ��� �������� �� �α��ε� ������� ����ڵ����� �˾ƺ��� ���� ������
//	public long getCustomerCodeByDeliery(int deliveryCode);
	//����� �Է��� �Ϸ���� ��(������̺��� delivery_status�� preparing���� �ٲ��� ��) �ֹ����̺��� order_status�� done���� �ٲ�.
	public int updateStatus(int orderCode);
	//order ���ͼ��Ϳ��� �������� �� �α��ε� ������� �ֹ��ڵ����� �˾ƺ��� ���� ������
	public long getCustomerCodeByOrder(int orderCode);
	public Integer getOrderCode(HashMap<String, Object> orderInfo, long customerCode);
	public int createOrderDetail(List<HashMap<String, Object>> productsHm, int orderCode);
	public int orderComplete(int orderCode, long customerCode);
	//����������-�ֹ���Ͽ��� ���ڵ�� �ֹ��ڵ�, �ֹ���, �ֹ�����, ��ۻ��� ��������
	public List<HashMap<String, Object>> getOrderDone(Integer customerCode);
}

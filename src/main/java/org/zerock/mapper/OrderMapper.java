package org.zerock.mapper;

import org.zerock.domain.OrderVO;

public interface OrderMapper {
	public int createOrder(OrderVO orderVO);
	//delivery ���ͼ��Ϳ��� �������� �� �α��ε� ������� ����ڵ����� �˾ƺ��� ���� ������
	public long getCustomerCodeByDeliery(int deliveryCode);
	//����� �Է��� �Ϸ���� ��(������̺��� delivery_status�� preparing���� �ٲ��� ��) �ֹ����̺��� order_status�� done���� �ٲ�.
	public int updateStatus(int orderCode);
	//order/orderSuccess ���ͼ��Ϳ��� ���
	public long getCustomerCodeByOrder(int orderCode);
}

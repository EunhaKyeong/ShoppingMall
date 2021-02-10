package org.zerock.controller;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zerock.domain.OrderVO;
import org.zerock.domain.basketVO;
import org.zerock.service.OrderServiceImpl;
import org.zerock.service.basketServiceImpl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@Controller
public class OrderController {
	@Resource
	private basketServiceImpl basketService;

	@Resource
	private OrderServiceImpl orderServie;
	
	@Resource
	private Gson gson;
	
	// ��ǰ �� ���������� �ٷ� �ֹ��ϱ� ��ư Ŭ������ �� axios�� Ȱ���ؼ� ���޹��� �����͸� DB�� �����Ű�� api
	// �̰� ���߿� ��ٱ��Ͽ��� �ֹ��ϱ�� �Ѿ ���� ����� ����.
	@RequestMapping(value = "/order/delivery", method = RequestMethod.POST)
	@ResponseBody
	public int delivery(@RequestBody HashMap<String, Object> deliveryHm, HttpSession session) {
		System.out.println("\n=====================================================");
		System.out.println("�ٷ� �ֹ��ϱ� ��ư�� Ŭ������ �� axios api �����");
		
		if (session.getAttribute("customerCode")==null) {
			return 0;
		}
		else if (session.getAttribute("customerCode").equals(2)) {
			return 2;
		}
		
		int totalPrice = 0;
		basketVO basketVO = new basketVO();
		OrderVO orderVO = new OrderVO();
		
		String history = (String) deliveryHm.get("history");
		JsonArray productsArr = (JsonArray) gson.toJsonTree(deliveryHm.get("products"));
		
		if (history.equals("detail")) {	//�� ���� ���������� �ٷ� �ֹ��ϱ� ��ư Ŭ������ ��
			JsonObject productObj= (JsonObject) productsArr.get(0);
			System.out.println("��û������ : " + productObj.toString());
			totalPrice += productObj.get("productPrice").getAsInt()*productObj.get("productQuantity").getAsInt();

			basketVO.setCustomer_code((long) session.getAttribute("customerCode"));
			basketVO.setProduct_code(productObj.get("productCode").getAsInt());
			basketVO.setProduct_quantity(productObj.get("productQuantity").getAsInt());
			//��ٱ��� ���̺� ������ ����.
			basketService.getBasketProduct(basketVO);
		}
//		else {	//�̰� ��ٱ��� ���������� �Ѿ�� ��
//			for (int i=0; i<productsArr.size(); i++) {
//				JsonObject productObj= (JsonObject) productsArr.get(i);
//				System.out.println("��û������ : " + productObj.toString());
//				totalPrice += productObj.get("productPrice").getAsInt()*productObj.get("productQuantity").getAsInt();
//				basketVO.setCustomer_code((long) session.getAttribute("customerCode"));
//				basketVO.setProduct_code(productObj.get("productCode").getAsInt());
//				basketVO.setProduct_quantity(productObj.get("productQuantity").getAsInt());
//				//��ٱ��� ���̺� ������ ����.
//				basketService.getBasketProduct(basketVO);
//			}
//		}
		
		//�ֹ����̺� ������ ����
		orderVO.setTotalOrderPrice(totalPrice);
		int orderCode = orderServie.createOrder(orderVO);
		
		//�ֹ�-��ٱ��� ���̺� ������ ����
		
		
		System.out.println("=====================================================");
		return 1;
	}

	@RequestMapping("/order/delivery/form")
	public String orderInput() {

		return "order/deliveryForm";
	}
}

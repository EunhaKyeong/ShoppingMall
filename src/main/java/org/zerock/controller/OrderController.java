package org.zerock.controller;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zerock.domain.DeliveryVO;
import org.zerock.domain.OrderBasketVO;
import org.zerock.domain.OrderVO;
import org.zerock.domain.basketVO;
import org.zerock.service.DeliveryServiceImpl;
import org.zerock.service.OrderBasketServiceImpl;
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
	private OrderBasketServiceImpl obService;
	@Resource
	private DeliveryServiceImpl deliveryService;
	@Resource
	private Gson gson;
	
	// ��ǰ �� ���������� �ٷ� �ֹ��ϱ� ��ư Ŭ������ �� axios�� Ȱ���ؼ� ���޹��� �����͸� DB�� �����Ű�� api
	// �̰� ���߿� ��ٱ��Ͽ��� �ֹ��ϱ�� �Ѿ ���� ����� ����.
	@RequestMapping(value = "/order/delivery", method = RequestMethod.POST)
	@ResponseBody
	public String delivery(@RequestBody HashMap<String, Object> deliveryHm, HttpSession session) {
		System.out.println("\n=====================================================");
		System.out.println("�ٷ� �ֹ��ϱ� ��ư�� Ŭ������ �� axios api �����");
		
		JsonObject resjson = new JsonObject();
		//����üũ
		if (session.getAttribute("customerCode")==null) {	//�α����� �� �� ���� ��
			resjson.addProperty("result", 0);
			
			return gson.toJson(resjson);
		}
		else if (session.getAttribute("customerCode").equals(2)) {	//�Ǹ����� ��
			resjson.addProperty("result", 2);
			
			return gson.toJson(resjson);
		}
		
		int totalPrice = 0;
		int orderCode = 0;
		long customerCode = 0;
		int productCode = 0;
		int productQuantity = 0;
		int deliveryCode = 0;
		OrderVO orderVO = new OrderVO();
		basketVO basketVO;
		OrderBasketVO obVO;
		DeliveryVO deliveryVO;
		
		String history = (String) deliveryHm.get("history");	//��ǰ ������ ���̺��� ��û�ߴ���, ��ٱ��� ���̺��� ��û�ߴ��� Ȯ�ο�.
		JsonArray productsArr = (JsonArray) gson.toJsonTree(deliveryHm.get("products"));	//�ֹ��ϰ��� �ϴ� ��ǰ�� ���� ������ ��� ����.(��ǰ�ڵ�, ����, ��ǰ����)
		
		if (history.equals("detail")) {	//�� ���� ���������� �ٷ� �ֹ��ϱ� ��ư Ŭ������ ��
			JsonObject productObj= (JsonObject) productsArr.get(0);
			
			totalPrice = (int) deliveryHm.get("totalPrice");	//��û �� ������ �� �ֹ��ݾ�
			customerCode = (long) session.getAttribute("customerCode");	//���ڵ�
			productCode = productObj.get("productCode").getAsInt();	//��ǰ�ڵ�
			productQuantity = productObj.get("productQuantity").getAsInt();	//��ǰ����
			
			basketVO = new basketVO(productCode, customerCode, productQuantity);	//basketVO �ν��Ͻ� ����.
			basketService.getBasketProduct(basketVO);	//��ٱ��� ���̺� ������ ����.
			orderVO.setTotalOrderPrice(totalPrice); //orderVO �ν��Ͻ��� totalPrice �ʵ尪 set.
			orderCode = orderServie.createOrder(orderVO);	//�ֹ����̺� ������ ����(������ ���� �� orderCode ��ȯ)
			obVO = new OrderBasketVO(orderCode, customerCode, productCode, productQuantity);	//orderBasketVO �ν��Ͻ� ����.
			obService.createOrderBasket(obVO); 	//�ֹ�-��ٱ��� ���̺� ������ ����
			deliveryVO = new DeliveryVO(orderCode);	//deliveryVO �ν��Ͻ� ����
			deliveryCode = deliveryService.createDelivery(deliveryVO);	//��� ���̺� ������ ����.
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
		
		//������ �����ϱ�
		resjson.addProperty("result", 1);
		resjson.addProperty("deliveryCode", deliveryCode);
		
		System.out.println("=====================================================");
		
		return gson.toJson(resjson);
	}

	@RequestMapping(value="/order/delivery/form", method=RequestMethod.GET)
	public String orderInput(@RequestParam String deliveryCode) {
		System.out.println("\n=====================================================");
		System.out.println("����� ������Է� ������");
		
		System.out.println("=====================================================");
		return "order/deliveryForm";
	}
}

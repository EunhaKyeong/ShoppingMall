package org.zerock.controller;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zerock.domain.DeliveryVO;
import org.zerock.domain.basketVO;
import org.zerock.service.CustomerServiceImpl;
import org.zerock.service.DeliveryServiceImpl;
import org.zerock.service.OrderDetailServiceImpl;
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
	private OrderDetailServiceImpl odService;
	@Resource
	private DeliveryServiceImpl deliveryService;
	@Resource
	private CustomerServiceImpl customerService;
	@Resource
	private Gson gson;
	
	// ��ǰ �� ���������� �ٷ� �ֹ��ϱ� ��ư Ŭ������ �� axios�� Ȱ���ؼ� ���޹��� �����͸� DB�� �����Ű�� api
	// �̰� ���߿� ��ٱ��Ͽ��� �ֹ��ϱ�� �Ѿ ���� ����� ����.
	@RequestMapping(value = "/order/delivery", method = RequestMethod.POST)
	@ResponseBody
	public String delivery(@RequestBody HashMap<String, Object> deliveryHm, HttpSession session) {
		System.out.println("\n=====================================================");
		System.out.println("�ٷ� �ֹ��ϱ� ��ư�� Ŭ������ �� axios api �����");
		
		JsonObject resjson = new JsonObject();	//���� jSON �ν��Ͻ� ����.
		//����üũ
		if (session.getAttribute("customerCode")==null) {	//�α����� �� �� ���� ��
			resjson.addProperty("result", 0);
			
			return gson.toJson(resjson);
		}
		
		int totalPrice = 0;
		int orderCode = 0;
		int productCode = 0;
		int productQuantity = 0;
		int deliveryCode = 0;
		long customerCode = (long) session.getAttribute("customerCode");	//���ڵ�
		basketVO basketVO;
		
		String reqUrl = (String) deliveryHm.get("reqUrl");
		JsonArray productsArr = (JsonArray) gson.toJsonTree(deliveryHm.get("products"));	//�ֹ��ϰ��� �ϴ� ��ǰ�� ���� ������ ��� ����.(��ǰ�ڵ�, ����, ��ǰ����)
		totalPrice = (int) deliveryHm.get("totalPrice");	//��û �� ������ �� �ֹ��ݾ�
		
		if (reqUrl.contains("ProductDetail")) {	//��ǰ ������ ���������� �ٷ� �ֹ��ϱ� ��ư�� Ŭ���� ���
			JsonObject productObj= (JsonObject) productsArr.get(0);
			productCode = productObj.get("productCode").getAsInt();	//��ǰ�ڵ�
			productQuantity = productObj.get("productQuantity").getAsInt();	//��ǰ����
			
			basketVO = new basketVO(productCode, customerCode, productQuantity);	//basketVO �ν��Ͻ� ����.
			basketService.getBasketProduct(basketVO);	//��ٱ��� ���̺� ������ ����.
		}
		
		//�ֹ� ���̺� ������ �����ϱ�
		orderCode = orderServie.createOrder(totalPrice, customerCode);
		
		//�ֹ��� ���̺� ������ �����ϱ� 
		for (int i=0; i<productsArr.size(); i++) {
			JsonObject productObj = (JsonObject) productsArr.get(i);
			productCode = productObj.get("productCode").getAsInt();	//��ǰ�ڵ�
			productQuantity = productObj.get("productQuantity").getAsInt();	//��ǰ����
			odService.createOrderDetail(orderCode, productCode, productQuantity);
		}
		
		//��� ���̺� ������ �����ϱ�
		deliveryCode = deliveryService.createDelivery(orderCode, customerCode);
		
		//���䵥���� json ������ ����� - result, deliveryCode
		resjson.addProperty("result", 1);
		resjson.addProperty("deliveryCode", deliveryCode);
		
		System.out.println("=====================================================");
		
		return gson.toJson(resjson);
	}

	@RequestMapping(value="/order/delivery/form", method=RequestMethod.GET)
	public String orderInput(@RequestParam int deliveryCode, Model model, HttpSession session) {
		System.out.println("\n=====================================================\n����� ������Է� ������");
		HashMap<String, Object> deliveryHm = deliveryService.getDelivery(deliveryCode);
		HashMap<String, Object> customerHm = customerService.getBuyerProfile((long) session.getAttribute("customerCode"));
		
		model.addAttribute("buyer", customerHm);
		model.addAttribute("recipient", deliveryHm);
		System.out.println(deliveryHm.toString());
		System.out.println("=====================================================");
		return "order/deliveryForm";
	}
}

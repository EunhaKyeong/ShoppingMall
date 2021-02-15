package org.zerock.controller;

import java.util.HashMap;
import java.util.List;

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
import org.zerock.service.ProductServiceImpl;
import org.zerock.service.basketServiceImpl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
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
	private ProductServiceImpl productService;
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
		
		if (reqUrl.contains("ProductDetail")) {	//��ǰ ������ ���������� �ٷ� �ֹ��ϱ� ��ư�� Ŭ���� ���:��ٱ��� ���̺� ������ ����
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

	//����� �Է�������(GET)
	@RequestMapping(value="/order/delivery/form", method=RequestMethod.GET)
	public String deliveryFormGet(@RequestParam int deliveryCode, Model model, HttpSession session) {
		System.out.println("\n=====================================================\n����� ������Է� ������");
		HashMap<String, Object> deliveryHm = deliveryService.getDelivery(deliveryCode);
		deliveryHm.put("deliveryCode", deliveryCode);
		HashMap<String, Object> customerHm = customerService.getBuyerProfile((long) session.getAttribute("customerCode"));
		
		model.addAttribute("buyer", customerHm);
		model.addAttribute("recipient", deliveryHm);
		System.out.println(deliveryHm.toString());
		System.out.println("=====================================================");
		
		return "order/deliveryForm";
	}
	
	//����� �Է�������(PATCH)
	@RequestMapping(value="/order/delivery/form", method=RequestMethod.PATCH)
	@ResponseBody
	public String deliveryFormPATCH(@RequestBody DeliveryVO deliveryVO) {
		System.out.println("\n=====================================================\n����� �Է��� �������ϴ�.");
		System.out.println(deliveryVO.toString());
		
		HashMap<String, Object> resultHm = deliveryService.orderSuccess(deliveryVO);	//��� ���̺� ������Ʈ(result:������Ʈ ���, orderCode:������Ʈ�� �������� �ֹ��ڵ� ����)
		System.out.println("=====================================================");
		
		return gson.toJson(resultHm);
	}
	
	//������̺� ������Ʈ ���� orderStatus=done, basket ������ ����
	@RequestMapping(value="/order/delivery/after", method=RequestMethod.GET)
	public String orderSuccess(@RequestParam int orderCode, HttpSession session) {
		int result = 1;
		
		while(result!=0) {	//order ���̺� ������Ʈ, basket ���̺� ������ ���� ���� �� ���� �߻� üũ��
			HashMap<String, Object> productInfoHm = odService.getProductInfo(orderCode);	//�ش� �ֹ��ڵ忡 ���� ��ǰ�ڵ� ����Ʈ ��������

			result = (int) productInfoHm.get("result"); //list�� ũ�Ⱑ 0�̸� �ش� ��ǰ�� ���°��̹Ƿ� ���� �߻�
			log.info("productInfoHm size:" + result);
			
			result = basketService.deleteBasket((long)session.getAttribute("customerCode"), (List<Integer>) productInfoHm.get("productCodeList"));	//���ǿ� ����� customerCode�� ��ǰ�ڵ� ����Ʈ�� �ش�Ǵ� ��ٱ��� ������ ����
			log.info("basket ���̺� ������ ���� �Ϸ�");
			
			result = orderServie.updateStatus(orderCode);	//�ش� orderCode�� orderStatus=done���� ������Ʈ
			log.info("order ���̺��� orderStatus�� done���� ����Ǿ����ϴ�. orderCode : " + orderCode);
			
			result = productService.subStock(productInfoHm);	//�ֹ� ������ŭ ��� ���ֱ�
			
			break;
		}
		//result�� 0(����)�̸� orderError ��������, �����̸� orderSuccess �������� �����̷�Ʈ
		return result==0? "redirect:/order/orderError" : "redirect:/order/orderSuccess?orderCode="+orderCode;
	}
	
	//�ֹ� ���� ������
	@RequestMapping(value="/order/orderSuccess", method=RequestMethod.GET) 
	public String orderSuccess(@RequestParam int orderCode, Model model) {
		model.addAttribute("orderCode", orderCode);
		
		return "/order/orderSuccess";
	}
	
	//�ֹ� ���� ������
	@RequestMapping("/order/orderError")
	public String orderError() {
		
		return "/order/orderError";
	}
}

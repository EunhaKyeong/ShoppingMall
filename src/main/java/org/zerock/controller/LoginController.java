package org.zerock.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.zerock.domain.CustomerVO;
import org.zerock.oauth.SocialLogin;
import org.zerock.oauth.SocialValue;
import org.zerock.service.CustomerServiceImpl;

import com.github.scribejava.core.model.OAuth2AccessToken;

@Controller
public class LoginController {
	@Inject
	private SocialValue naverLogin;
	
	@Inject
	private SocialLogin naver;
	
	@Inject
	private CustomerServiceImpl customerService;
	
	//�α��� ������
	@RequestMapping("/login")
	public String login(Model model, HttpSession session) {
		naver = new SocialLogin(naverLogin, session);
		String naverLoginUrl = naver.getAuthorizationUrl();
	
		model.addAttribute("naverLoginUrl", naverLoginUrl);
		
		return "login";
	}
	
	//�Ҽȷα��� �ݹ�
	@RequestMapping(value="/login/{social}/callback", method=RequestMethod.GET)
	public String loginCallback(Model model, @PathVariable String social, HttpSession session, 
			@RequestParam String state, @RequestParam String code, RedirectAttributes redirectAttributes) throws Exception {
		CustomerVO loginCustomer = null;
		
		if (social.equals("naver")) {
			OAuth2AccessToken accessToken = naver.getAccessToken(code, state, session);
			loginCustomer = naver.getProfile(accessToken);
		}
		
		//���� ���� ���� Ȯ��
		HashMap<String, Object> loginInfo = customerService.getLoginInfo(loginCustomer.getSocialId());
		
		if (loginInfo==null) {
			System.out.println("ȸ������ �������� �̵��մϴ�.");
			redirectAttributes.addFlashAttribute("newCustomer", loginCustomer);
			
			return "redirect:/signUp";
		}
		else {
			System.out.println("�����ϴ� �����Դϴ�.");
			
			session.removeAttribute("oauthState"); 
			session.setAttribute("customerType", loginInfo.get("customer_type"));
			session.setAttribute("customerCode", loginInfo.get("customer_code"));
			
			System.out.println("customerType : " + session.getAttribute("customerType"));
			System.out.println("customerCode : " + session.getAttribute("customerCode"));
			
			return "redirect:/";
		}
	}
	
	//ȸ������ ������
	@RequestMapping(value="/signUp", method=RequestMethod.GET)
	public String signUpGet(Model model, HttpServletRequest request) {
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		
		if (flashMap!=null) {
			CustomerVO newCustomer = (CustomerVO) flashMap.get("newCustomer");
			model.addAttribute("newCustomer", newCustomer);
		}
		
		return "signUp";
	}
	
	@RequestMapping(value="/signUp", method=RequestMethod.POST) 
	public String signUpPost(CustomerVO customerInfo, HttpSession session) {
		if (customerInfo.getCustomerType()==1) {	//���� ������ ȸ���� �����ڶ��
			customerService.insertBuyer(customerInfo);
		}
		else {	//���� ������ ȸ���� �Ǹ��ڶ��
			customerService.insertSeller(customerInfo);
		}
		
		session.invalidate();
		
		return "redirect:/";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpSession session) {
		System.out.println("�α׾ƿ� �մϴ�!");
		session.invalidate();
		
		return "redirect:/";
	}
}

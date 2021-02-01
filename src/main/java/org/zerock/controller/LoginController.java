package org.zerock.controller;

import java.io.IOException;
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
import org.zerock.domain.SocialDetailVO;
import org.zerock.oauth.SocialLogin;
import org.zerock.oauth.SocialValue;
import org.zerock.service.CustomerServiceImpl;
import org.zerock.service.SocialDetailServiceImpl;

import com.github.scribejava.core.model.OAuth2AccessToken;

@Controller
public class LoginController {
	@Inject
	private SocialValue naverValue;
	
	@Inject
	private SocialLogin naverLogin;
	
	@Inject
	private CustomerServiceImpl customerService;
	
	@Inject
	private SocialDetailServiceImpl sdService;
	
	//�α��� ������
	@RequestMapping("/login")
	public String login(Model model, HttpSession session) {
		naverLogin.updateTest(naverValue);
		String naverLoginUrl = naverLogin.getAuthorizationUrl(session);
	
		model.addAttribute("naverLoginUrl", naverLoginUrl);
		
		return "login";
	}
	
	//�Ҽȷα��� �ݹ�
	@RequestMapping(value="/login/{social}/callback", method=RequestMethod.GET)
	public String loginCallback(Model model, @PathVariable String social, HttpSession session, 
			@RequestParam String state, @RequestParam String code, RedirectAttributes redirectAttributes) throws Exception {
		
		CustomerVO loginCustomer = null;
		OAuth2AccessToken accessToken = null;
		
		if (social.equals("naver")) {
			accessToken = naverLogin.getAccessToken(code, state, session);
			loginCustomer = naverLogin.getProfile(accessToken);
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
			
			long customerCode = (long) loginInfo.get("customer_code");
			
			//DB�� accessToken ����
			//social_detail ���̺� customer_code �����Ͱ� �����ϴ��� select
			SocialDetailVO socialDetail = sdService.findBySocialDetail(customerCode);
			
			if (socialDetail==null) {
				System.out.println("accessToken�� ���� �����Ͱ� �������� �ʽ��ϴ�.");
				socialDetail = naverLogin.getSocialDetail(customerCode, accessToken);
				sdService.insertTokenData(socialDetail);
			}
			else {
				System.out.println("accessToken�� ���� �����Ͱ� �����մϴ�.");
				socialDetail = naverLogin.getSocialDetail(customerCode, accessToken);
				sdService.updateTokenData(socialDetail);
			}
			
			//�α��� ��ȿ�� �˻縦 ���� session ����
			session.removeAttribute("oauthState"); 
			session.setAttribute("customerType", loginInfo.get("customer_type"));
			session.setAttribute("customerCode", customerCode);
			session.setAttribute("socialType", social);
			System.out.println("login ����!");
			
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
	
	@RequestMapping(value="/login/userModify")
	public String userModify(HttpSession session, Model model) {
		System.out.println("����� ȸ������ ���� ������");
		System.out.println(session.getAttribute("customerType"));
		HashMap<String, Object> profile = null;
		//ȸ�� Ÿ�Կ� ���� �ʿ��� ������������ �ٸ��� �����´�.
		if (session.getAttribute("customerType").equals(1)) {
			//�����ڶ�� �̸�, �̸���, ��ȭ��ȣ, �ּҸ�
			profile = customerService.getBuyerProfile((long) session.getAttribute("customerCode"));	
		}
		else {
			//�����ڶ�� �̸�, �̸���, ��ȭ��ȣ, ȸ���, ȸ�� ��ȭ��ȣ, ȸ�� �ּ�
			profile = customerService.getSellerProfile((long) session.getAttribute("customerCode"));
		}
		model.addAttribute("profile", profile);
		System.out.println(profile.keySet());
		
		return "/myPage/updateProfile";
	}
	
	@RequestMapping(value="/login/userDelete")
	public String userDelete(HttpSession session, Model model) {
		String reauthUrl = "";
		
		System.out.println("=========================================================");
		System.out.println("����� ȸ��Ż�� ������");
		
		if (session.getAttribute("socialType").equals("naver")) {
			naverLogin.updateTest(naverValue);
			reauthUrl  = naverLogin.getReauthorizationUrl(session);
		}
		
		model.addAttribute("reauthUrl", reauthUrl);
		System.out.println("������ url : " + reauthUrl);
		System.out.println("=========================================================");
		
		return "myPage/deleteUser";
	}
	
	@RequestMapping(value="/login/reauth/{social}/callback", method=RequestMethod.GET)
	public String userDeleteCallback(HttpSession session, @PathVariable String social, 
			@RequestParam String code, @RequestParam String state) throws Exception {
		OAuth2AccessToken accessToken = null;
		
		if (social.equals("naver")) {
			naverLogin.updateTest(naverValue);
			accessToken = naverLogin.compareAccessToken(session, code, state);
		}
		
		session.removeAttribute("reauthState");
		return "reauthRedirect";
	}
	
	@RequestMapping(value="/logout")
	public String logout(HttpSession session) {
		System.out.println("�α׾ƿ� �մϴ�!");
		session.invalidate();
		
		return "redirect:/";
	}
}

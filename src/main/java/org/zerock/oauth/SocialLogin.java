package org.zerock.oauth;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.zerock.domain.CustomerVO;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;


@Service
public class SocialLogin {
	private OAuth20Service oauth20Service;
	private String profileUrl;
	private String socialType;
	
	public SocialLogin() {
		
	}
	
	public SocialLogin(SocialValue social, HttpSession session) {
		String oauthState = generateState();
		session.setAttribute("oauthState", oauthState);
		
		this.oauth20Service = new ServiceBuilder()
				.apiKey(social.getClientId())
				.apiSecret(social.getClientSecret())
				.callback(social.getCallbackUrl())
				.state(oauthState)
				.build(social.getApi20Instance());
		
		this.profileUrl = social.getProfileUrl();
		this.socialType = social.getSocialType();
	}

	//���� ������ ���� ��ū(state token) ����
	public String generateState() {
		
		return UUID.randomUUID().toString();
	}

	public String getAuthorizationUrl() {
		// ���� url ����
		return this.oauth20Service.getAuthorizationUrl();
	}
	
	//accessToken ��������
	public OAuth2AccessToken getAccessToken(String code, String state, HttpSession session) throws Exception {
		if (state.equals(session.getAttribute("oauthState"))) {
			System.out.println("state, session�� oauthState ��ġ!");
			System.out.println("accessToken ����!");
			OAuth2AccessToken accessToken = this.oauth20Service.getAccessToken(code);
			
			return accessToken;
		}
		
		System.out.println("ERROR!:state, session�� oauthState ����ġ!");
		
		return null;
	}
	
	//������ ���� ��������
	public CustomerVO getProfile(OAuth2AccessToken accessToken) throws Exception {
		Response response = null;
		OAuthRequest request = new OAuthRequest(Verb.POST, this.profileUrl, this.oauth20Service);
		
		this.oauth20Service.signRequest(accessToken, request);
		response = request.send();
		
		System.out.println("������ response �Ϸ�!");
		return profileParsing(response.getBody());
	}
	
	//profile ������ JSON �����ͷ� ��ȯ�� �ʿ��� �����͸� CustomerVO�� ����.
	private CustomerVO profileParsing(String profileBody) throws Exception {
		CustomerVO customer = new CustomerVO();
		JSONParser jsonParse = new JSONParser();
		
		//JSONParse�� json�����͸� �־� �Ľ��� ���� JSONObject�� ��ȯ�Ѵ�. 
		JSONObject jsonObj = (JSONObject) jsonParse.parse(profileBody);
		jsonObj = (JSONObject) jsonObj.get("response");
		
		if (socialType.equals("naver")) {
			customer.setSocialType("naver");
			customer.setSocialId((String) jsonObj.get("id"));
			customer.setCustomerEmail((String) jsonObj.get("email"));
			customer.setCustomerPhone((String) jsonObj.get("mobile"));
			customer.setCustomerName((String) jsonObj.get("name"));
		}
		
		return customer;
	}
}

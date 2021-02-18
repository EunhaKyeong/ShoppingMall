package org.zerock.oauth;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.zerock.domain.CustomerVO;
import org.zerock.domain.SocialDetailVO;
import org.zerock.service.CustomerServiceImpl;
import org.zerock.service.SocialDetailServiceImpl;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class SocialLogin {
	@Resource
	SocialDetailServiceImpl sdService;
	
	@Resource
	CustomerServiceImpl customerService;
	
	private OAuth20Service oauth20Service;
	private String profileUrl;
	private String socialType;
	private SocialValue socialValue;

	public SocialLogin(SocialValue socialValue) {
		this.socialValue = socialValue; 
		this.profileUrl = socialValue.getProfileUrl();
		this.socialType = socialValue.getSocialType();
	}

	// ���� ������ ���� ��ū(state token) ����
	public String generateState() {

		return UUID.randomUUID().toString();
	}

	// �Ҽȷα��� ���� url ����
	public String getAuthorizationUrl(HttpSession session) {
		String oauthState = generateState();
		session.setAttribute("oauthState", oauthState);

		this.oauth20Service = new ServiceBuilder().apiKey(this.socialValue.getClientId())
				.apiSecret(this.socialValue.getClientSecret()).callback(this.socialValue.getCallbackUrl())
				.state(oauthState).build(this.socialValue.getApi20Instance());

		return this.oauth20Service.getAuthorizationUrl();
	}

	// ȸ��Ż��� �ʿ��� ������ url ����
	public String getReauthorizationUrl(HttpSession session) {
		String reauthState = generateState();
		session.setAttribute("reauthState", reauthState);

		this.oauth20Service = new ServiceBuilder().apiKey(this.socialValue.getClientId())
				.apiSecret(this.socialValue.getClientSecret()).callback(this.socialValue.getReauthCallbackUrl())
				.state(reauthState).build(this.socialValue.getApi20Instance());

		return this.oauth20Service.getAuthorizationUrl() + "&auth_type=reauthenticate";
	}

	// accessToken ��������
	public OAuth2AccessToken getAccessToken(String code, String state, HttpSession session) throws Exception {
		// �Ѿ�� state�� session�� ������ oauthState�� ��ġ�ϸ�
		if (state.equals(session.getAttribute("oauthState"))) {
			log.info("state, session�� oauthState ��ġ!");
			log.info("accessToken ����!");
			OAuth2AccessToken accessToken = this.oauth20Service.getAccessToken(code);

			return accessToken;
		}
		log.info("ERROR!:state, session�� oauthState ����ġ!");

		return null;
	}

	// ������ �� ���� accessToken�� ������ ���� �������� ���� �α��ε� �ִ� ����� ������ ��ġ�ϴ��� Ȯ��.
	public boolean compareAccessToken(HttpSession session, String code, String state) throws Exception {
		log.info("\n================================================================");
		log.info("�ݹ� �� ���� AccesstToken�� �̿��� ���� ������ ������ ����� ������ ��ġ�ϴ��� Ȯ���մϴ�.");
		
		boolean iscomplete = false;	//ȸ�� Ż�� ������ �����ߴ���, �����ߴ��� ����� ��� ���� boolean�� ����.
		
		if (state.equals(session.getAttribute("reauthState"))) {	//������ url�� ���� ���� state�� session�� ��� reauthState�� ��ġ�Ѵٸ�
			log.info("state, reauthState ��ġ!");
			long customerCode = (long) session.getAttribute("customerCode");	//���� �α��ε� �ִ� ������� ���ڵ�
			OAuth2AccessToken newAccessToken = this.oauth20Service.getAccessToken(code); // ���� �� �ݹ� code�� ���� accessToken
			CustomerVO newProfile = getProfile(newAccessToken);	//�� ��ū���� ���� ������ ����
			String socialId = customerService.getSocialId(customerCode);	//���� ������� socialId
			
			//���� ������� socialId�� newProfile�� socialId�� ��
			if (socialId.equals(newProfile.getSocialId())) {	//socialId�� ��ġ�Ѵٸ�
				log.info("�� ��ū���� ���� socialId�� ���� ������� socialId�� ��ġ�մϴ�.");
				//delete token�� ���� url�� �̵��ϱ� ���� deleteToken �޼ҵ� ����.
				log.info("token�� �����ϴ� deleteToken �޼ҵ带 �����մϴ�.");
				iscomplete = deleteToken(newAccessToken);	//Token ���Ḧ �����ϸ� iscomplete ������ true�� ����, �����ϸ� false�� ���.
				
				return iscomplete;	
			}
			else {	//socialId�� ��ġ���� �ʴ´ٸ�
				log.info("�� ��ū���� ���� socialId�� ���� ������� socialId�� ��ġ���� �ʽ��ϴ�.");
				
				return iscomplete;	//false
			}
		}
		log.info("state, reauthState ����ġ!");	//������ url callback�� ���� ���� state�� reauthState�� ��ġ���� �ʴ´ٸ�

		return iscomplete;	//false
	}

	// ������ ���� ��������
	public CustomerVO getProfile(OAuth2AccessToken accessToken) throws Exception {
		Response response = null;
		OAuthRequest request = new OAuthRequest(Verb.POST, this.profileUrl, this.oauth20Service);

		this.oauth20Service.signRequest(accessToken, request);
		response = request.send();
		log.info("������ response �Ϸ�!");
		
		//body�� ��� ������ ������ json ���·� ��ȯ�� customerVO�� ��� ���� �޼ҵ��� profileParsing ����.
		return profileParsing(response.getBody());
	}

	// profile ������ JSON �����ͷ� ��ȯ�� �ʿ��� �����͸� CustomerVO�� ����.
	private CustomerVO profileParsing(String profileBody) throws Exception {
		log.info("����� SocialLogin Ŭ������ profileParsing �޼ҵ�");
		CustomerVO customer = new CustomerVO();
		JSONParser jsonParse = new JSONParser();

		// JSONParse�� json�����͸� �־� �Ľ��� ���� JSONObject�� ��ȯ�Ѵ�.
		JSONObject jsonObj = (JSONObject) jsonParse.parse(profileBody);
		String resultCode = (String) jsonObj.get("resultcode");	//resultCode
		log.info("resultCode : " + resultCode);
		
		//�Ҽȷα��ο� īī���� �߰��Ǹ� ���̹��� īī���� ������ ���� ���°� �ٸ��� ������ if�� �ۼ�.
		if (socialType.equals("naver")) {
			if(resultCode.equals("00")) {	//naver������ ������ ���� ȣ���� �����ϸ� ������� 00.
				log.info("������ ������ ���������� ȣ���߽��ϴ�.");
				jsonObj = (JSONObject) jsonObj.get("response");
				
				//customerVO�� �ش� ������ ���� ���.
				customer.setSocialType("naver");
				customer.setSocialId((String) jsonObj.get("id"));
				customer.setCustomerEmail((String) jsonObj.get("email"));
				customer.setCustomerPhone((String) jsonObj.get("mobile"));
				customer.setCustomerName((String) jsonObj.get("name"));
			}
			
			return customer;
		}
		else {	//����ڵ尡 �����ڵ��
			log.info("������ ���� ȣ���� �����߽��ϴ�.");
			
			return null;
		}
	}

	//Token �����ؼ� ������Ʈ�� �Ͼ�� �� ����ϴ� �޼ҵ�
	public SocialDetailVO getSocialDetail(long customerCode, OAuth2AccessToken accessToken) {
		log.info("\n����� SocialLogin�� getSocialDetail");

		SocialDetailVO newTokenData = new SocialDetailVO();

		newTokenData.setCustomerCode(customerCode);
		newTokenData.setAccessToken(accessToken.getAccessToken());
		newTokenData.setRefreshToken(accessToken.getRefreshToken());

		return newTokenData;
	}
	
	//ȸ��Ż�� api url�� �̵� - Token delete(��ū ����) ó��
	public boolean deleteToken(OAuth2AccessToken accessToken) throws Exception {
		log.info("socialLogin Ŭ������ test �޼ҵ�");
		//��ū delete�� ���� api url ȣ��
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<MultiValueMap<String, String>> deleteTokenRequest = new HttpEntity<>(headers);
		
		//ȣ��� api url �̵��� ���� response ó��
		RestTemplate rt = new RestTemplate();
		String deleteApiUrl = "";	//�ش� social ��ū ���� api url
		//�Ҽȷα����� �������� ��ū ���� api url�� �ٸ��� ������ if�� �ۼ�.
		if (this.socialType.equals("naver")) {
			deleteApiUrl = this.socialValue.getDeleteTokenUrl() + "&client_id=" + this.socialValue.getClientId() + 
					"&client_secret=" + this.socialValue.getClientSecret() + "&access_token=" + accessToken.getAccessToken() + "&service_provider=NAVER";
		}
		ResponseEntity<String> response = rt.exchange(
				  deleteApiUrl,
			      HttpMethod.POST,
			      deleteTokenRequest,
			      String.class
		);
		
		//�� response�� body�κ��� json ���·� ��ȯ.
		JSONParser jsonParse = new JSONParser();
		JSONObject jsonObj = (JSONObject) jsonParse.parse(response.getBody());
		String result = (String) jsonObj.get("result");	//��ū ���� ����� ��� ���� result ����.
		if (result.equals("success")) {	//��ū ���Ḧ ����������
			log.info("��ū ������ �Ϸ��߽��ϴ�.");
			
			return true;
		}
		else {	//��ū ���Ḧ �����ߴٸ�
			log.info("��ū ������ �����߽��ϴ�.");
			
			return false;
		}
	}
}

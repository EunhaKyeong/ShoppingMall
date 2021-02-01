package org.zerock.oauth;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.zerock.domain.CustomerVO;
import org.zerock.domain.SocialDetailVO;
import org.zerock.service.SocialDetailServiceImpl;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import lombok.RequiredArgsConstructor;

@Service
public class SocialLogin {
	private OAuth20Service oauth20Service;
	private String profileUrl;
	private String socialType;
	private SocialValue socialValue;

	public void updateTest(SocialValue social) {
		this.socialValue = social;
		this.profileUrl = social.getProfileUrl();
		this.socialType = social.getSocialType();
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
			System.out.println("state, session�� oauthState ��ġ!");
			System.out.println("accessToken ����!");
			OAuth2AccessToken accessToken = this.oauth20Service.getAccessToken(code);

			return accessToken;
		}

		System.out.println("ERROR!:state, session�� oauthState ����ġ!");

		return null;
	}

	// ������ �� ���� accessToken�� DB�� ����� accessToken�� ��ġ�ϴ��� Ȯ���ϴ� �۾�
	public OAuth2AccessToken compareAccessToken(HttpSession session, String code, String state) throws IOException {
		System.out.println("================================================================");
		System.out.println("�ݹ� �� ���� AccesstToken�� DB�� ����� accessToken�� ���մϴ�.");
		SocialDetailVO socialDetail = new SocialDetailVO();

		if (state.equals(session.getAttribute("reauthState"))) {
			System.out.println("state, reauthState ��ġ!");
			String newAccessToken = this.oauth20Service.getAccessToken(code).getAccessToken(); // ���� �� �ݹ� code�� ����
																								// accessToken
			/* sdService.findBySocialDetail((long) session.getAttribute("customerCode")); */
		}

		System.out.println("state, reauthState ����ġ!");

		return null;
	}

	// ������ ���� ��������
	public CustomerVO getProfile(OAuth2AccessToken accessToken) throws Exception {
		Response response = null;
		OAuthRequest request = new OAuthRequest(Verb.POST, this.profileUrl, this.oauth20Service);

		this.oauth20Service.signRequest(accessToken, request);
		response = request.send();

		System.out.println("������ response �Ϸ�!");
		return profileParsing(response.getBody());
	}

	// profile ������ JSON �����ͷ� ��ȯ�� �ʿ��� �����͸� CustomerVO�� ����.
	private CustomerVO profileParsing(String profileBody) throws Exception {
		CustomerVO customer = new CustomerVO();
		JSONParser jsonParse = new JSONParser();

		// JSONParse�� json�����͸� �־� �Ľ��� ���� JSONObject�� ��ȯ�Ѵ�.
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

	public SocialDetailVO getSocialDetail(long customerCode, OAuth2AccessToken accessToken) {
		System.out.println("\n����� SocialLogin�� insertTokenData");
		System.out.println(accessToken);

		SocialDetailVO newTokenData = new SocialDetailVO();

		newTokenData.setCustomerCode(customerCode);
		newTokenData.setAccessToken(accessToken.getAccessToken());
		newTokenData.setRefreshToken(accessToken.getRefreshToken());

		return newTokenData;
	}
}

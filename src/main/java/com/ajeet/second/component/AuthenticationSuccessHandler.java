package com.ajeet.second.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Value("${jwt.expires_in}")
	private int EXPIRES_IN;

	@Value("${jwt.jsession.id}")
	private String AUTH_JSESSIONID;

	@Value("${jwt.header}")
	private String AUTH_HEADER;

	@Autowired
	TokenHelper tokenHelper;

	private String getJSessionIdFromRequest( HttpServletRequest request ) {


		Cookie[] cookies = request.getCookies();
		Map<String, String> cookiesMap = new HashMap<>();
		if (cookies != null) {
			Arrays.stream(cookies)
			.forEach(c -> cookiesMap.put(c.getName(), c.getValue())/*System.out.println(c.getName() + "=" + c.getValue())*/);
		}

		String jSessionId = cookiesMap.get(AUTH_JSESSIONID);
		return jSessionId;
	}


	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		clearAuthenticationAttributes(request);
		User user = (User) authentication.getPrincipal();
		String jsessionId = getJSessionIdFromRequest(request);
		String jwt = null;
		if(jsessionId!=null)
			jwt = tokenHelper.generateToken(user.getUsername(),jsessionId);

		UserTokenState userTokenState = new UserTokenState(jwt, EXPIRES_IN);
		try {
			String jwtResponse = new ObjectMapper().writeValueAsString(userTokenState);
			//response.setContentType("application/json");
			Cookie newCookie = new Cookie(AUTH_HEADER, "Bearer "+jwt);
			newCookie.setMaxAge(EXPIRES_IN);
			//newCookie.setSecure(true);
			response.addCookie(newCookie);
			//response.getWriter().write(jwtResponse);
			SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
			if (savedRequest != null) {
				response.sendRedirect(savedRequest.getRedirectUrl());
			}
			else{
				response.sendRedirect("portal/welcome");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private class UserTokenState {
		private String jws;
		private int expires;

		public UserTokenState(String jws, int expires) {
			this.jws = jws;
			this.expires = expires;
		}

		public String getJws() {
			return jws;
		}

		public void setJws(String jws) {
			this.jws = jws;
		}

		public int getExpires() {
			return expires;
		}

		public void setExpires(int expire) {
			this.expires = expire;
		}
	}
}
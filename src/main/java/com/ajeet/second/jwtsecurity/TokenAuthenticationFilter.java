package com.ajeet.second.jwtsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ajeet.second.component.TokenHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

	@Value("${jwt.header}")
	private String AUTH_HEADER;

	@Value("${jwt.jsession.id}")
	private String AUTH_JSESSIONID;

	@Autowired
	TokenHelper tokenHelper;

	@Autowired
	UserDetailsService userDetailServiceImpl;

	private String getToken( HttpServletRequest request ) {
		String authHeader = null;
		/* String authHeader = request.getHeader(AUTH_HEADER);
        if ( authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }*/

		Map<String, String> cookiesMap = getCookieMap(request);
		authHeader = cookiesMap.get(AUTH_HEADER);
		if ( authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}

		return null;
	}

	private String getJsessionId(HttpServletRequest request)
	{
		Map<String, String> cookiesMap = getCookieMap(request);
		return cookiesMap.get(AUTH_JSESSIONID);
	}

	private Map<String, String> getCookieMap(HttpServletRequest request)
	{
		Cookie[] cookies = request.getCookies();
		Map<String, String> cookiesMap = new HashMap<>();
		if (cookies != null) {
			Arrays.stream(cookies)
			.forEach(c -> cookiesMap.put(c.getName(), c.getValue())/*System.out.println(c.getName() + "=" + c.getValue())*/);
		}

		return cookiesMap;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

		try
		{

		String error = "";
		String authToken = getToken( request );

		if (authToken != null) {

			// Get username from token

			String username = tokenHelper.getUsernameFromToken( authToken );
			String tokenJsessionId = tokenHelper.getJSessionIdFromToken(authToken);
			String requestJsessionId = getJsessionId(request);
			if ( username != null && requestJsessionId.equals(tokenJsessionId)) {

				// Get user
				UserDetails userDetails = userDetailServiceImpl.loadUserByUsername( username );

				// Create authentication
				TokenBasedAuthentication authentication = new TokenBasedAuthentication( userDetails );
				authentication.setToken( authToken );
				SecurityContextHolder.getContext().setAuthentication( authentication );
			} else {
				error = "Username from token can't be found in DB. or jsessionId incorrect";
			}
		} else {
			error = "Authentication failed - no Bearer token provided.";
		}
		if( ! error.equals("")){
			System.out.println(error);
			//throw new BadCredentialsException("Hello");
			SecurityContextHolder.getContext().setAuthentication( new AnonAuthentication() );//prevent show login form...
		}
		chain.doFilter(request, response);
	 } catch (AuthenticationException authenticationException) {
	        SecurityContextHolder.clearContext();
	        response.sendError(HttpServletResponse.SC_FORBIDDEN, authenticationException.getMessage());
	    }

	}

}
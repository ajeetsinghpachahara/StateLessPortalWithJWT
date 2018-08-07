package com.ajeet.second.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ajeet.second.component.CustomPasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

   

   /* @Autowired
    private ObjectMapper objectMapper;*/
	@Autowired
	private CustomPasswordEncoder customPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

       /* User user = (User)cache.getItem("user/"+username, User.class);
        if( user == null){
            user = userRepository.findByEmail(username);
        }
        if( user == null){
            throw new UsernameNotFoundException("No user found. Username tried: " + username);
        }
        cache.setItem("user/"+username, user);*/
    	
    	if(!username.equals("ajeet")){
    		 throw new UsernameNotFoundException("No user found. Username tried: " + username);
    	}

        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        return new org.springframework.security.core.userdetails.User("ajeet", customPasswordEncoder.encode("ajeet"), grantedAuthorities);
    }
}

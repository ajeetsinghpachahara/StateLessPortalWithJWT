package com.ajeet.first.jwtcontroller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ajeet.first.jwtmodel.JwtUser;
import com.ajeet.first.jwtutils.JwtGenerator;



@RestController
public class TokenController {

    @Autowired
    private JwtGenerator jwtGenerator;

   /* public TokenController(JwtGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }*/

    @RequestMapping(value = { "/token" }, method = RequestMethod.POST)
    public String generate(@RequestBody final JwtUser jwtUser) {

        return jwtGenerator.generate(jwtUser);

    }
}

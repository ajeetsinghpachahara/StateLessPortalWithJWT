package com.ajeet.second.jwtcontroller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {


	// Spring Security see this :
	@RequestMapping(value = {"/login" }, method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest request, @RequestParam(name="error", required=false) boolean error,  @RequestParam(name="logout", required=false) boolean logout) {

		ModelAndView model = new ModelAndView();
		model.setViewName("index");
		return model;

	}
	
	@RequestMapping(value = {"/403", "/portal/403" }, method = RequestMethod.GET)
	public ModelAndView accessDenied(HttpServletRequest request, @RequestParam(name="error", required=false) boolean error,  @RequestParam(name="logout", required=false) boolean logout) {

		ModelAndView model = new ModelAndView();
		model.setViewName("403");
		return model;

	}

	@RequestMapping(value = { "/portal/welcome" }, method = RequestMethod.GET)
	public ModelAndView welcome(HttpServletRequest request) {

		ModelAndView model = new ModelAndView();
		model.setViewName("welcome");
		return model;

	}

}

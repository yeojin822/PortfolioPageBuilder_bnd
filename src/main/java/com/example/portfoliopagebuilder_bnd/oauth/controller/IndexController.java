package com.example.portfoliopagebuilder_bnd.oauth.controller;

import com.example.portfoliopagebuilder_bnd.oauth.dto.PrincipalDetails;
import com.example.portfoliopagebuilder_bnd.oauth.model.User;
import com.example.portfoliopagebuilder_bnd.oauth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

   // ::Todo health Check
	@GetMapping({"","/"})
	public @ResponseBody String index(){
		log.info("test ");
		return "Test";
	}

	// 시큐리티 세션에 있는 Authentication 객체 안에 UserDetails, OAuth2User 객체를 저장할 수 있음
	// UserDetails: 기존 로그인 유저 객체
	// OAuth2User: OAuth 로그인 유저 객체
	// 그래서 PrincipalDetails에 UserDetails, OAuth2User 모두 구현해야 함

	// Authentication 객체 안에 인증된 유저 정보 가져오기
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal PrincipalDetails userDetails){
		log.info("==== /test/login =====");
		PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
		log.info("authentication : " + principalDetails.getUser());
		log.info("userDetails : " + userDetails.getUser());

		return "세션 정보 확인하기";
	}

	// Authentication 객체 안에 oauth 인증된 유저 정보 가져오기
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User){
		log.info("==== /test/oauth/login =====");
		OAuth2User oAuth2UserObj = (OAuth2User) authentication.getPrincipal();
		log.info("authentication : " + oAuth2UserObj.getAttributes());
		log.info("oAuth2User : " + oAuth2User.getAttributes());

		return "OAuth 세션 정보 확인하기";
	}

	// loadUser, loadUserByUsername에서 각각 로그인 유저 객체를 Authentication에 저장해줌
	// @AuthenticationPrincipal 어노테이션으로 Authentication에 저장되어 있는 일반, OAuth 로그인 유저 객체 모두 받아올 수 있음
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		log.info("User : " + principalDetails.getUser());
		return "user";
	}

	@GetMapping("/admin")
	public @ResponseBody String admin(){
		return "admin";
	}

	@GetMapping("/manager")
	public @ResponseBody String manager(){
		return "manager";
	}

	@GetMapping("/loginForm")
	public String loginForm(){
		return "loginForm";
	}

	@GetMapping("/joinForm")
	public String joinForm(){
		return "joinForm";
	}

	@PostMapping("/join")
	public String join(User user) {
		user.setRole("ROLE_USER");
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userRepository.save(user);

		return "redirect:/loginForm";
	}

	@Secured("ROLE_ADMIN")
	@GetMapping("/data")
	public @ResponseBody String data(){
		return "data only admin";
	}

	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/users")
	public @ResponseBody String userList(){
		return "user list only manager, admin";
	}
}
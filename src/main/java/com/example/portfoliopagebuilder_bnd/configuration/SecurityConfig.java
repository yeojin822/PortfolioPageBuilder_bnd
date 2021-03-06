package com.example.portfoliopagebuilder_bnd.configuration;

import com.example.portfoliopagebuilder_bnd.handler.OAuth2SuccessHandler;
import com.example.portfoliopagebuilder_bnd.service.oauth.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// oauth2 과정
// 1.코드받기(인증), 2.액세스토큰(권한),
// 3-1.사용자프로필 정보를 가지고 회원가입 진행
// 3-2. 기본 데이터 (이메일, 전화번호, 이름, 아이디)
// 쇼핑몰 -> (집주소) 정보 필요 / 백화점몰 -> (vip, 일반등급) 정보 필요

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CustomOAuth2UserService oAuth2UserService;
	private final OAuth2SuccessHandler successHandler;

	@Bean
	public BCryptPasswordEncoder encodePassword(){
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http	.csrf().disable()
				.formLogin().disable()
				.httpBasic().disable()
				.oauth2Login()
				.authorizationEndpoint()
				.baseUri("/oauth2/authorization")
				.and()
				.redirectionEndpoint()
				.baseUri("/*/oauth2/code/*")
				.and()
				.userInfoEndpoint()
				.userService(oAuth2UserService)
				.and()
				.successHandler(successHandler)
				.failureUrl("/");

//		http.csrf().disable();
//		http.oauth2Login()
//			.successHandler(successHandler)
//			.userInfoEndpoint() // 로그인이 완료되면 코드가 아닌 (엑세스 토큰 + 사용자 프로필 정보)를 받음
//			.userService(oAuth2UserService);
	}
}

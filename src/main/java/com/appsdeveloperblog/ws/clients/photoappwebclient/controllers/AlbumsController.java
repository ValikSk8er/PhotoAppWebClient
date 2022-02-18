package com.appsdeveloperblog.ws.clients.photoappwebclient.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.appsdeveloperblog.ws.clients.photoappwebclient.response.AlbumRest;

@Controller
public class AlbumsController {

	@Autowired
	OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

	@Autowired
	RestTemplate restTemplate;

	@GetMapping("/albums")
	public String getAlbums(Model model,
							@AuthenticationPrincipal OidcUser principal) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

		OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(),
				oauthToken.getName());

		String jwtAccessToken = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
		System.out.println("jwtAccessToken " + jwtAccessToken);

		System.out.println("Principal " + principal);
		OidcIdToken idToken = principal.getIdToken();
		String tokenValue = idToken.getTokenValue();
		System.out.println("idTokenValue = " + tokenValue);

		String url = "http://localhost:8082/albums";
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + jwtAccessToken);

		HttpEntity<List<AlbumRest>> httpEntity = new HttpEntity<>(httpHeaders);

		ResponseEntity<List<AlbumRest>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<AlbumRest>>() {});

		List<AlbumRest> albumRests = responseEntity.getBody();

//		AlbumRest album = new AlbumRest();
//		album.setAlbumId("albumOne");
//		album.setAlbumTitle("Album one title");
//		album.setAlbumUrl("http://localhost:8082/albums/1");
//
//		AlbumRest album2 = new AlbumRest();
//		album2.setAlbumId("albumTwo");
//		album2.setAlbumTitle("Album two title");
//		album2.setAlbumUrl("http://localhost:8082/albums/2");
//
//		List<AlbumRest> albumRests = Arrays.asList(album, album2);

		model.addAttribute("albums", albumRests);

		return "albums";
	}
	
}

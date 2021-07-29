package com.kafka.validation.util;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ServiceConnector {

	private RestTemplate restTemplate;

	@Autowired
	public ServiceConnector(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	/**
	 * Generic method for HTTP post request
	 * @param <T>
	 * @param url
	 * @param request
	 * @param responseType
	 * @param uriVariables
	 * @return T
	 */
	public <T> T post(String url, Object request, Class<T> responseType, Map<String, Object> uriVariables) {
		return restTemplate.postForObject(url, request, responseType, uriVariables);
	}
	
	/**
	 * Generic method for HTTP get request
	 * @param <T>
	 * @param url
	 * @param responseType
	 * @return T
	 */
	public <T> T get(String url, Class<T> responseType) {
		return restTemplate.getForObject(url, responseType);
	}
	
	/**
	 * Generic method for HTTP patch request
	 * @param <T>
	 * @param url
	 * @param request
	 * @param responseType
	 * @return
	 */
	public <T> T patch(String url, Object request, Class<T> responseType) {
		return restTemplate.patchForObject(url, request, responseType);
	}
	
	/**
	 * Generic method for HTTP request
	 * @param <T>
	 * @param url
	 * @param method
	 * @param request
	 * @param responseType
	 * @param params
	 * @return T
	 */
	public <T> T exchange(String url, HttpMethod method, Object request, Class<T> responseType, Map<String, String> params) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", "application/json");
		HttpEntity<Object> entity = new HttpEntity<>(request, headers);
		ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType, params);
		return response.getBody();
	}
}

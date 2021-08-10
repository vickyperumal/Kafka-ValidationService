package com.kafka.validation.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.common.model.LoanDetails;
import com.kafka.validation.service.ValidationService;


@ExtendWith(SpringExtension.class)
class ValidationLoanControllerTest {
	
	private MockMvc mockMvc;
	
	@Mock
	private ValidationService service;
	
	@Mock
	List<LoanDetails> details;
	
	private ObjectMapper mapper;

	@BeforeEach
	void setUp() throws Exception {
		mapper=new ObjectMapper();
		this.mockMvc = MockMvcBuilders.standaloneSetup(new ValidationLoanController(service)).build();
		details = Arrays.asList(new LoanDetails("loanType", 1000L, "loanDate", 5.00, 24, 1234L, 100000L, 5));
	}
	
	@Test
	void testApplyLoan() throws JsonProcessingException, Exception {
	doNothing().when(service).sendMessage(details);
	MvcResult result = this.mockMvc.perform(post("/applyLoan")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(details)))
			.andExpect(status().isOk())
			.andReturn();
	assertNotNull(result.getResponse());
	assertTrue(result.getResponse().getContentAsString().contains("Success"));
	verify(service, times(1)).sendMessage(details);
	
	}
	
	@Test
	void testApplyLoanError() throws JsonProcessingException, Exception {
		details.get(0).setCustomerId(null);
		 this.mockMvc.perform(post("/applyLoan")
					.contentType(MediaType.APPLICATION_JSON)
					.content(mapper.writeValueAsString(details)))
					.andExpect(status().isOk())
					.andReturn();
		
		}
	}



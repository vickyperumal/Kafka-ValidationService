package com.kafka.validation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kafka.common.model.LoanDetails;
import com.kafka.validation.service.KafkaProducerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ValidationLoanController {

	@Autowired
	private KafkaProducerService service;
	@PostMapping(value="/applyLoan")
	public String applyLoan(@RequestBody List<LoanDetails> details) {
		log.info("Processing loan for the customer {}",details.get(0).getCustomerId());
		try {
		service.sendMessage(details);
		return "Loan Applied Successfully";
		}
		catch (Exception e) {
			log.error("Error occured during processing loans {}",e.getMessage());
			return "Unable to apply loan";
		}
		
	}
}

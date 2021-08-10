package com.kafka.validation.service;

import java.util.List;

import com.kafka.common.model.LoanDetails;

public interface ValidationService {

	void sendMessage(List<LoanDetails> loans);
}

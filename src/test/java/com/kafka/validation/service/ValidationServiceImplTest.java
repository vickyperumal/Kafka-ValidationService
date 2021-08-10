package com.kafka.validation.service;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import com.kafka.common.model.LoanDetails;
import com.kafka.common.model.ProcessLoanDetails;
import com.kafka.validation.util.ServiceConnector;

class ValidationServiceImplTest {

	@InjectMocks
	private ValidationServiceImpl service;

	private List<LoanDetails> details;
	
	
	@Mock
	private ServiceConnector serviceConnector;
	
	
	
	@Mock
	private KafkaTemplate<String, ProcessLoanDetails> kafkaTemplate;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

		details = Arrays.asList(new LoanDetails("loanType", 1000L, "loanDate", 5.00, 24, 1234L, 100000L, 5));
	}

	@Test
	void testSendMessage() {
		SettableListenableFuture<SendResult<String, ProcessLoanDetails>> future=new SettableListenableFuture<SendResult<String,ProcessLoanDetails>>();
		when(kafkaTemplate.send(Mockito.anyString(),Mockito.anyString(),Mockito.any(ProcessLoanDetails.class))).thenReturn(future);
		when(serviceConnector.get(Mockito.anyString(), Mockito.eq(Integer.class))).thenReturn(100);
		service.sendMessage(details);
		verify(serviceConnector).get(Mockito.anyString(), Mockito.eq(Integer.class));
		verify(kafkaTemplate).send(Mockito.anyString(),Mockito.anyString(),Mockito.any(ProcessLoanDetails.class));
		
	}

}

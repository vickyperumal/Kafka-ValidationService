package com.kafka.validation.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.kafka.common.model.LoanDetails;
import com.kafka.common.model.ProcessLoanDetails;
import com.kafka.validation.util.ServiceConnector;

@Service
public class KafkaProducerService {

	@Autowired
	private ServiceConnector serviceConnector;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private KafkaTemplate<String, ProcessLoanDetails> kafkaTemplate;

	public void sendMessage(List<LoanDetails> loans) {
		String key = UUID.randomUUID().toString();
		ProcessLoanDetails user = validateLoanDetails(loans);
		ListenableFuture<SendResult<String, ProcessLoanDetails>> future = kafkaTemplate.send("loan-topic", key, user);
		future.addCallback(new ListenableFutureCallback<SendResult<String, ProcessLoanDetails>>() {

			@Override
			public void onSuccess(SendResult<String, ProcessLoanDetails> result) {
				logger.info("Sent message[{}] with offset [{}] to partition [{}]", user,
						result.getRecordMetadata().offset(), result.getRecordMetadata().partition());
			}

			@Override
			public void onFailure(Throwable ex) {
				logger.error("Unable to send message [{}] due to {}", user, ex.getMessage());
			}
		});
	}

	private ProcessLoanDetails validateLoanDetails(List<LoanDetails> loans) {

		ProcessLoanDetails processDetails = new ProcessLoanDetails();
		Integer creditScore = 100;
		Integer updatedCreditScore = null;
		if (creditScore >= 40) {
			for (LoanDetails loanDetail : loans) {
				updatedCreditScore = 0;
				double salary = loanDetail.getMonthlySalary().doubleValue();
				Long interestPerMonth = loanDetail.getLoanAmount() / 100;
				double RemainingSalary = calculateRemainingSalary(salary, loanDetail.getLoanAmount(), interestPerMonth);
				Integer myCreditScore = calculateCreditScore(RemainingSalary, salary, interestPerMonth);

				Integer creditScoreForEachLoan = serviceConnector.get(
						"//Customer-Service/v1/customerService/getCustomerCreditScore/" + loanDetail.getCustomerId(),
						Integer.class);
				logger.info("Credit score for customer id : {} is {}", loanDetail.getCustomerId(),
						creditScoreForEachLoan);
				updatedCreditScore = creditScoreForEachLoan - myCreditScore;
				loanDetail.setCreditScore(myCreditScore);

			}
			processDetails.setLoanDetails(loans);
			processDetails.setCreditScore(updatedCreditScore);

			return processDetails;

		}
		return null;
	}

	private Integer calculateCreditScore(double remainingSalary, double salary, Long interestAmount) {
		Double myCreditScore = (salary + remainingSalary) / interestAmount;
		return myCreditScore.intValue();
	}

	private double calculateRemainingSalary(double monthlySalary, Long loanAmount, Long interestAmount) {
		double remainingSalary = monthlySalary - interestAmount;
		return remainingSalary;
	}

}

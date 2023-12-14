package com.konopleva.crudeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konopleva.crudeapp.dto.OrderDto;
import com.konopleva.crudeapp.dto.kafka.DeliveryState;
import com.konopleva.crudeapp.dto.kafka.OrderState;
import com.konopleva.crudeapp.dto.kafka.UserStateDto;
import com.konopleva.crudeapp.entity.AccountBilling;
import com.konopleva.crudeapp.exception.NoEnoughMoneyException;
import com.konopleva.crudeapp.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillingService {
    @Value("${kafka.topics.order-payment-accepted}")
    private String orderPayedTopic;
    @Value("${kafka.topics.order-payment-declined}")
    private String orderDeclinedTopic;
    private final ObjectMapper objectMapper;

    private final AccountRepository accountRepository;

    private final KafkaTemplate<String, OrderState> kafkaTemplate;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deposit(BigDecimal value) {
        var accountId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var account = accountRepository.findByEmail(accountId);
        if (account.isPresent()) {
            var accountEntity = account.get();
            accountEntity.setBalance(accountEntity.getBalance().add(value));
        } else {
            throw new EntityNotFoundException("Billing account doesnt exist");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void withdraw(BigDecimal value) {
        var userEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        withdrawByEmail(value, userEmail);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void withdrawByEmail(BigDecimal value, String email) {
        var account = accountRepository.findByEmail(email);
        if (account.isPresent()) {
            var accountEntity = account.get();
            var accountBalance = accountEntity.getBalance();
            if (accountBalance.compareTo(value) >= 0) {
                accountEntity.setBalance(accountEntity.getBalance().subtract(value));
                log.info("Withdraw {} from account {}",
                        value,
                        accountEntity.getEmail());
            } else {
                log.info("Can't withdraw {} from account {}, because account balance is {}",
                        value,
                        accountEntity.getEmail(),
                        accountEntity.getBalance());
                throw new NoEnoughMoneyException(String.format("Can't withdraw %s$, because user %s account balance is %s$",
                        value, accountEntity.getEmail(), accountEntity.getBalance()));
            }
        } else {
            throw new EntityNotFoundException("Billing account doesnt exist");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processUserStateKafkaCommand(String value) throws JsonProcessingException {
        var userStateDto = objectMapper.readValue(value, UserStateDto.class);
        log.info("Kafka message about user with name {} {} and email: {} consumed. Start creating billing account...",
                userStateDto.getFirstName(),
                userStateDto.getLastName(),
                userStateDto.getEmail());
        createBillingAccount(userStateDto.getEmail(), userStateDto.getFirstName(), userStateDto.getLastName());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processOrderStateKafkaCommand(String value) throws JsonProcessingException {
        var orderState = objectMapper.readValue(value, OrderState.class);
        log.info("Kafka message OrderState consumed. Parameters: id = {}, user = {}, price = {}. " +
                        "Trying to withdraw money...",
                orderState.getId(), orderState.getAssociatedUserEmail(), orderState.getPrice());
        payOrder(orderState);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processItemReserveCancelledKafkaMessage(String value) throws JsonProcessingException {
        var orderState = objectMapper.readValue(value, OrderState.class);
        log.info("Kafka message No available item of type {} in stock consumed. Parameters: id = {}, user = {}, price = {}. " +
                        "Trying to cancel payment...",
                orderState.getDescription(), orderState.getId(), orderState.getAssociatedUserEmail(), orderState.getPrice());
        cancelPayment(orderState);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processDeliveryCancelledKafkaMessage(String value) throws JsonProcessingException {
        var deliveryState = objectMapper.readValue(value, DeliveryState.class);
        log.info("Kafka message Delivery Cancelled consumed. Parameters: order id = {}, user = {}, adress = {}. " +
                        "Trying to cancel payment...",
                deliveryState.getOrderId(), deliveryState.getAssociatedUserEmail(), deliveryState.getAddress());
        cancelPaymentDueToDeliveryCancelled(deliveryState);
    }

    private void cancelPaymentDueToDeliveryCancelled(DeliveryState deliveryState) {
        var account = accountRepository.findByEmail(deliveryState.getAssociatedUserEmail());
        if (account.isPresent()) {
            var accountEntity = account.get();
            accountEntity.setBalance(accountEntity.getBalance().add(BigDecimal.valueOf(deliveryState.getPrice())));
        }
    }

    private void cancelPayment(OrderState orderState) {
        var account = accountRepository.findByEmail(orderState.getAssociatedUserEmail());
        if (account.isPresent()) {
            var accountEntity = account.get();
            accountEntity.setBalance(accountEntity.getBalance().add(BigDecimal.valueOf(orderState.getPrice())));
        }
    }

    private void createBillingAccount(String email, String firstname, String lastname) {
        var entity = accountRepository.findByEmail(email);
        entity.ifPresentOrElse(e -> log.info("Account with email = {} is already exists", email),
                () -> {
            var account = new AccountBilling()
                        .setEmail(email)
                        .setUsername(firstname + " " + lastname)
                        .setCreateDate(LocalDate.now())
                        .setUpdatedTime(LocalDateTime.now());
            accountRepository.save(account);
            log.info("Billing account for user with name {} {} and email: {} created", firstname, lastname, email);
        });

    }

    @Transactional(readOnly = true)
    public AccountBilling getAccount() {
        var userEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var account = accountRepository.findByEmail(userEmail);
        if (account.isPresent()) {
            return account.get();
        } else {
            throw new EntityNotFoundException("Billing account doesnt exist");
        }
    }

    public void payOrder(OrderState orderState) {
        try {
            withdrawByEmail(BigDecimal.valueOf(orderState.getPrice()), orderState.getAssociatedUserEmail());
            orderState.setStatus("PAYED");
            kafkaTemplate.send(orderPayedTopic, orderState);
            log.info("Send message to kafka about successful payment for order with id {}", orderState.getId());
        } catch (Exception exception) {
            orderState.setStatus("CANCELLED");
            kafkaTemplate.send(orderDeclinedTopic, orderState);
            log.info("Send message to kafka about payment declined for order with id {}", orderState.getId());
        }
    }
}

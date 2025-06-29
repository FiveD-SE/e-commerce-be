package com.pm.paymentservice.service;

import com.pm.paymentservice.dto.CreatePaymentMethodRequest;
import com.pm.paymentservice.dto.PaymentMethodDto;
import com.pm.paymentservice.dto.response.collection.CollectionResponse;
import com.pm.paymentservice.model.PaymentGateway;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentMethodService {
    
    // Payment Method Creation and Management
    PaymentMethodDto createPaymentMethod(CreatePaymentMethodRequest request);
    PaymentMethodDto getPaymentMethodById(Long methodId);
    PaymentMethodDto updatePaymentMethod(Long methodId, PaymentMethodDto paymentMethodDto);
    void deletePaymentMethod(Long methodId);
    
    // User Payment Methods
    List<PaymentMethodDto> getPaymentMethodsByUserId(Integer userId);
    CollectionResponse<PaymentMethodDto> getPaymentMethodsByUserId(Integer userId, Pageable pageable);
    List<PaymentMethodDto> getActivePaymentMethodsByUserId(Integer userId);
    PaymentMethodDto getDefaultPaymentMethodByUserId(Integer userId);
    
    // Payment Method Types and Gateways
    List<PaymentMethodDto> getPaymentMethodsByUserIdAndType(Integer userId, String type);
    List<PaymentMethodDto> getPaymentMethodsByUserIdAndGateway(Integer userId, PaymentGateway gateway);
    List<PaymentMethodDto> getCardsByUserId(Integer userId);
    
    // Payment Method Status Management
    PaymentMethodDto activatePaymentMethod(Long methodId);
    PaymentMethodDto deactivatePaymentMethod(Long methodId);
    PaymentMethodDto setAsDefaultPaymentMethod(Long methodId);
    PaymentMethodDto verifyPaymentMethod(Long methodId);
    
    // Payment Method Validation
    boolean isPaymentMethodValid(Long methodId);
    boolean isCardExpired(Long methodId);
    List<PaymentMethodDto> getExpiringCards(Integer userId, int months);
    
    // Analytics
    long countPaymentMethodsByUserId(Integer userId);
    long countActivePaymentMethodsByUserId(Integer userId);
    long countPaymentMethodsByUserIdAndType(Integer userId, String type);
}

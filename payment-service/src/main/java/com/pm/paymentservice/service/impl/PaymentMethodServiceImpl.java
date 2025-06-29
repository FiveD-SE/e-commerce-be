package com.pm.paymentservice.service.impl;

import com.pm.paymentservice.dto.CreatePaymentMethodRequest;
import com.pm.paymentservice.dto.PaymentMethodDto;
import com.pm.paymentservice.dto.response.collection.CollectionResponse;
import com.pm.paymentservice.mapper.PaymentMethodMapper;
import com.pm.paymentservice.model.PaymentGateway;
import com.pm.paymentservice.model.PaymentMethod;
import com.pm.paymentservice.repository.PaymentMethodRepository;
import com.pm.paymentservice.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    // ==================== Payment Method Creation and Management ====================
    
    @Override
    public PaymentMethodDto createPaymentMethod(CreatePaymentMethodRequest request) {
        log.info("Creating payment method for user ID: {}", request.getUserId());
        
        // If setting as default, unset other default methods
        if (Boolean.TRUE.equals(request.getSetAsDefault())) {
            unsetDefaultPaymentMethods(request.getUserId());
        }
        
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .userId(request.getUserId())
                .name(request.getName())
                .type(request.getType())
                .gateway(request.getGateway())
                .cardHolderName(request.getCardHolderName())
                .cardExpMonth(request.getCardExpMonth())
                .cardExpYear(request.getCardExpYear())
                .bankName(request.getBankName())
                .accountType(request.getAccountType())
                .walletEmail(request.getWalletEmail())
                .walletPhone(request.getWalletPhone())
                .isActive(true)
                .isDefault(Boolean.TRUE.equals(request.getSetAsDefault()))
                .isVerified(false)
                .billingAddress(request.getBillingAddress())
                .metadata(request.getMetadata())
                .notes(request.getNotes())
                .build();
        
        // Mask sensitive information
        if (request.getCardNumber() != null && request.getCardNumber().length() >= 4) {
            paymentMethod.setCardLastFour(request.getCardNumber().substring(request.getCardNumber().length() - 4));
        }
        
        if (request.getBankAccountNumber() != null && request.getBankAccountNumber().length() >= 4) {
            paymentMethod.setAccountLastFour(request.getBankAccountNumber().substring(request.getBankAccountNumber().length() - 4));
        }
        
        // TODO: Integrate with payment gateway to store payment method securely
        
        PaymentMethod savedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        log.info("Payment method created successfully with ID: {}", savedPaymentMethod.getMethodId());
        
        return paymentMethodMapper.toDTO(savedPaymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMethodDto getPaymentMethodById(Long methodId) {
        log.info("Fetching payment method by ID: {}", methodId);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + methodId));
        return paymentMethodMapper.toDTO(paymentMethod);
    }

    @Override
    public PaymentMethodDto updatePaymentMethod(Long methodId, PaymentMethodDto paymentMethodDto) {
        log.info("Updating payment method ID: {}", methodId);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + methodId));
        
        // If setting as default, unset other default methods
        if (Boolean.TRUE.equals(paymentMethodDto.getIsDefault()) && !paymentMethod.getIsDefault()) {
            unsetDefaultPaymentMethods(paymentMethod.getUserId());
        }
        
        paymentMethodMapper.updateEntityFromDTO(paymentMethodDto, paymentMethod);
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        
        log.info("Payment method updated successfully: {}", methodId);
        return paymentMethodMapper.toDTO(updatedPaymentMethod);
    }

    @Override
    public void deletePaymentMethod(Long methodId) {
        log.info("Deleting payment method ID: {}", methodId);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + methodId));
        
        // TODO: Check if payment method is being used in any pending payments
        
        paymentMethodRepository.delete(paymentMethod);
        log.info("Payment method deleted successfully: {}", methodId);
    }

    // ==================== User Payment Methods ====================
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> getPaymentMethodsByUserId(Integer userId) {
        log.info("Fetching payment methods for user ID: {}", userId);
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserId(userId);
        return paymentMethods.stream()
                .map(paymentMethodMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<PaymentMethodDto> getPaymentMethodsByUserId(Integer userId, Pageable pageable) {
        log.info("Fetching payment methods for user ID: {} with pagination: {}", userId, pageable);
        Page<PaymentMethod> paymentMethodPage = paymentMethodRepository.findByUserId(userId, pageable);
        List<PaymentMethodDto> paymentMethods = paymentMethodPage.getContent().stream()
                .map(paymentMethodMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.<PaymentMethodDto>builder()
                .data(paymentMethods)
                .totalElements(paymentMethodPage.getTotalElements())
                .totalPages(paymentMethodPage.getTotalPages())
                .currentPage(paymentMethodPage.getNumber())
                .pageSize(paymentMethodPage.getSize())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> getActivePaymentMethodsByUserId(Integer userId) {
        log.info("Fetching active payment methods for user ID: {}", userId);
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserIdAndIsActiveTrue(userId);
        return paymentMethods.stream()
                .map(paymentMethodMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentMethodDto getDefaultPaymentMethodByUserId(Integer userId) {
        log.info("Fetching default payment method for user ID: {}", userId);
        PaymentMethod paymentMethod = paymentMethodRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new RuntimeException("No default payment method found for user ID: " + userId));
        return paymentMethodMapper.toDTO(paymentMethod);
    }

    // ==================== Payment Method Types and Gateways ====================
    
    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> getPaymentMethodsByUserIdAndType(Integer userId, String type) {
        log.info("Fetching payment methods for user ID: {} and type: {}", userId, type);
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserIdAndType(userId, type);
        return paymentMethods.stream()
                .map(paymentMethodMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> getPaymentMethodsByUserIdAndGateway(Integer userId, PaymentGateway gateway) {
        log.info("Fetching payment methods for user ID: {} and gateway: {}", userId, gateway);
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUserIdAndGateway(userId, gateway);
        return paymentMethods.stream()
                .map(paymentMethodMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> getCardsByUserId(Integer userId) {
        log.info("Fetching cards for user ID: {}", userId);
        List<PaymentMethod> cards = paymentMethodRepository.findActiveCardsByUserId(userId);
        return cards.stream()
                .map(paymentMethodMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== Payment Method Status Management ====================
    
    @Override
    public PaymentMethodDto activatePaymentMethod(Long methodId) {
        log.info("Activating payment method ID: {}", methodId);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + methodId));
        
        paymentMethod.setIsActive(true);
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        
        log.info("Payment method activated: {}", methodId);
        return paymentMethodMapper.toDTO(updatedPaymentMethod);
    }

    @Override
    public PaymentMethodDto deactivatePaymentMethod(Long methodId) {
        log.info("Deactivating payment method ID: {}", methodId);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + methodId));
        
        paymentMethod.setIsActive(false);
        if (paymentMethod.getIsDefault()) {
            paymentMethod.setIsDefault(false);
        }
        
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        
        log.info("Payment method deactivated: {}", methodId);
        return paymentMethodMapper.toDTO(updatedPaymentMethod);
    }

    @Override
    public PaymentMethodDto setAsDefaultPaymentMethod(Long methodId) {
        log.info("Setting payment method as default: {}", methodId);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + methodId));
        
        // Unset other default methods for this user
        unsetDefaultPaymentMethods(paymentMethod.getUserId());
        
        paymentMethod.setIsDefault(true);
        paymentMethod.setIsActive(true); // Ensure it's active
        
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        
        log.info("Payment method set as default: {}", methodId);
        return paymentMethodMapper.toDTO(updatedPaymentMethod);
    }

    @Override
    public PaymentMethodDto verifyPaymentMethod(Long methodId) {
        log.info("Verifying payment method ID: {}", methodId);
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + methodId));
        
        // TODO: Implement actual verification logic with payment gateway
        
        paymentMethod.setIsVerified(true);
        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        
        log.info("Payment method verified: {}", methodId);
        return paymentMethodMapper.toDTO(updatedPaymentMethod);
    }

    // ==================== Payment Method Validation ====================
    
    @Override
    @Transactional(readOnly = true)
    public boolean isPaymentMethodValid(Long methodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElse(null);
        
        if (paymentMethod == null || !paymentMethod.getIsActive()) {
            return false;
        }
        
        return !paymentMethod.isExpired();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCardExpired(Long methodId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(methodId)
                .orElse(null);
        
        return paymentMethod != null && paymentMethod.isExpired();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> getExpiringCards(Integer userId, int months) {
        log.info("Fetching expiring cards for user ID: {} within {} months", userId, months);
        LocalDate futureDate = LocalDate.now().plusMonths(months);
        
        List<PaymentMethod> expiringCards = paymentMethodRepository.findExpiringCards(
                userId, futureDate.getYear(), futureDate.getMonthValue());
        
        return expiringCards.stream()
                .map(paymentMethodMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== Analytics ====================
    
    @Override
    @Transactional(readOnly = true)
    public long countPaymentMethodsByUserId(Integer userId) {
        return paymentMethodRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActivePaymentMethodsByUserId(Integer userId) {
        return paymentMethodRepository.countByUserIdAndIsActiveTrue(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPaymentMethodsByUserIdAndType(Integer userId, String type) {
        return paymentMethodRepository.countByUserIdAndType(userId, type);
    }

    // ==================== Helper Methods ====================
    
    private void unsetDefaultPaymentMethods(Integer userId) {
        List<PaymentMethod> defaultMethods = paymentMethodRepository.findByUserId(userId).stream()
                .filter(PaymentMethod::getIsDefault)
                .collect(Collectors.toList());
        
        for (PaymentMethod method : defaultMethods) {
            method.setIsDefault(false);
        }
        
        if (!defaultMethods.isEmpty()) {
            paymentMethodRepository.saveAll(defaultMethods);
        }
    }
}

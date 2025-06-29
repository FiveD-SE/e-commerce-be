package com.pm.paymentservice.repository;

import com.pm.paymentservice.model.PaymentGateway;
import com.pm.paymentservice.model.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    
    // User-based queries
    List<PaymentMethod> findByUserId(Integer userId);
    Page<PaymentMethod> findByUserId(Integer userId, Pageable pageable);
    List<PaymentMethod> findByUserIdAndIsActiveTrue(Integer userId);
    Optional<PaymentMethod> findByUserIdAndIsDefaultTrue(Integer userId);
    
    // Type and gateway queries
    List<PaymentMethod> findByUserIdAndType(Integer userId, String type);
    List<PaymentMethod> findByUserIdAndGateway(Integer userId, PaymentGateway gateway);
    List<PaymentMethod> findByUserIdAndTypeAndIsActiveTrue(Integer userId, String type);
    
    // Gateway method ID
    Optional<PaymentMethod> findByGatewayMethodId(String gatewayMethodId);
    Optional<PaymentMethod> findByUserIdAndGatewayMethodId(Integer userId, String gatewayMethodId);
    
    // Active methods
    List<PaymentMethod> findByIsActiveTrue();
    Page<PaymentMethod> findByIsActiveTrue(Pageable pageable);
    
    // Verified methods
    List<PaymentMethod> findByUserIdAndIsVerifiedTrue(Integer userId);
    
    // Card-specific queries
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.userId = :userId AND pm.type IN ('CREDIT_CARD', 'DEBIT_CARD') AND pm.isActive = true")
    List<PaymentMethod> findActiveCardsByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT pm FROM PaymentMethod pm WHERE pm.userId = :userId AND pm.cardExpYear = :year AND pm.cardExpMonth = :month")
    List<PaymentMethod> findExpiringCards(@Param("userId") Integer userId, @Param("year") Integer year, @Param("month") Integer month);
    
    // Count queries
    long countByUserId(Integer userId);
    long countByUserIdAndIsActiveTrue(Integer userId);
    long countByUserIdAndType(Integer userId, String type);
}

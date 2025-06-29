package com.pm.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "payment_methods")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class PaymentMethod extends AbstractMappedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id", unique = true, nullable = false, updatable = false)
    private Long methodId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", nullable = false, length = 50)
    private String type; // CREDIT_CARD, DEBIT_CARD, BANK_ACCOUNT, WALLET

    @Enumerated(EnumType.STRING)
    @Column(name = "gateway", nullable = false, length = 20)
    private PaymentGateway gateway;

    @Column(name = "gateway_method_id", length = 255)
    private String gatewayMethodId;

    // Card Information (encrypted)
    @Column(name = "card_last_four", length = 4)
    private String cardLastFour;

    @Column(name = "card_brand", length = 20)
    private String cardBrand; // VISA, MASTERCARD, AMEX, etc.

    @Column(name = "card_exp_month")
    private Integer cardExpMonth;

    @Column(name = "card_exp_year")
    private Integer cardExpYear;

    @Column(name = "card_holder_name", length = 255)
    private String cardHolderName;

    // Bank Account Information
    @Column(name = "bank_name", length = 255)
    private String bankName;

    @Column(name = "account_last_four", length = 4)
    private String accountLastFour;

    @Column(name = "account_type", length = 20)
    private String accountType; // CHECKING, SAVINGS

    // Wallet Information
    @Column(name = "wallet_email", length = 255)
    private String walletEmail;

    @Column(name = "wallet_phone", length = 20)
    private String walletPhone;

    // Status and Settings
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    // Additional Information
    @Column(name = "billing_address", columnDefinition = "JSON")
    private String billingAddress;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Helper methods
    public boolean isCard() {
        return "CREDIT_CARD".equals(type) || "DEBIT_CARD".equals(type);
    }

    public boolean isBankAccount() {
        return "BANK_ACCOUNT".equals(type);
    }

    public boolean isWallet() {
        return "WALLET".equals(type);
    }

    public boolean isExpired() {
        if (!isCard() || cardExpMonth == null || cardExpYear == null) {
            return false;
        }
        
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate expiry = java.time.LocalDate.of(cardExpYear, cardExpMonth, 1)
                .plusMonths(1).minusDays(1);
        
        return now.isAfter(expiry);
    }

    public String getMaskedNumber() {
        if (isCard() && cardLastFour != null) {
            return "**** **** **** " + cardLastFour;
        } else if (isBankAccount() && accountLastFour != null) {
            return "****" + accountLastFour;
        } else if (isWallet() && walletEmail != null) {
            String email = walletEmail;
            int atIndex = email.indexOf('@');
            if (atIndex > 2) {
                return email.substring(0, 2) + "***" + email.substring(atIndex);
            }
            return "***" + email.substring(atIndex);
        }
        return "****";
    }

    public String getDisplayName() {
        if (isCard()) {
            return cardBrand + " " + getMaskedNumber();
        } else if (isBankAccount()) {
            return bankName + " " + getMaskedNumber();
        } else if (isWallet()) {
            return gateway.name() + " " + getMaskedNumber();
        }
        return name;
    }
}

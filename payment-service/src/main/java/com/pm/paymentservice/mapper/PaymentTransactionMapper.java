package com.pm.paymentservice.mapper;

import com.pm.paymentservice.dto.PaymentTransactionDto;
import com.pm.paymentservice.model.PaymentTransaction;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper {

    PaymentTransactionDto toDTO(PaymentTransaction paymentTransaction);

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "transactionReference", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentTransaction toEntity(PaymentTransactionDto paymentTransactionDto);

    List<PaymentTransactionDto> toDTOList(List<PaymentTransaction> paymentTransactions);

    List<PaymentTransaction> toEntityList(List<PaymentTransactionDto> paymentTransactionDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "transactionReference", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(PaymentTransactionDto paymentTransactionDto, @MappingTarget PaymentTransaction paymentTransaction);
}

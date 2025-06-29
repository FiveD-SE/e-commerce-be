package com.pm.paymentservice.mapper;

import com.pm.paymentservice.dto.PaymentDto;
import com.pm.paymentservice.dto.PaymentSummaryDto;
import com.pm.paymentservice.model.Payment;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PaymentTransactionMapper.class})
public interface PaymentMapper {

    @Mapping(target = "transactions", source = "transactions")
    PaymentDto toDTO(Payment payment);

    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "webhooks", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "paymentReference", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(PaymentDto paymentDto);

    List<PaymentDto> toDTOList(List<Payment> payments);

    List<Payment> toEntityList(List<PaymentDto> paymentDtos);

    @Mapping(target = "refundedAmount", source = "refundedAmount")
    PaymentSummaryDto toSummaryDTO(Payment payment);

    List<PaymentSummaryDto> toSummaryDTOList(List<Payment> payments);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "paymentReference", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "webhooks", ignore = true)
    void updateEntityFromDTO(PaymentDto paymentDto, @MappingTarget Payment payment);
}

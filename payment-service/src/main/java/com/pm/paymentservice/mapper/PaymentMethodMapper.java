package com.pm.paymentservice.mapper;

import com.pm.paymentservice.dto.PaymentMethodDto;
import com.pm.paymentservice.model.PaymentMethod;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethodDto toDTO(PaymentMethod paymentMethod);

    @Mapping(target = "methodId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PaymentMethod toEntity(PaymentMethodDto paymentMethodDto);

    List<PaymentMethodDto> toDTOList(List<PaymentMethod> paymentMethods);

    List<PaymentMethod> toEntityList(List<PaymentMethodDto> paymentMethodDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "methodId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(PaymentMethodDto paymentMethodDto, @MappingTarget PaymentMethod paymentMethod);
}

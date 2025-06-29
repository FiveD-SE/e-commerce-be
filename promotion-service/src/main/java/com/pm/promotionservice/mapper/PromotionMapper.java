package com.pm.promotionservice.mapper;

import com.pm.promotionservice.dto.PromotionDto;
import com.pm.promotionservice.model.Promotion;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    PromotionDto toDTO(Promotion promotion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Promotion toEntity(PromotionDto promotionDto);

    List<PromotionDto> toDTOList(List<Promotion> promotions);

    List<Promotion> toEntityList(List<PromotionDto> promotionDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(PromotionDto promotionDto, @MappingTarget Promotion promotion);
}

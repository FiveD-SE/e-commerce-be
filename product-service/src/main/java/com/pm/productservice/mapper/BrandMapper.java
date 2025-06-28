package com.pm.productservice.mapper;

import com.pm.productservice.dto.BrandDto;
import com.pm.productservice.model.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    
    BrandDto toDTO(Brand brand);
    
    @Mapping(target = "id", ignore = true)
    Brand toEntity(BrandDto brandDto);
    
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(BrandDto brandDto, @MappingTarget Brand brand);
} 
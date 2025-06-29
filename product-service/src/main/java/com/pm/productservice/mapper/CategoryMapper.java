package com.pm.productservice.mapper;

import com.pm.productservice.dto.CategoryDto;
import com.pm.productservice.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "parentName", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "productCount", ignore = true)
    CategoryDto toDTO(Category category);
    
    @Mapping(target = "parentId", source = "parentId")
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDto categoryDto);
    
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CategoryDto categoryDto, @MappingTarget Category category);
    
    // Conversion methods
    default LocalDateTime map(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }
    
    default Instant map(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toInstant(ZoneOffset.UTC) : null;
    }
}
package com.pm.productservice.mapper;

import com.pm.productservice.dto.CategoryDto;
import com.pm.productservice.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parentId")
    CategoryDto toDTO(Category category);
    
    @Mapping(target = "parentId", source = "parentId")
    Category toEntity(CategoryDto categoryDto);
}
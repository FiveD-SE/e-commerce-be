package com.pm.productservice.mapper;

import com.pm.productservice.dto.CategoryDto;
import com.pm.productservice.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentCategoryId", source = "parentCategory.categoryId")
    CategoryDto toDTO(Category category);

    @Mapping(target = "parentCategory", ignore = true)
    Category toEntity(CategoryDto categoryDTO);
}

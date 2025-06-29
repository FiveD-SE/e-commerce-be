package com.pm.contentservice.mapper;

import com.pm.contentservice.dto.BlogDto;
import com.pm.contentservice.model.Blog;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlogMapper {

    BlogDto toDTO(Blog blog);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Blog toEntity(BlogDto blogDto);

    List<BlogDto> toDTOList(List<Blog> blogs);

    List<Blog> toEntityList(List<BlogDto> blogDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(BlogDto blogDto, @MappingTarget Blog blog);
}

package com.pm.contentservice.mapper;

import com.pm.contentservice.dto.BannerDto;
import com.pm.contentservice.model.Banner;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BannerMapper {

    BannerDto toDTO(Banner banner);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Banner toEntity(BannerDto bannerDto);

    List<BannerDto> toDTOList(List<Banner> banners);

    List<Banner> toEntityList(List<BannerDto> bannerDtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(BannerDto bannerDto, @MappingTarget Banner banner);
}

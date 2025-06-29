package com.pm.contentservice.service.impl;

import com.pm.contentservice.dto.BannerDto;
import com.pm.contentservice.dto.CreateBannerRequest;
import com.pm.contentservice.dto.response.collection.CollectionResponse;
import com.pm.contentservice.mapper.BannerMapper;
import com.pm.contentservice.model.Banner;
import com.pm.contentservice.repository.BannerRepository;
import com.pm.contentservice.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final BannerMapper bannerMapper;

    // ==================== Banner CRUD Operations ====================
    
    @Override
    public BannerDto createBanner(CreateBannerRequest request) {
        log.info("Creating banner with name: {}", request.getName());
        
        Banner banner = Banner.builder()
                .name(request.getName())
                .label(request.getLabel())
                .imageUrl(request.getImageUrl())
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .description(request.getDescription())
                .linkUrl(request.getLinkUrl())
                .linkText(request.getLinkText())
                .position(request.getPosition())
                .type(request.getType() != null ? request.getType() : "IMAGE")
                .target(request.getTarget() != null ? request.getTarget() : "_self")
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .priority(request.getPriority() != null ? request.getPriority() : 0)
                .width(request.getWidth())
                .height(request.getHeight())
                .altText(request.getAltText())
                .cssClass(request.getCssClass())
                .cssStyle(request.getCssStyle())
                .htmlContent(request.getHtmlContent())
                .mobileImageUrl(request.getMobileImageUrl())
                .tabletImageUrl(request.getTabletImageUrl())
                .createdBy(request.getCreatedBy())
                .build();
        
        Banner savedBanner = bannerRepository.save(banner);
        log.info("Banner created successfully with ID: {}", savedBanner.getId());
        
        return bannerMapper.toDTO(savedBanner);
    }

    @Override
    @Transactional(readOnly = true)
    public BannerDto getBannerById(Long id) {
        log.info("Fetching banner by ID: {}", id);
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with ID: " + id));
        return bannerMapper.toDTO(banner);
    }

    @Override
    public BannerDto updateBanner(Long id, BannerDto bannerDto) {
        log.info("Updating banner ID: {}", id);
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with ID: " + id));
        
        bannerMapper.updateEntityFromDTO(bannerDto, banner);
        Banner updatedBanner = bannerRepository.save(banner);
        
        log.info("Banner updated successfully: {}", id);
        return bannerMapper.toDTO(updatedBanner);
    }

    @Override
    public void deleteBanner(Long id) {
        log.info("Deleting banner ID: {}", id);
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with ID: " + id));
        
        bannerRepository.delete(banner);
        log.info("Banner deleted successfully: {}", id);
    }

    // ==================== Banner Listing and Filtering ====================
    
    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getAllBanners(Pageable pageable) {
        log.info("Fetching all banners with pagination: {}", pageable);
        Page<Banner> bannerPage = bannerRepository.findAll(pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getBannersByLabel(String label, Pageable pageable) {
        log.info("Fetching banners by label: {} with pagination: {}", label, pageable);
        Page<Banner> bannerPage = bannerRepository.findByLabel(label, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getBannersByPosition(String position, Pageable pageable) {
        log.info("Fetching banners by position: {} with pagination: {}", position, pageable);
        Page<Banner> bannerPage = bannerRepository.findByPosition(position, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getBannersByType(String type, Pageable pageable) {
        log.info("Fetching banners by type: {} with pagination: {}", type, pageable);
        Page<Banner> bannerPage = bannerRepository.findByType(type, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getBannersByCreatedBy(Integer createdBy, Pageable pageable) {
        log.info("Fetching banners by created by: {} with pagination: {}", createdBy, pageable);
        Page<Banner> bannerPage = bannerRepository.findByCreatedBy(createdBy, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    // ==================== Active Banners ====================
    
    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getActiveBanners(Pageable pageable) {
        log.info("Fetching active banners with pagination: {}", pageable);
        Instant now = Instant.now();
        Page<Banner> bannerPage = bannerRepository.findActiveBanners(now, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerDto> getActiveBannersByPosition(String position) {
        log.info("Fetching active banners by position: {}", position);
        Instant now = Instant.now();
        List<Banner> banners = bannerRepository.findActiveBannersByPosition(position, now);
        return banners.stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getActiveBannersByPosition(String position, Pageable pageable) {
        log.info("Fetching active banners by position: {} with pagination: {}", position, pageable);
        Instant now = Instant.now();
        Page<Banner> bannerPage = bannerRepository.findActiveBannersByPosition(position, now, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getFeaturedActiveBanners(Pageable pageable) {
        log.info("Fetching featured active banners with pagination: {}", pageable);
        Instant now = Instant.now();
        Page<Banner> bannerPage = bannerRepository.findFeaturedActiveBanners(now, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    // ==================== Scheduled and Expired Banners ====================
    
    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getScheduledBanners(Pageable pageable) {
        log.info("Fetching scheduled banners with pagination: {}", pageable);
        Instant now = Instant.now();
        Page<Banner> bannerPage = bannerRepository.findScheduledBanners(now, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getExpiredBanners(Pageable pageable) {
        log.info("Fetching expired banners with pagination: {}", pageable);
        Instant now = Instant.now();
        Page<Banner> bannerPage = bannerRepository.findExpiredBanners(now, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(), 
                bannerPage.getNumber(), bannerPage.getSize());
    }

    // ==================== Banner Search ====================
    
    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> searchBanners(String searchTerm, Pageable pageable) {
        log.info("Searching banners with term: {} and pagination: {}", searchTerm, pageable);
        Page<Banner> bannerPage = bannerRepository.searchBanners(searchTerm, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());
        
        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(),
                bannerPage.getNumber(), bannerPage.getSize());
    }

    // ==================== Banner Actions ====================

    @Override
    public BannerDto activateBanner(Long id) {
        log.info("Activating banner ID: {}", id);
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with ID: " + id));

        banner.setIsActive(true);

        Banner updatedBanner = bannerRepository.save(banner);
        log.info("Banner activated successfully: {}", id);

        return bannerMapper.toDTO(updatedBanner);
    }

    @Override
    public BannerDto deactivateBanner(Long id) {
        log.info("Deactivating banner ID: {}", id);
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with ID: " + id));

        banner.setIsActive(false);

        Banner updatedBanner = bannerRepository.save(banner);
        log.info("Banner deactivated successfully: {}", id);

        return bannerMapper.toDTO(updatedBanner);
    }

    @Override
    public BannerDto featureBanner(Long id) {
        log.info("Featuring banner ID: {}", id);
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with ID: " + id));

        banner.setIsFeatured(true);

        Banner updatedBanner = bannerRepository.save(banner);
        log.info("Banner featured successfully: {}", id);

        return bannerMapper.toDTO(updatedBanner);
    }

    @Override
    public BannerDto unfeatureBanner(Long id) {
        log.info("Unfeaturing banner ID: {}", id);
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Banner not found with ID: " + id));

        banner.setIsFeatured(false);

        Banner updatedBanner = bannerRepository.save(banner);
        log.info("Banner unfeatured successfully: {}", id);

        return bannerMapper.toDTO(updatedBanner);
    }

    // ==================== Banner Interactions ====================

    @Override
    public BannerDto incrementClickCount(Long id) {
        log.debug("Incrementing click count for banner ID: {}", id);
        bannerRepository.incrementClickCount(id);
        return getBannerById(id);
    }

    @Override
    public BannerDto incrementViewCount(Long id) {
        log.debug("Incrementing view count for banner ID: {}", id);
        bannerRepository.incrementViewCount(id);
        return getBannerById(id);
    }

    // ==================== Banner Analytics ====================

    @Override
    @Transactional(readOnly = true)
    public long countActiveBanners() {
        Instant now = Instant.now();
        return bannerRepository.countActiveBanners(now);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFeaturedBanners() {
        return bannerRepository.countByIsFeaturedTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long countBannersByPosition(String position) {
        return bannerRepository.countByPosition(position);
    }

    @Override
    @Transactional(readOnly = true)
    public long countBannersByType(String type) {
        return bannerRepository.countByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveBannersByPosition(String position) {
        Instant now = Instant.now();
        return bannerRepository.countActiveBannersByPosition(position, now);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalClicks() {
        return bannerRepository.getTotalClicks();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalViews() {
        return bannerRepository.getTotalViews();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageClicks() {
        return bannerRepository.getAverageClicks();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageViews() {
        return bannerRepository.getAverageViews();
    }

    // ==================== Banner Metadata ====================

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllPositions() {
        return bannerRepository.findAllPositions();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllTypes() {
        return bannerRepository.findAllTypes();
    }

    // ==================== Date Range Queries ====================

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getBannersByDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        log.info("Fetching banners between {} and {} with pagination: {}", startDate, endDate, pageable);
        Page<Banner> bannerPage = bannerRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(),
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getBannersByStartDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        log.info("Fetching banners with start date between {} and {} with pagination: {}", startDate, endDate, pageable);
        Page<Banner> bannerPage = bannerRepository.findByStartDateBetween(startDate, endDate, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(),
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getBannersByEndDateRange(Instant startDate, Instant endDate, Pageable pageable) {
        log.info("Fetching banners with end date between {} and {} with pagination: {}", startDate, endDate, pageable);
        Page<Banner> bannerPage = bannerRepository.findByEndDateBetween(startDate, endDate, pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(),
                bannerPage.getNumber(), bannerPage.getSize());
    }

    // ==================== Popular Banners ====================

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getPopularBanners(Pageable pageable) {
        log.info("Fetching popular banners with pagination: {}", pageable);
        Page<Banner> bannerPage = bannerRepository.findPopularBanners(pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(),
                bannerPage.getNumber(), bannerPage.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionResponse<BannerDto> getMostViewedBanners(Pageable pageable) {
        log.info("Fetching most viewed banners with pagination: {}", pageable);
        Page<Banner> bannerPage = bannerRepository.findMostViewedBanners(pageable);
        List<BannerDto> banners = bannerPage.getContent().stream()
                .map(bannerMapper::toDTO)
                .collect(Collectors.toList());

        return CollectionResponse.of(banners, bannerPage.getTotalElements(), bannerPage.getTotalPages(),
                bannerPage.getNumber(), bannerPage.getSize());
    }

    // ==================== Scheduled Tasks ====================

    @Override
    public void activateScheduledBanners() {
        log.info("Activating scheduled banners");
        Instant now = Instant.now();
        List<Banner> scheduledBanners = bannerRepository.findScheduledBanners(now);

        for (Banner banner : scheduledBanners) {
            if (banner.getStartDate() != null && !now.isBefore(banner.getStartDate())) {
                banner.setIsActive(true);
                bannerRepository.save(banner);
                log.info("Activated scheduled banner ID: {}", banner.getId());
            }
        }

        log.info("Activated {} scheduled banners", scheduledBanners.size());
    }

    @Override
    public void deactivateExpiredBanners() {
        log.info("Deactivating expired banners");
        Instant now = Instant.now();
        List<Banner> expiredBanners = bannerRepository.findExpiredBanners(now);

        for (Banner banner : expiredBanners) {
            banner.setIsActive(false);
            bannerRepository.save(banner);
            log.info("Deactivated expired banner ID: {}", banner.getId());
        }

        log.info("Deactivated {} expired banners", expiredBanners.size());
    }
}

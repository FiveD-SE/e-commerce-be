-- Sample brands data
INSERT INTO brands (id, name, description, logo_url, website_url, status, created_at, updated_at) VALUES
-- Technology Brands
('550e8400-e29b-41d4-a716-446655440001', 'TechPro', 'Leading technology products and electronics', 'https://example.com/logos/techpro.png', 'https://techpro.com', 'ACTIVE', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440002', 'SmartGadgets', 'Innovative smart home and wearable devices', 'https://example.com/logos/smartgadgets.png', 'https://smartgadgets.com', 'ACTIVE', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'GameZone', 'Gaming accessories and consoles', 'https://example.com/logos/gamezone.png', 'https://gamezone.com', 'ACTIVE', NOW(), NOW()),

-- Fashion Brands
('550e8400-e29b-41d4-a716-446655440004', 'StyleWear', 'Contemporary fashion and apparel', 'https://example.com/logos/stylewear.png', 'https://stylewear.com', 'ACTIVE', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440005', 'TrendyFit', 'Active wear and sports clothing', 'https://example.com/logos/trendyfit.png', 'https://trendyfit.com', 'ACTIVE', NOW(), NOW()),

-- Home & Garden Brands
('550e8400-e29b-41d4-a716-446655440006', 'HomeComfort', 'Home improvement and furniture', 'https://example.com/logos/homecomfort.png', 'https://homecomfort.com', 'ACTIVE', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440007', 'GardenPlus', 'Garden tools and outdoor equipment', 'https://example.com/logos/gardenplus.png', 'https://gardenplus.com', 'ACTIVE', NOW(), NOW()),

-- Beauty & Health Brands
('550e8400-e29b-41d4-a716-446655440008', 'BeautyGlow', 'Skincare and cosmetics', 'https://example.com/logos/beautyglow.png', 'https://beautyglow.com', 'ACTIVE', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440009', 'HealthVitals', 'Health supplements and wellness products', 'https://example.com/logos/healthvitals.png', 'https://healthvitals.com', 'ACTIVE', NOW(), NOW()),

-- Inactive/Discontinued brands for testing
('550e8400-e29b-41d4-a716-446655440010', 'OldTech', 'Discontinued technology brand', 'https://example.com/logos/oldtech.png', 'https://oldtech.com', 'DISCONTINUED', NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440011', 'TestBrand', 'Test brand for development', NULL, NULL, 'INACTIVE', NOW(), NOW()); 
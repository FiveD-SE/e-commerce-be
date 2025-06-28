-- Update existing products with brand IDs
UPDATE products SET brand_id = '550e8400-e29b-41d4-a716-446655440001' WHERE sku = 'TEL-001';  -- iPhone to TechPro
UPDATE products SET brand_id = '550e8400-e29b-41d4-a716-446655440002' WHERE sku = 'LAP-001';  -- MacBook to SmartGadgets
UPDATE products SET brand_id = '550e8400-e29b-41d4-a716-446655440001' WHERE sku = 'TV-001';   -- Samsung TV to TechPro
UPDATE products SET brand_id = '550e8400-e29b-41d4-a716-446655440004' WHERE sku = 'CLO-001';  -- T-Shirt to StyleWear
UPDATE products SET brand_id = '550e8400-e29b-41d4-a716-446655440005' WHERE sku = 'CLO-002';  -- Jeans to TrendyFit

-- Sample product reviews
INSERT INTO product_reviews (id, product_id, user_id, rating, title, comment, status, created_at, updated_at) VALUES
-- Reviews for iPhone (assuming first product)
('650e8400-e29b-41d4-a716-446655440001', 
 (SELECT id FROM products WHERE sku = 'TEL-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440001', 
 5, 'Excellent Phone!', 'Amazing features and great camera quality. Highly recommended!', 'ACTIVE', NOW(), NOW()),

('650e8400-e29b-41d4-a716-446655440002', 
 (SELECT id FROM products WHERE sku = 'TEL-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440002', 
 4, 'Very Good', 'Good performance but a bit expensive. Overall satisfied with the purchase.', 'ACTIVE', NOW(), NOW()),

('650e8400-e29b-41d4-a716-446655440003', 
 (SELECT id FROM products WHERE sku = 'TEL-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440003', 
 5, 'Perfect!', 'Everything works perfectly. Fast delivery and excellent packaging.', 'ACTIVE', NOW(), NOW()),

-- Reviews for MacBook
('650e8400-e29b-41d4-a716-446655440004', 
 (SELECT id FROM products WHERE sku = 'LAP-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440004', 
 5, 'Outstanding Laptop', 'Perfect for work and gaming. The M1 chip is incredibly fast!', 'ACTIVE', NOW(), NOW()),

('650e8400-e29b-41d4-a716-446655440005', 
 (SELECT id FROM products WHERE sku = 'LAP-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440005', 
 4, 'Great Performance', 'Excellent build quality and performance. Battery life is amazing.', 'ACTIVE', NOW(), NOW()),

-- Reviews for Samsung TV
('650e8400-e29b-41d4-a716-446655440006', 
 (SELECT id FROM products WHERE sku = 'TV-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440006', 
 5, 'Best TV Ever', 'Crystal clear picture quality. Smart features work flawlessly.', 'ACTIVE', NOW(), NOW()),

('650e8400-e29b-41d4-a716-446655440007', 
 (SELECT id FROM products WHERE sku = 'TV-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440007', 
 3, 'Good but Issues', 'Good picture quality but had some connectivity issues initially.', 'ACTIVE', NOW(), NOW()),

-- Reviews for T-Shirt
('650e8400-e29b-41d4-a716-446655440008', 
 (SELECT id FROM products WHERE sku = 'CLO-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440008', 
 4, 'Comfortable Fit', 'Good quality fabric and comfortable to wear. True to size.', 'ACTIVE', NOW(), NOW()),

('650e8400-e29b-41d4-a716-446655440009', 
 (SELECT id FROM products WHERE sku = 'CLO-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440009', 
 5, 'Love It!', 'Perfect fit and great quality. Will buy more colors!', 'ACTIVE', NOW(), NOW()),

-- Reviews for Jeans
('650e8400-e29b-41d4-a716-446655440010', 
 (SELECT id FROM products WHERE sku = 'CLO-002' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440010', 
 4, 'Stylish Jeans', 'Good fit and style. Durable material and comfortable.', 'ACTIVE', NOW(), NOW()),

-- Some pending/rejected reviews for testing
('650e8400-e29b-41d4-a716-446655440011', 
 (SELECT id FROM products WHERE sku = 'TEL-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440011', 
 2, 'Not Happy', 'Had issues with the product. Requesting support.', 'PENDING', NOW(), NOW()),

('650e8400-e29b-41d4-a716-446655440012', 
 (SELECT id FROM products WHERE sku = 'LAP-001' LIMIT 1), 
 '750e8400-e29b-41d4-a716-446655440012', 
 1, 'Inappropriate Review', 'This contains inappropriate content and should be rejected.', 'REJECTED', NOW(), NOW()); 
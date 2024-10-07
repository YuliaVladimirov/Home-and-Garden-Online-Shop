-- liquibase formatted sql

-- changeset yulia:insert_products
INSERT INTO Products (CategoryID, Price, DiscountPrice,CreatedAt, UpdatedAt, Name, Description, ImageURL)
VALUES
    (1,4.99, 2.99, NOW(), NOW(),'Alfalfa Meal','Plant-based fertilizer','https://example.com/images/alfalfa_meal.jpg'),
    (1,23.99, 19.99, NOW(), NOW(),'Bat Guano','The most common source of guano','https://example.com/images/bat_guano.jpg'),
    (1, 14.99, NULL, NOW(), NOW(),'Fish Emulsion','A great additive when nitrogen levels are low and leafy plants need a good boost','https://example.com/images/fish_emulsion.jpg'),
    (1, 10.99, 6.99, NOW(), NOW(),'Cotton Seed Meal','A common plant fertilizer in areas where cotton is grown','https://example.com/images/cotton_seed_meal.jpg'),
    (1,29.99, NULL, NOW(), NOW(),'Corn Gluten Meal','A common late-season, winter preparation additive, corn gluten is a good soil stabilizer for winter','https://example.com/images/corn_gluten_meal.jpg'),
    (2, 13.38, 10.54, NOW(), NOW(),'Protective Gloves','Protective Large Garden Gloves with Strong Grip','https://example.com/images/protrective_gloves.jpg'),
    (2, 9.99, 8.50, NOW(), NOW(),'Knee Support','Knee Support Padding for gardening and cleaning','https://example.com/images/knee_support.jpg'),
    (2, 7.35, 4.58, NOW(), NOW(),'Anti Bird Net','Reusable anti bird net for garden and fence','https://example.com/images/anti_bird_net.jpg'),
    (2, 1000.00, 850.00, NOW(), NOW(),'Klargester Alpha Septic Tank','2800 Litre septic tank suitable for all domestic applications,','https://example.com/images/klargester_alpha_septic-tank.jpg'),
    (2, 1535.00, NULL, NOW(), NOW(),'Clearwater Shallow Dig Septic Tank','A high capacity tank, perfect for off-mains domestic properties','https://example.com/images/clearwater_shallow_dig_septic_tank.jpg'),
    (3, 11.99, 9.50, NOW(), NOW(),'Magic Garden Seeds','Classic gardening seed-set','https://example.com/images/magic_garden_seeds.jpg'),
    (3, 7.95, 5.99, NOW(), NOW(),'Sedum Mix','Sedum mix contains more than 20 seed varieties','https://example.com/images/sedum_mix.jpg'),
    (3, 5.99, NULL, NOW(), NOW(),'Marshalls Garden Sweet Pepper Big Ben seedling','Sweet Pepper seedling','https://example.com/images/marshalls_garden_sweet_pepper_seedling.jpg'),
    (3, 4.99, 2.99, NOW(), NOW(),'Cordon Tomato Sungold seedling','Tomato seedling','https://example.com/images/cordon_tomato_seedling.jpg'),
    (3, 2.95, 2.50, NOW(), NOW(),'Tulip Blue Beauty','Tulip bulbs for planting','https://example.com/images/tulip_blue_beauty.jpg'),
    (4,25.99 ,19.99 , NOW(), NOW(),'Leaf Rakes','A tool to rake up leaves','https://example.com/images/leaf_rakes.jpg'),
    (4, 8.99, 5.99, NOW(), NOW(),'Weeder','A weeding tool for loosening and pulling up roots!','https://example.com/images/weeder.jpg'),
    (4, 19.99, 15.99, NOW(), NOW(), 'Garden Trowel', 'Sturdy garden trowel with wooden handle', 'https://example.com/images/garden_trowel.jpg'),
    (4, 39.99, 29.99, NOW(), NOW(), 'Pruning Shears', 'Heavy-duty pruning shears for trimming bushes and small branches', 'https://example.com/images/pruning_shears.jpg'),
    (4, 349.99, 299.99, NOW(), NOW(), 'Gas Lawn Mower', 'Gas-powered lawn mower with 21-inch cutting deck', 'https://example.com/images/lawn_mower.jpg'),
    (5, 15.99, 12.50, NOW(), NOW(),'Deroma White Garden Pot','Ceramic pot 28 cm diameter ','https://example.com/images/deroma_white_garden_pot.jpg'),
    (5, 8.22, 7.33, NOW(), NOW(),'Iron Planters Flower Pot','Metal Bucket for Balcony 15 cm diameter','https://example.com/images/iron_planters_flower_pot.jpg'),
    (5, 25.60, 20.75, NOW(), NOW(),'Potato Planter','Wooden Potato Planter Box 60 cm x 61 cm','https://example.com/images/poptato_planter.jpg'),
    (5, 150.00, 137.00, NOW(), NOW(),'Forest Toulouse Garden Trellis Planter','A planter with integral trellis to support climbing plants 31cm x 183cm x 50cm','https://example.com/images/forest_toulouse_garden_trellis_planter.jpg'),
    (5, 38.00, NULL, NOW(), NOW(),'Garden Italia Pots Round Vase','VASE in classic "never out of fashion" style','https://example.com/images/garden_italia_pots_round_vase.jpg');

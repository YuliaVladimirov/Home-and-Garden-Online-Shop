-- liquibase formatted sql

-- changeset yulia:create_table_users
CREATE TABLE Users (UserID BIGINT AUTO_INCREMENT NOT NULL, Name VARCHAR(50) NULL, Email VARCHAR(100) NULL UNIQUE, PhoneNumber VARCHAR(20) NULL, PasswordHash VARCHAR(255) NULL, Role ENUM('CLIENT', 'ADMINISTRATOR') NULL, RefreshToken VARCHAR(255) NULL, CONSTRAINT PK_USERS PRIMARY KEY (UserID));

-- changeset yulia:create_table_products
CREATE TABLE Products (ProductID BIGINT AUTO_INCREMENT NOT NULL, CategoryID BIGINT NULL, Price DECIMAL (6, 2) NULL, DiscountPrice DECIMAL (6, 2) NULL, CreatedAt datetime NULL, UpdatedAt datetime NULL, Name VARCHAR(50) NULL, Description VARCHAR(255) NULL, ImageURL VARCHAR(255) NULL, CONSTRAINT PK_PRODUCTS PRIMARY KEY (ProductID));

-- changeset yulia:create_table_categories
CREATE TABLE Categories (CategoryID BIGINT AUTO_INCREMENT NOT NULL, Name VARCHAR(50) NULL, CONSTRAINT PK_CATEGORIES PRIMARY KEY (CategoryID));

-- changeset yulia:create_table_cart
CREATE TABLE Cart (CartID BIGINT AUTO_INCREMENT NOT NULL,
UserID BIGINT NULL, CONSTRAINT PK_CART PRIMARY KEY (CartID), UNIQUE (UserID));

-- changeset yulia:create_table_cartitems
CREATE TABLE CartItems (CartItemID BIGINT AUTO_INCREMENT NOT NULL, CartID BIGINT  NULL,
ProductID BIGINT  NULL, Quantity INT NULL, CONSTRAINT PK_CARTITEMS PRIMARY KEY (CartItemID));

-- changeset yulia:create_table_favorites
CREATE TABLE Favorites (FavoriteID BIGINT AUTO_INCREMENT NOT NULL, ProductID BIGINT  NULL, UserID BIGINT  NULL, CONSTRAINT PK_FAVORITES PRIMARY KEY (FavoriteID));

-- changeset yulia:create_table_orders
CREATE TABLE Orders (OrderID BIGINT AUTO_INCREMENT NOT NULL, UserID BIGINT NULL, CreatedAt datetime NULL, DeliveryAddress VARCHAR(255) NULL, ContactPhone VARCHAR(15) NULL, DeliveryMethod ENUM('COURIER_DELIVERY','CUSTOMER_PICKUP') NULL, Status ENUM('CREATED','PENDING_PAYMENT', 'PAID', 'ON_THE_WAY', 'DELIVERED', 'CANCELED') NULL, UpdatedAt datetime NULL, CONSTRAINT PK_ORDERS PRIMARY KEY (OrderID));

-- changeset yulia:create_table_orderitems
CREATE TABLE OrderItems (OrderItemID BIGINT AUTO_INCREMENT NOT NULL, OrderID BIGINT NULL, ProductID BIGINT NULL, Quantity INT NULL, PriceAtPurchase DECIMAL (10, 2) NULL, CONSTRAINT PK_ORDERITEMS PRIMARY KEY (OrderItemID));



-- changeset yulia:create_foreign_key_products_categories
ALTER TABLE Products ADD CONSTRAINT foreign_key_products_categories FOREIGN KEY (CategoryID) REFERENCES Categories (CategoryID) ON UPDATE CASCADE ON DELETE SET NULL;

-- changeset yulia:create_foreign_key_favorites_users
ALTER TABLE Favorites ADD CONSTRAINT foreign_key_favorites_users FOREIGN KEY (UserID) REFERENCES Users (UserID) ON UPDATE CASCADE ON DELETE SET NULL;

-- changeset yulia:create_foreign_key_favorites_products
ALTER TABLE Favorites ADD CONSTRAINT foreign_key_favorites_products FOREIGN KEY (ProductID) REFERENCES Products (ProductID) ON UPDATE CASCADE ON DELETE SET NULL;

-- changeset yulia:create_foreign_key_cart_users
ALTER TABLE Cart ADD CONSTRAINT foreign_key_cart_users FOREIGN KEY (UserID) REFERENCES Users (UserID) ON UPDATE CASCADE ON DELETE SET NULL;

-- changeset yulia:create_foreign_key_cartitems_cart
ALTER TABLE CartItems ADD CONSTRAINT foreign_key_cartitems_cart FOREIGN KEY (CartID) REFERENCES Cart (CartID) ON UPDATE CASCADE ON DELETE SET NULL;

-- changeset yulia:create_foreign_key_cartitems_products
ALTER TABLE CartItems ADD CONSTRAINT foreign_key_cartitems_products FOREIGN KEY (ProductID) REFERENCES Products (ProductID) ON UPDATE CASCADE ON DELETE SET NULL;

-- changeset yulia:create_foreign_key_orders_users
ALTER TABLE Orders ADD CONSTRAINT foreign_key_orders_users FOREIGN KEY (UserID) REFERENCES Users (UserID) ON UPDATE CASCADE ON DELETE SET NULL;

-- changeset yulia:create_foreign_key_orderitems_orders
ALTER TABLE OrderItems ADD CONSTRAINT foreign_key_orderitems_orders FOREIGN KEY (OrderID) REFERENCES Orders (OrderID) ON UPDATE CASCADE ON DELETE SET NULL;

-- changeset yulia:create_foreign_key_orderitems_products
ALTER TABLE OrderItems ADD CONSTRAINT foreign_key_orderitems_products FOREIGN KEY (ProductID) REFERENCES Products (ProductID) ON UPDATE CASCADE ON DELETE SET NULL;




-- changeset yulia:create_index_products_to_categories
CREATE INDEX foreign_key_products_categories ON Products(CategoryID);

-- changeset yulia:create_index_favorites_to_users
CREATE INDEX foreign_key_favorites_users ON Favorites(UserID);

-- changeset yulia:create_index_favorites_to_products
CREATE INDEX foreign_key_favorites_products ON Favorites(ProductID);

-- changeset yulia:create_index_cart
CREATE INDEX foreign_key_cart_users ON Cart(UserID);

-- changeset yulia:create_index_cartitems_to_cart
CREATE INDEX foreign_key_cartitems_cart ON CartItems(CartID);

-- changeset yulia:create_index_cartitems_to_products
CREATE INDEX foreign_key_cartitems_products ON CartItems(ProductID);

-- changeset yulia:create_index_orders
CREATE INDEX foreign_key_orders_users ON Orders(UserID);

-- changeset yulia:create_index_orderitems_to_orders
CREATE INDEX foreign_key_orderitems_orders ON OrderItems(OrderID);

-- changeset yulia:create_index_orderitems_to_products
CREATE INDEX foreign_key_orderitems_products ON OrderItems(ProductID);










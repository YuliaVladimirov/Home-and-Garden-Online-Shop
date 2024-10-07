#### This is the Entity Relationship diagram for the application's database schema:


```mermaid
erDiagram
%%{init: {
  "theme": "default",
  "themeCSS": [
    ".er.relationshipLabel { fill: black; }",
    ".er.relationshipLabelBox { fill: white; }",
    ".er.entityBox { fill: lightgray}",
    "[id^=entity-Order] .er.entityBox { fill: orange;} ",
    "[id^=entity-Cart] .er.entityBox { fill: lightgreen;} ",
    "[id^=entity-Products] .er.entityBox { fill: powderblue;} ",
    "[id^=entity-Categories] .er.entityBox { fill: powderblue;} ",
    "[id^=entity-Favorites] .er.entityBox { fill: powderblue;} "

    ]
}}%%

    Cart {
        CartID INT PK
        UserID INT FK
    }
    CartItems {
        CartItemID INT PK
        CartID INT FK
        ProductID INT FK
        Quantity INT
    }
    Users {
        UserID INT PK
        Name VARCHAR(64)
        Email VARCHAR(100) UK
        PhoneNumber VARCHAR(30)
        PasswordHash VARCHAR(256)
        RefreshTokent VARCHAR(256) "NULL"
        Role ENUM "CLIENT, ADMINISTRATOR"
    }
    Favorites {
        FavoriteID INT PK
        UserID INT FK
        ProductID INT FK
    }
    Products {
        ProductID INT PK
        Name VARCHAR
        Description VARCHAR
        Price DECIMAL
        CategoryID INT FK
        ImageURL VARCHAR
        DiscountPrice DECIMAL "NULL"
        CreatedAt TIMESTAMP
        UpdatedAt TIMESTAMP
    }
    Categories {
        CategoryID INT PK
        Name VARCHAR
    }
    Orders {
        OrderID INT PK
        UserID INT FK
        CreatedAt TIMESTAMP
        DeliveryAddress VARCHAR
        ContactPhone VARCHAR
        DeliveryMethod VARCHAR
        Status ENUM "PAID, SHIPPED, CLOSED"
        UpdatedAt TIMESTAMP
    }
    OrderItems {
        OrderItemID INT PK
        OrderID INT FK
        ProductID INT FK
        Quantity INT
        PriceAtPurchase DECIMAL
    }

    Cart ||--|| Users : "1 → 1"
    Cart ||--o{ CartItems : "1 → many"
    CartItems }o--|| Products : "many → 1"
    Products }o--|| Categories : "many → 1"
    Products ||--o{ Favorites : "1 → many"
    Users ||--o{ Favorites : "1 → many"
    Users ||--o{ Orders : "1 → many"
    Orders ||--o{ OrderItems : "1 → many"
    Products ||--o{ OrderItems : "1 → many"

```

package org.example.homeandgarden.repository;

import org.example.homeandgarden.entity.Product;
import org.example.homeandgarden.entity.query.ProductCountInterface;
import org.example.homeandgarden.entity.query.ProductPendingInterface;
import org.example.homeandgarden.entity.query.ProductProfitInterface;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {


    @Modifying(clearAutomatically=true, flushAutomatically=true)
    @Query("DELETE FROM Product product " +
            "WHERE product.productId = :id")
    void deleteById(Long id);


    @Query("SELECT product FROM Product product " +
            "WHERE product.discountPrice IS NOT NULL " +
            "AND (SELECT MAX(product.price / product.discountPrice) " +
            "FROM Product product) = (product.price / product.discountPrice)")
    List<Product> getMaxDiscountProduct();





    @Query(value =
            "SELECT  p.ProductID as productId, p.Name as name, o.Status as status, SUM(Quantity) as count, SUM(Quantity*Price) as sum " +
                    "FROM Products p JOIN OrderItems oi ON p.ProductID = oi.ProductID " +
                    "JOIN Orders o ON oi.OrderId = o.OrderID " +
                    "WHERE   "+
                    "(:status ='PAID' AND o.Status IN ('PAID','ON_THE_WAY','DELIVERED') ) OR " +
                    "(:status = 'CANCELED' AND o.Status = 'CANCELED' )" +
                    "GROUP BY p.ProductID, p.Name, o.Status " +
                    "ORDER BY Count DESC  " +
                    "LIMIT 10"
            , nativeQuery = true
    )
    List<ProductCountInterface> findTop10Products(@Param("status") String status);


    @Query("SELECT product from Product product " +
            "WHERE (:hasCategory = FALSE OR product.category.categoryId = :category) " +
            "AND product.price BETWEEN :minPrice and :maxPrice " +
            "AND (:hasDiscount = FALSE OR product.discountPrice IS NOT NULL)")
    List<Product> findProductsByFilter(Boolean hasCategory, Long category, BigDecimal minPrice, BigDecimal maxPrice, Boolean hasDiscount, Sort sortObject);




@Query (value =
           "SELECT  p.ProductID as productId, p.Name as name, SUM(oi.Quantity) as count, o.Status "+
           "FROM Products p JOIN OrderItems oi ON p.ProductID = oi.ProductID " +
           "JOIN Orders o ON oi.OrderId = o.OrderID " +
           "where o.Status = 'PENDING_PAYMENT' and o.CreatedAt < Now() - INTERVAL :day DAY  " +
           "GROUP BY  p.ProductID "+
           "Order by p.ProductID ", nativeQuery = true)
    List<ProductPendingInterface> findProductPending(Integer day);



    @Query(value =
            "SELECT CASE " +
                    "WHEN :period = 'MONTH' THEN DATE_FORMAT(o.CreatedAt, '%Y-%m') " +
                    "WHEN :period = 'WEEK' THEN DATE_FORMAT(o.CreatedAt, '%Y-%u') " +
                    "ELSE DATE_FORMAT(o.CreatedAt, '%Y-%m-%d') " +
            "END as period, SUM(oi.Quantity * p.Price) as sum " +
            "FROM Products p " +
            "JOIN OrderItems oi ON p.ProductID = oi.ProductID " +
            "JOIN Orders o ON oi.OrderId = o.OrderID " +
            "WHERE o.Status IN ('PAID','ON_THE_WAY','DELIVERED') AND o.CreatedAt >= " +
            "CASE " +
                    "WHEN :period = 'MONTH' THEN NOW() - INTERVAL :value MONTH " +
                    "WHEN :period = 'WEEK' THEN NOW() - INTERVAL :value WEEK " +
                    "ELSE NOW() - INTERVAL :value DAY " +
            "END " +
            "GROUP BY CASE " +
                    "WHEN :period = 'MONTH' THEN DATE_FORMAT(o.CreatedAt, '%Y-%m') " +
                    "WHEN :period = 'WEEK' THEN DATE_FORMAT(o.CreatedAt, '%Y-%u') " +
                    "ELSE DATE_FORMAT(o.CreatedAt, '%Y-%m-%d') " +
            "END ",
            nativeQuery = true
    )
    List<ProductProfitInterface> findProfitByPeriod(String period, Integer value);

}
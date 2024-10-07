package org.example.homeandgarden.entity.query;

import java.math.BigDecimal;

public interface ProductCountInterface {
    Long getProductId();
    String getName();
    String getStatus();
    Integer getCount();
    BigDecimal getSum();
}

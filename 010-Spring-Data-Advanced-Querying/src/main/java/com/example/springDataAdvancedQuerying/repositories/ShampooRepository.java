package com.example.springDataAdvancedQuerying.repositories;

import com.example.springDataAdvancedQuerying.entities.Shampoo;
import com.example.springDataAdvancedQuerying.entities.Size;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface ShampooRepository extends BaseRepository<Shampoo> {
    List<Shampoo> findAllBySizeOrderById(Size size);

    List<Shampoo> findAllBySizeOrLabelIdOrderByPrice(Size size, Long labelIDd);

    List<Shampoo> findAllByPriceGreaterThanOrderByPriceDesc(BigDecimal price);

    long countByPriceLessThan(BigDecimal price);

    @Query("SELECT DISTINCT s FROM Shampoo s JOIN s.ingredients i WHERE i.name IN :names ORDER BY s.id")
    List<Shampoo> findAllByIngredientNames(@Param("names") Collection<String> names);

    @Query("SELECT s FROM Shampoo s WHERE SIZE(s.ingredients) < :count ORDER BY s.id")
    List<Shampoo> findAllWithIngredientCountLessThan(@Param("count") int count);
}

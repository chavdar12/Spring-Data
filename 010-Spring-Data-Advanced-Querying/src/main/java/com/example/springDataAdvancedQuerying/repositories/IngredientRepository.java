package com.example.springDataAdvancedQuerying.repositories;

import com.example.springDataAdvancedQuerying.entities.Ingredient;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface IngredientRepository extends BaseRepository<Ingredient> {
    List<Ingredient> findAllByNameStartingWithOrderById(String prefix);

    List<Ingredient> findAllByNameInOrderByPrice(Collection<String> names);

    // the join table has no ON DELETE CASCADE, so links go first
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM shampoos_ingredients WHERE ingredient_id IN (SELECT id FROM ingredients WHERE name = :name)", nativeQuery = true)
    int deleteShampooLinksByName(@Param("name") String name);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Ingredient i WHERE i.name = :name")
    int deleteByName(@Param("name") String name);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Ingredient i SET i.price = i.price * :factor")
    int multiplyAllPrices(@Param("factor") BigDecimal factor);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Ingredient i SET i.price = i.price * :factor WHERE i.name IN :names")
    int multiplyPricesByNames(@Param("factor") BigDecimal factor, @Param("names") Collection<String> names);
}

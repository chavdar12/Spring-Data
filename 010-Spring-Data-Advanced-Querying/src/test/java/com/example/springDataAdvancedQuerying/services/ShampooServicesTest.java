package com.example.springDataAdvancedQuerying.services;

import com.example.springDataAdvancedQuerying.entities.Label;
import com.example.springDataAdvancedQuerying.entities.Shampoo;
import com.example.springDataAdvancedQuerying.entities.Size;
import com.example.springDataAdvancedQuerying.repositories.LabelRepository;
import com.example.springDataAdvancedQuerying.repositories.ShampooRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ShampooServicesImpl.class)
class ShampooServicesTest {

    @Autowired
    private ShampooServices shampooServices;
    @Autowired
    private ShampooRepository shampooRepository;
    @Autowired
    private LabelRepository labelRepository;

    private Label fresh;

    private void shampoo(String brand, Size size, String price, Label label) {
        Shampoo shampoo = new Shampoo();
        shampoo.setBrand(brand);
        shampoo.setSize(size);
        shampoo.setPrice(new BigDecimal(price));
        shampoo.setLabel(label);
        shampoo.setIngredients(Set.of());
        shampooRepository.save(shampoo);
    }

    @BeforeEach
    void setUp() {
        fresh = new Label();
        fresh.setTitle("Fresh");
        labelRepository.save(fresh);

        Label other = new Label();
        other.setTitle("Other");
        labelRepository.save(other);

        shampoo("Nettle", Size.MEDIUM, "19.50", other);
        shampoo("Apple", Size.SMALL, "5.00", fresh);
        shampoo("Aloe", Size.MEDIUM, "8.20", other);
        shampoo("Big", Size.LARGE, "3.10", other);
    }

    @Test
    void findsBySizeOrderedById() {
        assertThat(shampooServices.findAllBySizeOrderById(Size.MEDIUM)).extracting(Shampoo::getBrand)
                .containsExactly("Nettle", "Aloe");
    }

    @Test
    void findsBySizeOrLabelOrderedByPrice() {
        assertThat(shampooServices.findAllBySizeOrLabelIdOrderByPrice(Size.MEDIUM, fresh)).extracting(Shampoo::getBrand)
                .containsExactly("Apple", "Aloe", "Nettle");
    }

    @Test
    void toStringShowsBrandSizeAndPrice() {
        assertThat(shampooServices.findAllBySizeOrderById(Size.LARGE)).singleElement().hasToString(
                String.format("Big LARGE %.2flv.", 3.10));
    }
}

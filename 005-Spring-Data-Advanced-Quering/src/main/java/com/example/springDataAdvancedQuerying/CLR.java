package com.example.springDataAdvancedQuerying;

import com.example.springDataAdvancedQuerying.entities.Size;
import com.example.springDataAdvancedQuerying.services.ShampooServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class CLR implements CommandLineRunner {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private final ShampooServices shampooServices;

    public CLR(ShampooServices shampooServices) {
        this.shampooServices = shampooServices;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Task 1");

        Size size = Size.valueOf(reader.readLine());

        this.shampooServices.findAllBySizeOrderById(size).forEach(System.out::println);

    }
}

package com.example.schedule.repository;


import com.example.schedule.dto.Coffee;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CoffeeRepository {

    private Map<String, Coffee> coffeeMap = new HashMap<>();

    @PostConstruct
    public void init() {

        Coffee latte = Coffee.builder().name("latte").price(1100).build();
        Coffee mocha = Coffee.builder().name("mocha").price(1300).build();
        Coffee americano = Coffee.builder().name("americano").price(800).build();

        coffeeMap.put(latte.getName(), latte);
        coffeeMap.put(mocha.getName(), mocha);
        coffeeMap.put(americano.getName(), americano);

    }

    public Integer getPriceByName(String name) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return coffeeMap.get(name).getPrice();
    }
}

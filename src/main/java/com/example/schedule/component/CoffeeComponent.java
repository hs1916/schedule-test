package com.example.schedule.component;

import com.example.schedule.repository.CoffeeRepository;
import com.example.schedule.repository.CoffeeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@Slf4j
@RequiredArgsConstructor
public class CoffeeComponent implements CoffeeUseCase {

    private final CoffeeRepository coffeeRepository;
    Executor executor = Executors.newFixedThreadPool(10);

    @Override
    public int getPrice(String name) {
        Integer getPrice = coffeeRepository.getPriceByName(name);
        log.info(" 동기 호출 방식으로 가격 조회 시작 {} milli sec pending", 1000);
        return getPrice;
    }

    @Override
    public CompletableFuture<Integer> getPriceAsync(String name) {
        log.info(" 비동기 호출 방식으로 가격 조회 시작 ");

        CompletableFuture<Integer> future = new CompletableFuture<>();

//        new Thread( () -> {
//            log.info(" 새로운 스레드로 작업 시작" );
//            Integer getPrice = coffeeRepository.getPriceByName(name);
//            future.complete(getPrice);
//        }).start();

//        return future;
        return CompletableFuture.supplyAsync(() -> {
            log.info("supply async");
            return coffeeRepository.getPriceByName(name);
        }, executor);
    }

    @Override
    public Future<Integer> getDiscountPriceAsync(String name) {
        return null;
    }
}

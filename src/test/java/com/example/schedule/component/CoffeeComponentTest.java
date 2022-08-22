package com.example.schedule.component;

import com.example.schedule.repository.CoffeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

//
////@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {
//        CoffeeComponent.class,
//        CoffeeRepository.class,
//        LoggerFactory.class
//})
//
//@SpringBootTest(classes = {
//        CoffeeComponent.class,
//        CoffeeRepository.class
//})
@Slf4j
//@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        CoffeeComponent.class,
        CoffeeRepository.class
})
class CoffeeComponentTest {

    @Autowired
    CoffeeComponent coffeeComponent;

    CoffeeComponent component2;

    @Test
    public void 가격_조회_동기_블로킹_호출_테스트() throws Exception {
        int expectedPrice = 1100;

        log.debug(" 가격_조회_동기_블로킹_호출_테스트 R");
        int lattePrice = 0;
        for (int i = 0; i < 3; i++) {
            lattePrice = coffeeComponent.getPrice("latte");
        }

        assertThat(expectedPrice).isEqualTo(lattePrice);
    }

    @Test
    public void 가격_조회_비동기_블로킹_호출_테스트() throws Exception {

        int expectedPrice = 1100;

        CompletableFuture<Integer> future1 = coffeeComponent.getPriceAsync("latte");
        CompletableFuture<Integer> future2 = coffeeComponent.getPriceAsync("latte");
        CompletableFuture<Integer> future3 = coffeeComponent.getPriceAsync("latte");

        Integer getPrice1 = future1.join();
        log.info(" 최종 가격 전달 받음 1 {}", getPrice1);

        Integer getPrice2 = future2.join();
        log.info(" 최종 가격 전달 받음 2 {}", getPrice2);

        Integer getPrice3 = future3.join();
        log.info(" 최종 가격 전달 받음 3 {}", getPrice3);

        assertThat(expectedPrice).isEqualTo(getPrice3);

    }

    @Test
    public void 가격조회_비동기_호출_콜백_반환없음_테스트() throws Exception {
        Integer expectedPrice = 1100;

        CompletableFuture<Void> future = coffeeComponent.getPriceAsync("latte")
                .thenAccept(p -> {
                    log.info("1 콜백 가격은 {}원, 하지만 데이터를 반환하지는 않음", p);
                    assertThat(p).isEqualTo(expectedPrice);
                });

        log.info(" 아직 최종 데이터를 전달 받진 않았지만, 다른 작업 수행 가능, 논블로킹");

        assertThat(future.join()).isNull();
    }

    @Test
    public void 가격조회_비동기_호출_콜백_반환_테스트() throws Exception {
        Integer expectedPrice = 1100;

        CompletableFuture<Void> future = coffeeComponent.getPriceAsync("latte")
                .thenApply(p -> {
                    log.info("리턴 받은 가격은 {}원", p);
                    return p + 100;
                })
                .thenAccept(p->{
                    log.info(" 콜백 가격은 {}원, 하지만 데이터를 반환하지는 않음", p);
                });

        log.info(" 아직 최종 데이터를 전달 받진 않았지만, 다른 작업 수행 가능, 논블로킹");

        assertThat(future.join()).isNull();
    }



}
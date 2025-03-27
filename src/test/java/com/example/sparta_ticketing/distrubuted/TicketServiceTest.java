package com.example.sparta_ticketing.distrubuted;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.service.SeatService;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.repository.ShowRepository;
import com.example.sparta_ticketing.domain.ticket.dto.request.CreateTicketRequestDto;
import com.example.sparta_ticketing.domain.ticket.entity.Ticket;
import com.example.sparta_ticketing.domain.ticket.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SeatService seatService;

    private static final String REDIS_KEY = "show:2/S";
    private static final int TOTAL_TICKETS = 1000;
    private static final int THREAD_COUNT = 10;
    private static final Long SEAT_ID = 4L;
    private static final Long SHOW_ID = 2L;
    private static final Long USER_ID = 1L;

    @BeforeEach
    public void setUp() {
        redisTemplate.opsForValue().set(REDIS_KEY, String.valueOf(5));
    }


    @Test
    void noLockConcurrencyTest() throws InterruptedException {
        int testCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(testCount);

        AtomicInteger successfulUpdates = new AtomicInteger(0);

        for (int i = 0; i < testCount; i++) {
            executorService.submit(() -> {
                try {
                    ticketService.issueTicket(new CreateTicketRequestDto(USER_ID, SEAT_ID, SHOW_ID));

                    successfulUpdates.incrementAndGet();       // thread A 읽음 thread B 대기
                } catch (Exception e) {
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown(); //  1000 -1 = 999 .... 0이 될때까지
                }
            });
        }
        // 메인 스레드와 별개로  executorService 로 만든 스레드가 실행을 끝낼 때 까지
        latch.await(); // 모든 스레드가 작업을 완료할 때까지 대기
        executorService.shutdown(); // 자원 정리
        Seat seat = seatService.findSeat(SEAT_ID);

        Integer ticketLeft = Integer.valueOf(redisTemplate.opsForValue().get(REDIS_KEY));
        seat.setSeatCount(ticketLeft);
        String finalCount = String.valueOf(ticketLeft);

        System.out.println("남은 티켓: " + finalCount);
        System.out.println("성공한 업데이트 수: " + successfulUpdates.get());

        // 락을 사용하지 않았기 때문에 동시성 문제가 발생할 수 있으며, 성공한 업데이트 수와 count 값이 다를 수 있음
        assertNotEquals(successfulUpdates.get(), finalCount);

    }

}

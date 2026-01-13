package concurrencyTest;

import java.util.*;
import java.util.concurrent.*;

/**
 * HashMap 은 멀티스레드 환경에서 동시성을 보장하지 않음을 테스트한다.
 * */
public class HashMapAccountService {

    static class HashMapService {
        private final Map<String, Integer> balanceMap = new HashMap<>();

        public HashMapService() {
            balanceMap.put("ACC-001", 10_000);
        }

        public void withdraw(String accountId, int amount) {
            String threadName = Thread.currentThread().getName();

            Integer balance = balanceMap.get(accountId);
            System.out.println(threadName + " - 출금 시도 | 현재 잔액 = " + balance);

            if (balance >= amount) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ignored) {}

                int newBalance = balance - amount;
                balanceMap.put(accountId, newBalance);

                System.out.println(threadName +
                        " - 출금 완료 | 출금액 = " + amount +
                        ", 잔액 = " + newBalance);
            }
        }

        public int getBalance(String accountId) {
            return balanceMap.get(accountId);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        HashMapService service = new HashMapService();

        int numThreads = 10;
        int withdrawRequests = 100;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < withdrawRequests; i++) {
            executorService.submit(() -> service.withdraw("ACC-001", 100));
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("==== HashMap 기반 결과 ====");
        System.out.println("예상 잔액 : 0");
        System.out.println("실제 잔액 : " + service.getBalance("ACC-001"));
    }
}

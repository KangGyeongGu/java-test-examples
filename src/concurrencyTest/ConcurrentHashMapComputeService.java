package concurrencyTest;

import java.util.concurrent.*;

/**
 * 오퍼레이션(비즈니스로직 레벨)의 원자성을 보장하기 위해서는 compute() 내부 메서드 등을 사용해야 함을 테스트한다.
 * */
public class ConcurrentHashMapComputeService {

    static class HashMapAccountService {
        private final ConcurrentMap<String, Integer> balanceMap = new ConcurrentHashMap<>();

        public HashMapAccountService() {
            balanceMap.put("ACC-001", 10_000);
        }

        public void withdraw(String accountId, int amount) {
            String threadName = Thread.currentThread().getName();

            balanceMap.compute(accountId, (key, balance) -> {
                System.out.println(threadName + " - 출금 시도 | 현재 잔액 = " + balance);

                if (balance >= amount) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {}

                    int newBalance = balance - amount;
                    System.out.println(threadName + " - 출금 완료 | 출금액 = " + amount + ", 잔액 = " + newBalance);
                    return newBalance;
                }
                return balance;
            });
        }

        public int getBalance(String accountId) {
            return balanceMap.get(accountId);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        HashMapAccountService service = new HashMapAccountService();

        int numThreads = 10;
        int withdrawRequests = 100;

        ExecutorService executor =
                Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < withdrawRequests; i++) {
            executor.submit(() ->
                    service.withdraw("ACC-001", 100));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("==== ConcurrentHashMap 기반 결과 ====");
        System.out.println("예상 잔액 : 0");
        System.out.println("실제 잔액 : " + service.getBalance("ACC-001"));
    }
}

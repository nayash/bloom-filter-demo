package com.outliers.bloomfilter.services;

import com.outliers.bloomfilter.entities.User;
import com.outliers.bloomfilter.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BloomFilterHelper bloomFilterHelper;

    public void save(User user) throws Exception {
        if (userRepository.existsById(user.getUsername())) {
            throw new Exception(String.format("%s username exists", user.getUsername()));
        }
        userRepository.save(user);
    }

    public void simulateNaiveChecks(int n) {
        log.info("Processing request to do naive check for {} random users", n);
        long startTime = System.currentTimeMillis();
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build();
        for (int i=0; i<n; i++) {
            checkIfExistsNaive(generator.generate(10));
        }
        long endTime = System.currentTimeMillis();
        log.info("performed random user naive checks for {} users in {} seconds", n, (endTime - startTime) / 1000);
    }

    public boolean checkIfExistsNaive(String username) {
        return userRepository.findById(username).isPresent();
    }

    public boolean checkIfExistsBloom(String username) {
        return bloomFilterHelper.isPresent(username);
    }

    public void simulateBloomChecks(int n) {
        log.info("Processing request to do bloom check for {} random users", n);
        long startTime = System.currentTimeMillis();
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build();
        for (int i=0; i<n; i++) {
            checkIfExistsBloom(generator.generate(10));
        }
        long endTime = System.currentTimeMillis();
        log.info("performed random user bloom checks for {} users in {} seconds", n, (endTime - startTime) / 1000);
    }

    @Async
    public void saveRandom(int n) {
        log.info("Processing request to add {} random users", n);
        long startTime = System.currentTimeMillis();
        RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('0', 'z')
                .filteredBy(Character::isLetterOrDigit)
                .build();

        int batchSize = 1000; // Set batch size for optimal DB writes
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < n; i += batchSize) {
            int end = Math.min(i + batchSize, n);

            int finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<User> batch = new ArrayList<>();
                for (int j = finalI; j < end; j++) {
                    batch.add(User.builder()
                            .username(generator.generate(10))
                            .email(generator.generate(10) + "@gmail.com")
                            .password("test")
                            .build());
                }
                userRepository.saveAll(batch);
            }, executorService);

            futures.add(future);
        }

        // Wait for all threads to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        executorService.shutdown();
        long endTime = System.currentTimeMillis();
        log.info("Saved {} users in {} seconds", n, (endTime - startTime) / 1000);
    }

}



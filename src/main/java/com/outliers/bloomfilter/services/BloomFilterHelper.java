package com.outliers.bloomfilter.services;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.outliers.bloomfilter.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class BloomFilterHelper {

    private BloomFilter<String> bloomFilter;

    @Value("${bloom-filter.username.n}")
    private int expectedInsertions;
    @Value("${bloom-filter.username.p}")
    private double falsePositiveRate;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void initializeBloomFilter() {
        bloomFilter = BloomFilter.create(
                Funnels.stringFunnel(StandardCharsets.UTF_8),
                expectedInsertions,
                falsePositiveRate
        );

        // Load all usernames into the Bloom Filter. We can also do it incrementally as new users are being registered
        List<String> usernames = userRepository.findAllUsernames();
        usernames.forEach(bloomFilter::put);

        log.info("Bloom Filter initialized with " + usernames.size() + " usernames with expected false positive rate of: {}",
                bloomFilter.expectedFpp()); // 1.2MB for 1.01 crore items
    }

    public boolean mightContain(String username) {
        return bloomFilter.mightContain(username);
    }

    public boolean isPresent(String username) {
        return mightContain(username) && userRepository.existsById(username);
    }
}


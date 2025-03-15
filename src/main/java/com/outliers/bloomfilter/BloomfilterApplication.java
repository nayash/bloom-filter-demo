package com.outliers.bloomfilter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BloomfilterApplication {

	public static void main(String[] args) {
		SpringApplication.run(BloomfilterApplication.class, args);
	}

}

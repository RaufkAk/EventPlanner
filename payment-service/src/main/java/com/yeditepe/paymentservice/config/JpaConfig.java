package com.yeditepe.paymentservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.yeditepe.paymentservice.repository")
@EnableTransactionManagement
public class JpaConfig {
}

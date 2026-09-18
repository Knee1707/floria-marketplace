package com.starshop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Bật @CreatedDate / @LastModifiedDate cho BaseEntity. */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}

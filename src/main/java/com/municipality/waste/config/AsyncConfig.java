package com.municipality.waste.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables @Async methods (used by EmailService) so sending an email — which
 * involves a network round-trip to an SMTP server — never adds latency to
 * the request that triggered it (e.g. posting a message). If the SMTP
 * server is slow or unreachable, the caller still gets an instant response;
 * the email attempt happens on a background thread and failures are only
 * logged, never surfaced to the user.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}

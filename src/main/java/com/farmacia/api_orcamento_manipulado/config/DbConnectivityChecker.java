package com.farmacia.api_orcamento_manipulado.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;

@Component
public class DbConnectivityChecker implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DbConnectivityChecker.class);

    @Override
    public void run(String... args) {
        // Detect and mitigate environment JVM flags that prefer IPv6, which
        // can cause "Network unreachable" in environments without IPv6.
        try {
            String javaToolOpts = System.getenv("JAVA_TOOL_OPTIONS");
            if (javaToolOpts != null && javaToolOpts.contains("preferIPv6Addresses=true")) {
                log.warn(
                        "Detected JAVA_TOOL_OPTIONS contains preferIPv6Addresses=true. Overriding to prefer IPv4 stack to avoid connectivity issues.");
                System.setProperty("java.net.preferIPv4Stack", "true");
            }
        } catch (Exception ex) {
            log.debug("Unable to inspect or set network properties: {}", ex.toString());
        }

        try {
            String jdbc = System.getenv("SPRING_DATASOURCE_URL");
            String host = System.getenv("DB_HOST");
            String portStr = System.getenv("DB_PORT");
            if (jdbc != null && jdbc.startsWith("jdbc:")) {
                try {
                    // try to extract host and port from JDBC URL
                    String withoutJdbc = jdbc.substring(5); // remove 'jdbc:'
                    URI uri = new URI(withoutJdbc);
                    host = uri.getHost();
                    int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                    portStr = String.valueOf(port);
                } catch (Exception ex) {
                    log.debug("Could not parse SPRING_DATASOURCE_URL as URI: {}", ex.getMessage());
                }
            }

            if (host == null) {
                log.warn("DB host not defined (SPRING_DATASOURCE_URL or DB_HOST) - skipping connectivity check");
                return;
            }

            int port = portStr != null ? Integer.parseInt(portStr) : 5432;
            log.info("DB connectivity check: resolving host='{}' port={}", host, port);

            try {
                InetAddress[] addrs = InetAddress.getAllByName(host);
                for (InetAddress a : addrs) {
                    log.info("Resolved address: {} (reach: {})", a.getHostAddress(), a.isReachable(2000));
                }
            } catch (Exception e) {
                log.warn("DNS resolution failed for {}: {}", host, e.getMessage());
            }

            // attempt a raw TCP connect
            try (Socket s = new Socket()) {
                s.connect(new java.net.InetSocketAddress(host, port), 5000);
                log.info("TCP connect to {}:{} succeeded", host, port);
            } catch (Exception e) {
                log.error("TCP connect to {}:{} failed: {}", host, port, e.toString());
            }

        } catch (Exception ex) {
            log.error("Unexpected error in DbConnectivityChecker: {}", ex.toString());
        }
    }
}

package com.example.reservation;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@SpringBootTest(
        classes = ReservationApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {
    public static final String MYSQL_CONTAINER_NAME = "test";
    public static final String MYSQL_DB_NAME = "meetingroom";
    public static final int MYSQL_PORT = 3306;

    @Container
    static DockerComposeContainer composeContainer =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService(
                            MYSQL_CONTAINER_NAME,
                            MYSQL_PORT,
                            Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(60))
                    );

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry dynamicPropertyRegistry) {

        final String host = composeContainer.getServiceHost(MYSQL_CONTAINER_NAME, MYSQL_PORT);
        final Integer port = composeContainer.getServicePort(MYSQL_CONTAINER_NAME, MYSQL_PORT);

        dynamicPropertyRegistry.add("spring.datasource.url",
                () -> "jdbc:mysql://%s:%d/%s".formatted(host, port, MYSQL_DB_NAME));
        dynamicPropertyRegistry.add("spring.datasource.username", () -> "root");
        dynamicPropertyRegistry.add("spring.datasource.password", () -> "test");
        dynamicPropertyRegistry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }
}

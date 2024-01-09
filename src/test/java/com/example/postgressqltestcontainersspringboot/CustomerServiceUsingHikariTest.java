package com.example.postgressqltestcontainersspringboot;

import com.example.postgressqltestcontainersspringboot.util.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CustomerServiceUsingHikariTest {

    static PostgreSQLContainer<?> postgreContainer = new PostgreSQLContainer<>("postgres:16");
    static CustomerService customerService;
    static DBConnection dbConnection;
    static TestUtils testUtils;
    private static int testCount;

    @BeforeAll
    static void startDb() throws SQLException, InterruptedException {
        postgreContainer.start();

        final String url = postgreContainer.getJdbcUrl();
        dbConnection =  new ConnectionPool(url, postgreContainer.getUsername(), postgreContainer.getPassword());

        testUtils = new TestUtils(dbConnection);
        testUtils.setUpData();
        customerService = new CustomerService(dbConnection);
    }

    @AfterAll
    static void stopDb(){
        postgreContainer.stop();
    }

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        log.info("Test {}: {}", ++testCount, testInfo.getDisplayName());
    }

    @Test
    public void containerRunning(){
        // Assert whether the container is running
        assertTrue(postgreContainer.isRunning());
    }

    @Test
    public void getCustomer() throws SQLException {
        // Generate a random customer using test utilities
        Customer testCustomer = testUtils.getRandomCustomer();

        // Call the customerService to retrieve a customer based on the generated user's ID
        Customer customer = customerService.getCustomer(testCustomer.getId());

        // Assert that the retrieved customer's ID matches the generated customer's ID
        assertEquals(testCustomer.getId(), customer.getId());

        // Assert that the retrieved customer's name matches the generated customer's name
        assertEquals(testCustomer.getName(), customer.getName());
    }

    @Test
    public void createCustomer() throws SQLException {
        // Generate a random string for the test customer's name
        String testName = testUtils.getRandomString(10);

        // Create a new Customer object with the generated name
        Customer testCustomer = new Customer(testName);

        // Call the customerService to create a new customer and get the result
        Customer newCustomer = customerService.createCustomer(testCustomer);

        // Assert that the created customer is not null
        assertNotNull(newCustomer);

        // Retrieve the customer from the customerService based on the newly created customer's ID
        Customer customer = customerService.getCustomer(newCustomer.getId());

        // Assert that the retrieved customer's name matches the originally generated name
        assertEquals(testName, customer.getName());
    }
}

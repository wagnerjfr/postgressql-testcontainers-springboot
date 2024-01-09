package com.example.postgressqltestcontainersspringboot.util;

import com.example.postgressqltestcontainersspringboot.Customer;
import com.example.postgressqltestcontainersspringboot.DBConnectionProvider;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class TestUtils {
    private static final String TABLE = "Customer";
    private final DBConnectionProvider dbConnectionProvider;

    public TestUtils(DBConnectionProvider dbConnectionProvider){
        this.dbConnectionProvider = dbConnectionProvider;
    }

    public void checkConnection() throws InterruptedException, SQLException {
        Connection connection = null;
        int attempts = 10;
        while (attempts > 0) {
            try {
                connection = this.dbConnectionProvider.getConnection();
                break;
            } catch (Exception e) {
                log.warn("Trying to get the connection..");
                attempts--;
                Thread.sleep(200);
            }
        }
        if (connection != null) {
            connection.close();
        } else {
            throw new RuntimeException("Unable to get the DB connection");
        }
    }

    public void setUpData() throws SQLException {
        String createUserSql = "create table if not exists " + TABLE + " (id int primary key, name varchar(20) null)";
        String insertUserSql = "insert into " + TABLE + " (id, name) values(1, 'Wagner')";
        try (Connection connection = this.dbConnectionProvider.getConnection()) {
            Statement createStatement = connection.createStatement();
            Statement insertStatement = connection.createStatement();
            createStatement.execute(createUserSql);
            insertStatement.execute(insertUserSql);
         }
    }

    public Customer getRandomCustomer() throws SQLException {
        String query = "SELECT id, name FROM " + TABLE + " ORDER by random() limit 1";
        try (Connection connection = this.dbConnectionProvider.getConnection()) {
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            resultSet.next();
            return new Customer(resultSet.getInt("id"), resultSet.getString("name"));
        }
    }

    public Customer getCustomerById(int id) throws SQLException {
        try (Connection connection = this.dbConnectionProvider.getConnection()) {
            String query = "SELECT id, name FROM " + TABLE + " WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return new Customer(rs.getInt("id"), rs.getString("name"));
        }
    }

    public String getRandomString(int length){
        return RandomStringUtils.randomAlphabetic(length);
    }
}

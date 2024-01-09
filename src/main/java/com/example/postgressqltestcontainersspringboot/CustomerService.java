package com.example.postgressqltestcontainersspringboot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public final class CustomerService {
    private static final String TABLE = "Customer";
    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    private final DBConnectionProvider dbConnectionProvider;

    public CustomerService(DBConnectionProvider dbConnectionProvider) {
        this.dbConnectionProvider = dbConnectionProvider;
    }

    public Customer getCustomer(int id) throws SQLException {
        try (Connection connection = this.dbConnectionProvider.getConnection()) {
            String query = "SELECT * FROM " + TABLE + " WHERE id = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return new Customer(rs.getInt("id"), rs.getString("name"));
        }
    }

    public Customer createCustomer(Customer customer) throws SQLException {
        try (Connection connection = this.dbConnectionProvider.getConnection()) {
            String query = "INSERT INTO " + TABLE + " (id, name) VALUES (?, ?)";
            int id = atomicInteger.incrementAndGet();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, customer.getName());
            ps.executeUpdate();
            customer.setId(id);
        }
        return customer;
    }
}

package com.example.postgressqltestcontainersspringboot;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

@Slf4j
public class ConnectionPool implements DBConnection{
    private HikariDataSource ds;

    public ConnectionPool(String url, String username, String password) throws InterruptedException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        create(config);
    }

    @Override
    public Connection getConnection() {
        try {
            return ds.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void create(HikariConfig config) throws InterruptedException {
        int attempts = 10;
        while (attempts > 0) {
            try {
                this.ds = new HikariDataSource(config);
                break;
            } catch (Exception e) {
                log.warn("Trying to create HikariDataSource..");
                attempts--;
                Thread.sleep(200);
            }
        }
        if (this.ds == null) {
            throw new RuntimeException("Unable to create HikariDataSource");
        }
    }
}

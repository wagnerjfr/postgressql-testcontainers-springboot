package com.example.postgressqltestcontainersspringboot;

import java.sql.Connection;

public interface DBConnection {
    Connection getConnection();
}

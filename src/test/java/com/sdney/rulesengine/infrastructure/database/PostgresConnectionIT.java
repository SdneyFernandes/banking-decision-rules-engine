package com.sdney.rulesengine.infrastructure.database;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("local")
public class PostgresConnectionIT {

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldConnectToPostgres() throws Exception {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT 1");
                ResultSet resultSet = statement.executeQuery()
        ) {
            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt(1));
        }
    }


}



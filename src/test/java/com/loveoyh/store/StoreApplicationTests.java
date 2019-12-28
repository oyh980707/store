package com.loveoyh.store;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StoreApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Resource
	DataSource dataSource;
	
	@Test
	public void getConnection() throws SQLException {
		Connection c = dataSource.getConnection();
		System.err.println(c);
	}

}

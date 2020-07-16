package com.loveoyh.store;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@MapperScan("com.loveoyh.store.mapper")
public class StoreApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoreApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
		LOGGER.info("服务[store]启动成功");
	}
	
	@Bean
	public MultipartConfigElement getMultipartConfig() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		//一次传输文件大小限制
		DataSize maxFileSize = DataSize.ofMegabytes(50);
		factory.setMaxFileSize(maxFileSize );
		//一次请求数据量限制
		DataSize maxRequestSize = DataSize.ofMegabytes(100);
		factory.setMaxRequestSize(maxRequestSize);
		
		return factory.createMultipartConfig();
	}

}

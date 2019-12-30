package com.loveoyh.store;

import javax.servlet.MultipartConfigElement;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@SpringBootApplication
@Configuration
@MapperScan("com.loveoyh.store.mapper")
public class StoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
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

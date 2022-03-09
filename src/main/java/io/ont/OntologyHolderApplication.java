package io.ont;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("io.ont.mapper")
public class OntologyHolderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OntologyHolderApplication.class, args);
	}
}
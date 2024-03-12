package com.example.CampusPartnerBackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.CampusPartnerBackend.Mapper")
public class CampusPartnerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusPartnerBackendApplication.class, args);
    }

}

package com.example.CampusPartnerBackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@SpringBootApplication
@MapperScan("com.example.CampusPartnerBackend.Mapper")
@EnableScheduling
@EnableSwagger2WebMvc
public class CampusPartnerBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusPartnerBackendApplication.class, args);
    }

}

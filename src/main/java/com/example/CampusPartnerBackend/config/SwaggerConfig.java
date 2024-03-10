package com.example.CampusPartnerBackend.config; /**
 * @author Shier
 * @date 2023/02/02
 * 自定义 Swagger 接口文档的配置
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author: 诨号无敌鸭
 * @date: 2022/11/20
 * @ClassName: yupao-backend01
 * @Description: 自定义 Swagger 接口文档的配置
 */
@Configuration
@EnableSwagger2WebMvc //启用 Swagger2 在 Spring MVC 环境中的使用。
@Profile({"dev", "test"})   //仅在"dev"和"test"两个环境下生效，即只有在开发和测试环境下才会启用 Swagger 接口文档配置。
public class SwaggerConfig {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 这里一定要标注你控制器的位置
                .apis(RequestHandlerSelectors.basePackage("com.example.CampusPartnerBackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api 信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("无敌鸭用户中心")
                .description("无敌鸭用户中心接口文档")
                .termsOfServiceUrl("https://github.com/Serendipityzhezi")
                .contact(new Contact("诨号无敌鸭", "https://blog.csdn.net/m0_74870396?spm=1000.2115.3001.5343","1342558165@qq.com"))
                .version("1.0")
                .build();
    }
}
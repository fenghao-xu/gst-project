package com.ylzs.common.config;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import com.ylzs.controller.auth.UserContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xuwei
 * Swagger2 配置，
 */
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
public class Swagger2Configurer {

	@Bean
	public Docket createRestApi() {
		ParameterBuilder tokenPar = new ParameterBuilder();
		List<Parameter> pars = new ArrayList<>();
		tokenPar.name("X-Auth-Token").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
		pars.add(tokenPar.build());

		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
				.ignoredParameterTypes(UserContext.class)
				.select()// 选择那些路径和api会生成document
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))// 设置只生成 有ApiOperation注解的api接口的文档
				.apis(RequestHandlerSelectors.basePackage("com.ylzs"))// 设置扫描的包路径
				.paths(PathSelectors.any())// 对所有路径进行监控
				.build()
				.globalOperationParameters(pars);
	}



	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("建标接口") // 标题
				.description("赢领智尚GST建标后台接口")// 描述
				.version("1.0")// 版本号
				.contact(new Contact("", "", ""))// 联系方式
				.build();
	}
}

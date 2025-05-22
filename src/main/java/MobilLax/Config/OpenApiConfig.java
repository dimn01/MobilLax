/*
 * OpenApiConfig.java
 * ✅ 목적: Swagger(OpenAPI 3.0) 문서화 설정
 */

package MobilLax.Config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    /**
     * ✅ Swagger/OpenAPI 기본 정보 설정
     * @return OpenAPI 인스턴스
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🚍 MobilLax 대중교통 API 문서")
                        .description("도시/시외/고속/열차/지하철 등 대중교통 경로 및 결제 API 명세")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("MobilLax 개발팀")
                                .email("team@mobillax.com")
                        )
                );
    }
}

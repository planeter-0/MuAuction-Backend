package icu.planeter.muauction.common.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @description: TODO
 * @author Planeter
 * @date 2021/5/15 0:41
 * @status dev
 */
@Configuration
public class JacksonConfig implements WebMvcConfigurer {
    @Bean
    public ObjectMapper objectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(String.class, new XSSJacksonSerializer());
        simpleModule.addDeserializer(String.class, new XSSJacksonDeserializer());
        return new ObjectMapper().registerModule(simpleModule);
    }
}

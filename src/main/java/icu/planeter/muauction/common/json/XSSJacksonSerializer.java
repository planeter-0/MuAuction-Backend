package icu.planeter.muauction.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;

/**
 * @description: TODO
 * @author Planeter
 * @date 2021/5/15 0:18
 * @status dev
 */
@JsonSerialize
public class XSSJacksonSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String handledString = HtmlUtils.htmlEscape(s);
        jsonGenerator.writeString(handledString);
    }

}

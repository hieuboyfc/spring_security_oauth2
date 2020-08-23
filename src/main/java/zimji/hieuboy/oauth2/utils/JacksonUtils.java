package zimji.hieuboy.oauth2.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.StringUtils;
import zimji.hieuboy.oauth2.exceptions.InternalServerException;

import java.io.IOException;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 20/08/2020 - 17:22
 */

public class JacksonUtils {

    private static JacksonUtils instance = new JacksonUtils();

    public static JacksonUtils getInstance() {
        return instance;
    }

    private JacksonUtils() {

    }

    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object findFilterId(Annotated a) {
                return null;
            }
        });
        return objectMapper;
    }

    public <T> T string2Object(String input, Class<T> clazz) {
        T result = null;
        try {
            if (!StringUtils.isEmpty(input)) {
                result = getObjectMapper().readValue(input, clazz);
            }
        } catch (IOException e) {
            throw new InternalServerException(Common.getStackTrace(e));
        }
        return result;
    }

    public ObjectWriter getObjectWriter(String filterId, String[] arrFilterOutAllExcept,
                                        String[] arrSerializeAllExcept) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        if (!StringUtils.isEmpty(filterId)) {
            if (arrFilterOutAllExcept != null && arrFilterOutAllExcept.length > 0) {
                SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter
                        .filterOutAllExcept(arrFilterOutAllExcept);
                FilterProvider filterProvider = new SimpleFilterProvider().addFilter(filterId,
                        simpleBeanPropertyFilter);
                return objectMapper.writer(filterProvider);
            }
            if (arrSerializeAllExcept != null && arrSerializeAllExcept.length > 0) {
                SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter
                        .serializeAllExcept(arrSerializeAllExcept);
                FilterProvider filterProvider = new SimpleFilterProvider().addFilter(filterId,
                        simpleBeanPropertyFilter);
                return objectMapper.writer(filterProvider);
            }
        }
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            private static final long serialVersionUID = 1L;

            @Override
            public Object findFilterId(Annotated a) {
                return null;
            }
        });
        return objectMapper.writer(new SimpleFilterProvider());
    }

    public String object2String(Object input) {
        return object2String(input, null, null, null);
    }

    public String object2String(Object input, String filterId, String[] arrFilterOutAllExcept,
                                String[] arrSerializeAllExcept) {
        String result = null;
        try {
            if (input != null) {
                result = getObjectWriter(filterId, arrFilterOutAllExcept, arrSerializeAllExcept)
                        .writeValueAsString(input);
            }
        } catch (JsonProcessingException e) {
            throw new InternalServerException(Common.getStackTrace(e));
        }
        return result;
    }

}

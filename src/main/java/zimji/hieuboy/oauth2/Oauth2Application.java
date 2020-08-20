package zimji.hieuboy.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import zimji.hieuboy.oauth2.configs.AppProperties;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 15/08/2020 - 22:06
 */

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Oauth2Application {

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(Oauth2Application.class, args);
    }

}

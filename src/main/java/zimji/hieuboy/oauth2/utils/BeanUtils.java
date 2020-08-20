package zimji.hieuboy.oauth2.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import zimji.hieuboy.oauth2.configs.AppProperties;
import zimji.hieuboy.oauth2.exceptions.ResourceNotFoundException;

/**
 * @author HieuDT28 - (Hiếu Boy)
 * created 17/08/2020 - 11:27
 */

@Service
public class BeanUtils implements ApplicationContextAware {

    private static AppProperties appProperties;

    private static ApplicationContext applicationContext;

    public static ApplicationContext getContext() {
        return applicationContext;
    }

    public static void setContext(ApplicationContext applicationContext) {
        BeanUtils.applicationContext = applicationContext;
    }

    public static AppProperties getAppProperties() {
        if (appProperties == null) {
            appProperties = getBean(AppProperties.class);
        }
        return appProperties;
    }

    public static <T> T getBean(Class<T> beanClass) {
        if (getContext() == null) {
            throw new ResourceNotFoundException(BeanUtils.class.getName(), ApplicationContext.class.getName(),
                    beanClass.getName());
        }
        return getContext().getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }

}

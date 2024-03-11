package com.invoiceq.oracleebsadapter.util;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Locale;

@Component
public class AppUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    private static List<String> activeProfile;

    public static <T extends Object> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);

    }

    public static <T extends Object> T getBean(Class<T> beanClass, String qualifier) {
        return context.getBean(qualifier, beanClass);
    }

    public static String getBeanName(Class beanClass) {
        return context.getBeanNamesForType(beanClass)[0];
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        setContext(context);
        activeProfile = List.of(context.getBean(Environment.class).getActiveProfiles());
    }



    public static boolean isEnabledProfile(String profile) {
        return activeProfile.contains(profile);
    }

    private static synchronized void setContext(ApplicationContext context) {
        AppUtil.context = context;
    }
}

package com.starshop;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Cho phép deploy dạng WAR lên Tomcat 10.1 ngoài (ngoài chế độ embedded).
 */
public class ServletInitializer extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(StarShopApplication.class);
    }
}

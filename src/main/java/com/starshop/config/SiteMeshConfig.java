package com.starshop.config;

import org.sitemesh.builder.SiteMeshFilterBuilder;
import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * SiteMesh 3 cấu hình bằng code (không dùng sitemesh3.xml) — tránh lỗi decorator-prefix.
 * Mọi trang nội dung sẽ được "bọc" bởi layout chung default.jsp.
 * LEADER sở hữu — thành viên KHÔNG sửa.
 */
@Configuration
public class SiteMeshConfig {

    @Bean
    public FilterRegistrationBean<ConfigurableSiteMeshFilter> siteMeshFilter() {
        FilterRegistrationBean<ConfigurableSiteMeshFilter> reg =
                new FilterRegistrationBean<>(new StarShopSiteMeshFilter());
        reg.addUrlPatterns("/*");
        // Bọc cả REQUEST và FORWARD vì Spring MVC forward tới JSP
        reg.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD));
        reg.setOrder(1);
        return reg;
    }

    /** Định nghĩa mapping decorator ngay trong code. */
    static class StarShopSiteMeshFilter extends ConfigurableSiteMeshFilter {
        @Override
        protected void applyCustomConfiguration(SiteMeshFilterBuilder builder) {
            builder
                // Layout mặc định cho toàn site
                .addDecoratorPath("/*", "/WEB-INF/views/decorators/default.jsp")
                // Trang không cần bọc layout (đăng nhập/đăng ký dùng layout riêng nếu muốn)
                .addExcludedPath("/webjars/*")
                .addExcludedPath("/assets/*")
                .addExcludedPath("/api/*")
                .addExcludedPath("/error");
        }
    }
}

package com.starshop.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Sau khi đăng nhập, đưa mỗi vai trò về ĐÚNG khu chức năng của mình (không về trang mua hàng).
 * ADMIN→/admin · MANAGER→/manager · VENDOR→/vendor · SHIPPER→/shipper · USER→/ (trang mua).
 * @owner: L (F2.3 / F2.6).
 */
@Component
public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        String target;
        if (roles.contains("ROLE_ADMIN"))        target = "/admin";
        else if (roles.contains("ROLE_MANAGER")) target = "/manager";
        else if (roles.contains("ROLE_VENDOR"))  target = "/vendor";
        else if (roles.contains("ROLE_SHIPPER")) target = "/shipper";
        else                                     target = "/";   // USER: trang mua hàng

        response.sendRedirect(request.getContextPath() + target);
    }
}

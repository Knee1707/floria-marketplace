<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><sitemesh:write property="title">Floria</sitemesh:write></title>
    <link rel="stylesheet" href="${ctx}/webjars/bootstrap/5.3.3/css/bootstrap.min.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/assets/css/tokens.css">
    <link rel="stylesheet" href="${ctx}/assets/css/main.css">
    <sitemesh:write property="head"/>
</head>
<body>
<a class="skip-link" href="#main">Bỏ qua tới nội dung</a>

<%-- ===== Top nav dùng chung (rules/frontend/01) ===== --%>
<nav class="app-nav" aria-label="Điều hướng chính">
  <div class="container container-gutter d-flex align-items-center justify-content-between" style="height:60px">
    <a class="d-flex align-items-center gap-2 fw-bold" href="${ctx}/" style="font-size:var(--text-xl)">
      <t:icon name="store" size="24"/> Floria
    </a>
    <div class="d-none d-md-flex align-items-center gap-3">
      <%-- Shipper không mua hàng → ẩn "Sản phẩm" để header chỉ còn lối vào kênh giao vận --%>
      <sec:authorize access="!hasRole('SHIPPER')"><a href="${ctx}/products">Sản phẩm</a></sec:authorize>
      <sec:authorize access="hasRole('VENDOR')"><a href="${ctx}/vendor">Kênh bán</a></sec:authorize>
      <sec:authorize access="hasRole('SHIPPER')"><a href="${ctx}/shipper">Giao hàng</a></sec:authorize>
      <sec:authorize access="hasRole('ADMIN')"><a href="${ctx}/admin">Quản trị</a></sec:authorize>
    </div>
    <div class="d-flex align-items-center gap-2">
      <%-- Giỏ hàng (truy cập nhanh) cho người MUA: USER và cả VENDOR (Vendor có quyền của User - yêu cầu GV). --%>
      <sec:authorize access="hasAnyRole('USER','VENDOR')">
        <a href="${ctx}/cart" title="Giỏ hàng"><t:icon name="cart" size="22" label="Giỏ hàng"/></a>
      </sec:authorize>
      <%-- Menu tài khoản: có Lịch sử mua hàng, Yêu thích, Đã xem, Địa chỉ, Hồ sơ, Đăng xuất --%>
      <sec:authorize access="isAuthenticated()">
        <div class="dropdown">
          <button class="btn btn-sm btn-light dropdown-toggle d-flex align-items-center gap-1" type="button"
                  data-bs-toggle="dropdown" aria-expanded="false">
            <t:icon name="user" size="18"/> Tài khoản
          </button>
          <ul class="dropdown-menu dropdown-menu-end">
            <%-- Chức năng MUA HÀNG: USER và VENDOR (Vendor có quyền của User - yêu cầu GV) --%>
            <sec:authorize access="hasAnyRole('USER','VENDOR')">
              <li><a class="dropdown-item" href="${ctx}/account/orders"><t:icon name="package" size="16"/> Lịch sử mua hàng</a></li>
              <li><a class="dropdown-item" href="${ctx}/cart"><t:icon name="cart" size="16"/> Giỏ hàng</a></li>
              <li><a class="dropdown-item" href="${ctx}/wishlist"><t:icon name="heart" size="16"/> Sản phẩm yêu thích</a></li>
              <li><a class="dropdown-item" href="${ctx}/account/viewed"><t:icon name="eye" size="16"/> Sản phẩm đã xem</a></li>
              <li><a class="dropdown-item" href="${ctx}/account/addresses"><t:icon name="home" size="16"/> Địa chỉ nhận hàng</a></li>
              <li><hr class="dropdown-divider"></li>
            </sec:authorize>
            <%-- Lối tắt kênh bán cho Vendor --%>
            <sec:authorize access="hasRole('VENDOR')">
              <li><a class="dropdown-item" href="${ctx}/vendor"><t:icon name="store" size="16"/> Kênh bán của tôi</a></li>
              <li><hr class="dropdown-divider"></li>
            </sec:authorize>
            <%-- Hồ sơ: cho người có khu tài khoản (USER, VENDOR) --%>
            <sec:authorize access="hasAnyRole('USER','VENDOR')">
              <li><a class="dropdown-item" href="${ctx}/account/profile"><t:icon name="user" size="16"/> Hồ sơ của tôi</a></li>
            </sec:authorize>
            <li>
              <form method="post" action="${ctx}/auth/logout" class="m-0">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button class="dropdown-item" type="submit"><t:icon name="logout" size="16"/> Đăng xuất</button>
              </form>
            </li>
          </ul>
        </div>
      </sec:authorize>
      <sec:authorize access="isAnonymous()">
        <a class="btn btn-sm btn-light" href="${ctx}/auth/login">Đăng nhập</a>
        <a class="btn btn-sm btn-brand" href="${ctx}/auth/register">Đăng ký</a>
      </sec:authorize>
    </div>
  </div>
</nav>

<main id="main" class="app-main">
  <div class="container container-gutter">
    <sitemesh:write property="body"/>
  </div>
</main>

<footer class="text-center muted" style="padding:var(--space-8) 0;border-top:1px solid var(--color-border)">
  <small>Floria &copy; 2026 — Đồ án Lập trình web (Spring Boot)</small>
</footer>

<script src="${ctx}/webjars/bootstrap/5.3.3/js/bootstrap.bundle.min.js"></script>
<sitemesh:write property="page.script"/>
</body>
</html>

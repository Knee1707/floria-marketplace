<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
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
      <a href="${ctx}/products">Sản phẩm</a>
      <sec:authorize access="hasRole('VENDOR')"><a href="${ctx}/vendor">Kênh bán</a></sec:authorize>
      <sec:authorize access="hasRole('SHIPPER')"><a href="${ctx}/shipper">Giao hàng</a></sec:authorize>
      <sec:authorize access="hasAnyRole('ADMIN','MANAGER')"><a href="${ctx}/admin">Quản trị</a></sec:authorize>
    </div>
    <div class="d-flex align-items-center gap-2">
      <sec:authorize access="isAuthenticated()">
        <a href="${ctx}/cart" title="Giỏ hàng"><t:icon name="cart" size="22" label="Giỏ hàng"/></a>
        <a href="${ctx}/account/orders" title="Đơn của tôi"><t:icon name="package" size="22" label="Đơn của tôi"/></a>
        <span class="d-none d-sm-inline" style="opacity:.9"><sec:authentication property="name"/></span>
        <form method="post" action="${ctx}/auth/logout" class="d-inline m-0">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <button class="btn btn-sm btn-light" type="submit">Đăng xuất</button>
        </form>
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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head><title>Đăng nhập - Floria</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-5">
    <h3 class="mb-3">Đăng nhập</h3>
    <c:if test="${param.error != null}"><div class="alert alert-danger">Sai email hoặc mật khẩu.</div></c:if>
    <c:if test="${param.activated != null}"><div class="alert alert-success">Kích hoạt thành công, mời đăng nhập.</div></c:if>
    <c:if test="${param.logout != null}"><div class="alert alert-info">Đã đăng xuất.</div></c:if>
    <c:if test="${param.reset != null}"><div class="alert alert-success">Đổi mật khẩu thành công, mời đăng nhập.</div></c:if>
    <form method="post" action="${pageContext.request.contextPath}/auth/do-login">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div class="mb-3"><label class="form-label">Email</label>
            <input class="form-control" type="email" name="email" required></div>
        <div class="mb-3"><label class="form-label">Mật khẩu</label>
            <input class="form-control" type="password" name="password" required></div>
        <button class="btn btn-danger w-100">Đăng nhập</button>
    </form>
    <p class="mt-3 text-center mb-1"><a href="${pageContext.request.contextPath}/auth/forgot">Quên mật khẩu?</a></p>
    <p class="text-center"><a href="${pageContext.request.contextPath}/auth/register">Chưa có tài khoản? Đăng ký</a></p>
  </div>
</div>
</body>
</html>

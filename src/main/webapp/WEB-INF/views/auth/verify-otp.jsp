<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head><title>Xác thực OTP - Floria</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-5">
    <h3 class="mb-3">Nhập mã OTP</h3>
    <p class="text-muted">Mã OTP đã gửi tới <b>${email}</b> (hết hạn sau 5 phút).</p>
    <c:if test="${error != null}"><div class="alert alert-danger">${error}</div></c:if>
    <form method="post" action="${pageContext.request.contextPath}/auth/verify">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="email" value="${email}"/>
        <div class="mb-3"><label class="form-label">Mã OTP</label>
            <input class="form-control" name="otp" maxlength="6" required></div>
        <button class="btn btn-danger w-100">Xác nhận</button>
    </form>
  </div>
</div>
</body>
</html>

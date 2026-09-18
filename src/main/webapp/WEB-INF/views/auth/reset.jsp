<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Đặt lại mật khẩu - Floria</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-5">
    <t:card title="Đặt lại mật khẩu">
      <p class="muted">Nhập mã OTP đã gửi tới <b>${email}</b> (hết hạn sau 5 phút) và mật khẩu mới.</p>
      <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
      <form method="post" action="${pageContext.request.contextPath}/auth/reset">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="hidden" name="email" value="${email}"/>
        <t:form-field name="otp" label="Mã OTP" required="true"/>
        <t:form-field name="newPassword" label="Mật khẩu mới" type="password" required="true" hint="Tối thiểu 6 ký tự"/>
        <button class="btn btn-brand btn-min w-100">Đổi mật khẩu</button>
      </form>
    </t:card>
  </div>
</div>
</body>
</html>

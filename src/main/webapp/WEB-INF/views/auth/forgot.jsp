<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Quên mật khẩu - Floria</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-5">
    <t:card title="Quên mật khẩu">
      <p class="muted">Nhập email đã đăng ký, chúng tôi sẽ gửi mã OTP để đặt lại mật khẩu.</p>
      <form method="post" action="${pageContext.request.contextPath}/auth/forgot">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <t:form-field name="email" label="Email" type="email" required="true"/>
        <button class="btn btn-brand btn-min w-100">Gửi mã OTP</button>
      </form>
      <p class="mt-3 text-center"><a href="${pageContext.request.contextPath}/auth/login">Quay lại đăng nhập</a></p>
    </t:card>
  </div>
</div>
</body>
</html>

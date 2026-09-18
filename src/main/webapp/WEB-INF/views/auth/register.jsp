<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head><title>Đăng ký - Floria</title></head>
<body>
<div class="row justify-content-center">
  <div class="col-md-5">
    <h3 class="mb-3">Đăng ký tài khoản</h3>
    <c:if test="${error != null}"><div class="alert alert-danger">${error}</div></c:if>
    <form:form method="post" action="${pageContext.request.contextPath}/auth/register" modelAttribute="form">
        <div class="mb-3"><label class="form-label">Họ tên</label>
            <form:input path="fullName" cssClass="form-control"/>
            <form:errors path="fullName" cssClass="text-danger small"/></div>
        <div class="mb-3"><label class="form-label">Email</label>
            <form:input path="email" type="email" cssClass="form-control"/>
            <form:errors path="email" cssClass="text-danger small"/></div>
        <div class="mb-3"><label class="form-label">Số điện thoại</label>
            <form:input path="phone" cssClass="form-control"/>
            <form:errors path="phone" cssClass="text-danger small"/></div>
        <div class="mb-3"><label class="form-label">Mật khẩu</label>
            <form:password path="password" cssClass="form-control"/>
            <form:errors path="password" cssClass="text-danger small"/></div>
        <button class="btn btn-danger w-100">Đăng ký & nhận OTP</button>
    </form:form>
  </div>
</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Hồ sơ - Floria</title></head>
<body>
<t:page-header title="Hồ sơ của tôi"/>
<t:card>
  <p><b>Họ tên:</b> ${user.fullName}</p>
  <p><b>Email:</b> ${user.email}</p>
  <p><b>Số điện thoại:</b> ${user.phone}</p>
  <p><b>Vai trò:</b> ${user.role}</p>
  <div class="d-flex gap-2 flex-wrap">
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/account/addresses">Quản lý địa chỉ</a>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/account/orders">Đơn của tôi</a>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/wishlist">Sản phẩm yêu thích</a>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/account/viewed">Sản phẩm đã xem</a>
  </div>
</t:card>
</body>
</html>

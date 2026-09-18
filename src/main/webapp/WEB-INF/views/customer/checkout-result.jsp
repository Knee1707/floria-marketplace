<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Đặt hàng thành công - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:card>
  <div class="ss-state">
    <span style="color:var(--success)"><t:icon name="check" size="40" label="Thành công"/></span>
    <h2>Đặt hàng thành công!</h2>
    <c:if test="${not empty note}"><p class="muted">${note}</p></c:if>
    <p>Mã đơn: <c:forEach var="o" items="${orders}"><b>${o.code}</b> </c:forEach></p>
    <a class="btn btn-brand btn-min" href="${ctx}/account/orders">Xem đơn của tôi</a>
    <a class="btn btn-outline-secondary btn-min" href="${ctx}/products">Tiếp tục mua</a>
  </div>
</t:card>
</body>
</html>

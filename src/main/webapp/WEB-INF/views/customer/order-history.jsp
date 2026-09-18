<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Đơn của tôi - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:page-header title="Đơn của tôi"/>

<div class="d-flex gap-2 flex-wrap mb-3">
  <a class="btn btn-sm ${empty status?'btn-brand':'btn-outline-secondary'}" href="${ctx}/account/orders">Tất cả</a>
  <c:forEach var="s" items="${statuses}">
    <a class="btn btn-sm ${status==s?'btn-brand':'btn-outline-secondary'}" href="${ctx}/account/orders?status=${s}">
      <t:status-badge value="${s}"/></a>
  </c:forEach>
</div>

<c:choose>
  <c:when test="${orders.totalElements == 0}">
    <t:empty-state variant="empty" title="Chưa có đơn nào" actionHref="/products" actionLabel="Mua sắm"/>
  </c:when>
  <c:otherwise>
    <table class="ss-table">
      <thead><tr><th>Mã đơn</th><th>Cửa hàng</th><th>Trạng thái</th><th class="num">Tổng</th></tr></thead>
      <tbody>
        <c:forEach var="o" items="${orders.content}">
          <tr><td>${o.code}</td><td>${o.store.name}</td><td><t:status-badge value="${o.status}"/></td>
            <td class="num"><fmt:formatNumber value="${o.total}" type="number"/> đ</td></tr>
        </c:forEach>
      </tbody>
    </table>
    <t:pagination page="${orders}" baseUrl="/account/orders?status=${status}&"/>
  </c:otherwise>
</c:choose>
</body>
</html>

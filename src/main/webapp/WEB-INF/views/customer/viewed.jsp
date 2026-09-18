<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Sản phẩm đã xem - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:page-header title="Sản phẩm đã xem"/>

<c:choose>
  <c:when test="${empty items}">
    <t:empty-state variant="empty" title="Chưa xem sản phẩm nào" actionHref="/products" actionLabel="Xem sản phẩm"/>
  </c:when>
  <c:otherwise>
    <div class="prod-grid">
      <c:forEach var="h" items="${items}">
        <a class="ss-card" href="${ctx}/products/${h.product.id}" style="color:inherit">
          <img class="prod-thumb" src="${empty h.product.thumbnailUrl?'https://placehold.co/300x300?text=Hoa':h.product.thumbnailUrl}" alt="${h.product.name}">
          <div style="margin-top:var(--space-2);font-weight:var(--fw-semibold)">${h.product.name}</div>
          <div class="price"><fmt:formatNumber value="${h.product.price}" type="number"/> đ</div>
        </a>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>
</body>
</html>

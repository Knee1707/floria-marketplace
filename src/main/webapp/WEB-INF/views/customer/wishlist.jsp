<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Sản phẩm yêu thích - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:page-header title="Sản phẩm yêu thích"/>

<c:choose>
  <c:when test="${empty items}">
    <t:empty-state variant="empty" title="Chưa có sản phẩm yêu thích" actionHref="/products" actionLabel="Khám phá"/>
  </c:when>
  <c:otherwise>
    <div class="prod-grid">
      <c:forEach var="w" items="${items}">
        <div class="ss-card">
          <a href="${ctx}/products/${w.product.id}" style="color:inherit">
            <img class="prod-thumb" src="${empty w.product.thumbnailUrl?'https://placehold.co/300x300?text=Hoa':w.product.thumbnailUrl}" alt="${w.product.name}">
            <div style="margin-top:var(--space-2);font-weight:var(--fw-semibold)">${w.product.name}</div>
            <div class="price"><fmt:formatNumber value="${w.product.price}" type="number"/> đ</div>
          </a>
          <form method="post" action="${ctx}/wishlist/toggle" class="mt-2">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="hidden" name="productId" value="${w.product.id}"/>
            <button class="btn btn-sm btn-outline-danger w-100"><t:icon name="heart" size="16"/> Bỏ thích</button>
          </form>
        </div>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>
</body>
</html>

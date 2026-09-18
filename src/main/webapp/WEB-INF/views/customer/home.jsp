<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Floria - Hoa tươi mỗi ngày</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="ss-card" style="background:linear-gradient(120deg,var(--pink-50),var(--color-surface));margin-bottom:var(--space-6)">
  <h1>Hoa tươi từ các cửa hàng Floria</h1>
  <p class="muted">Chọn hoa cho mọi dịp — giao nhanh, nhiều cửa hàng, nhiều ưu đãi.</p>
  <a class="btn btn-brand btn-min" href="${ctx}/products"><t:icon name="search" size="18"/> Khám phá sản phẩm</a>
</div>

<h2>Sản phẩm bán chạy</h2>
<c:choose>
  <c:when test="${empty topProducts}">
    <t:empty-state variant="empty" title="Chưa có sản phẩm. Hãy chạy seed (V2__seed.sql)." actionHref="/products" actionLabel="Xem sản phẩm"/>
  </c:when>
  <c:otherwise>
    <div class="prod-grid">
      <c:forEach var="p" items="${topProducts}">
        <a class="ss-card" href="${ctx}/products/${p.id}" style="color:inherit">
          <img class="prod-thumb" src="${empty p.thumbnailUrl?'https://placehold.co/300x300?text=Hoa':p.thumbnailUrl}" alt="${p.name}">
          <div style="margin-top:var(--space-2);font-weight:var(--fw-semibold)">${p.name}</div>
          <div class="price"><fmt:formatNumber value="${p.price}" type="number"/> đ</div>
          <div class="muted" style="font-size:var(--text-xs)">Đã bán ${p.soldCount} · ${p.store.name}</div>
        </a>
      </c:forEach>
    </div>
  </c:otherwise>
</c:choose>
</body>
</html>

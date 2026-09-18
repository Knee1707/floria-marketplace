<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Sản phẩm - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:page-header title="Sản phẩm" subtitle="Tìm hoa cho mọi dịp"/>

<form class="d-flex gap-2 mb-4" method="get" action="${ctx}/products">
  <div class="input-group">
    <span class="input-group-text"><t:icon name="search" size="18"/></span>
    <input class="form-control" name="keyword" value="${keyword}" placeholder="Tìm sản phẩm...">
  </div>
  <button class="btn btn-brand btn-min">Tìm</button>
</form>

<c:choose>
  <c:when test="${products.totalElements == 0}">
    <t:empty-state variant="empty" title="Không có sản phẩm phù hợp" actionHref="/products" actionLabel="Xoá lọc"/>
  </c:when>
  <c:otherwise>
    <div class="prod-grid">
      <c:forEach var="p" items="${products.content}">
        <a class="ss-card" href="${ctx}/products/${p.id}" style="color:inherit">
          <img class="prod-thumb" src="${empty p.thumbnailUrl?'https://placehold.co/300x300?text=Hoa':p.thumbnailUrl}" alt="${p.name}">
          <div style="margin-top:var(--space-2);font-weight:var(--fw-semibold)">${p.name}</div>
          <div class="price"><fmt:formatNumber value="${p.price}" type="number"/> đ</div>
          <div class="muted" style="font-size:var(--text-xs)">${p.store.name} · ⭐ ${p.ratingAvg}</div>
        </a>
      </c:forEach>
    </div>
    <t:pagination page="${products}" baseUrl="/products?keyword=${keyword}&"/>
  </c:otherwise>
</c:choose>
</body>
</html>

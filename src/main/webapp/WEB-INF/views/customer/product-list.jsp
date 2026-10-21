<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Sản phẩm - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:page-header title="Sản phẩm" subtitle="Tìm hoa cho mọi dịp"/>

<%-- Tìm kiếm + lọc giá (giữ lại tiêu chí sắp xếp + danh mục đang chọn) --%>
<form class="d-flex gap-2 mb-3 flex-wrap" method="get" action="${ctx}/products">
  <div class="input-group" style="max-width:340px">
    <span class="input-group-text"><t:icon name="search" size="18"/></span>
    <input class="form-control" name="keyword" value="${keyword}" placeholder="Tìm sản phẩm...">
  </div>
  <input class="form-control" type="number" name="minPrice" value="${minPrice}" min="0" step="1000" placeholder="Giá từ (đ)" style="max-width:140px">
  <input class="form-control" type="number" name="maxPrice" value="${maxPrice}" min="0" step="1000" placeholder="đến (đ)" style="max-width:140px">
  <input type="hidden" name="sort" value="${sort}">
  <c:if test="${not empty categoryId}"><input type="hidden" name="categoryId" value="${categoryId}"></c:if>
  <button class="btn btn-brand btn-min"><t:icon name="filter" size="18"/> Lọc</button>
</form>

<%-- Sắp xếp theo 4 tiêu chí (đề bài): mới / bán chạy / đánh giá / yêu thích --%>
<c:set var="qs" value="keyword=${keyword}&categoryId=${categoryId}&minPrice=${minPrice}&maxPrice=${maxPrice}"/>
<div class="seg mb-4">
  <a href="${ctx}/products?${qs}&sort=banchay"  class="${sort=='banchay'?'active':''}">Bán chạy</a>
  <a href="${ctx}/products?${qs}&sort=moi"       class="${sort=='moi'?'active':''}">Mới nhất</a>
  <a href="${ctx}/products?${qs}&sort=danhgia"   class="${sort=='danhgia'?'active':''}">Đánh giá cao</a>
  <a href="${ctx}/products?${qs}&sort=yeuthich"  class="${sort=='yeuthich'?'active':''}">Yêu thích</a>
</div>

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
          <div class="muted d-flex align-items-center gap-1" style="font-size:var(--text-xs)">
            ${p.store.name} · <t:icon name="star" size="13"/> ${p.ratingAvg}
            <c:if test="${p.favoriteCount > 0}"> · <t:icon name="heart" size="13"/> ${p.favoriteCount}</c:if>
          </div>
        </a>
      </c:forEach>
    </div>
    <t:pagination page="${products}" baseUrl="/products?keyword=${keyword}&sort=${sort}&categoryId=${categoryId}&minPrice=${minPrice}&maxPrice=${maxPrice}&"/>
  </c:otherwise>
</c:choose>
</body>
</html>

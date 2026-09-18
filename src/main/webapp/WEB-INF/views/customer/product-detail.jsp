<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head><title>${product.name} - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<div class="row g-4">
  <div class="col-md-5">
    <img class="prod-thumb" src="${empty product.thumbnailUrl?'https://placehold.co/500x500?text=Hoa':product.thumbnailUrl}" alt="${product.name}">
  </div>
  <div class="col-md-7">
    <h1>${product.name}</h1>
    <div class="d-flex align-items-center gap-2 mb-2">
      <t:status-badge value="${product.status}"/>
      <span class="muted">${product.store.name} · ⭐ ${product.ratingAvg} · Đã bán ${product.soldCount}</span>
    </div>
    <div class="price" style="font-size:var(--text-2xl)"><fmt:formatNumber value="${product.price}" type="number"/> đ</div>
    <p class="muted" style="margin-top:var(--space-3)">${product.description}</p>

    <sec:authorize access="isAuthenticated()">
      <div class="d-flex gap-2 mt-3">
        <form method="post" action="${ctx}/cart/add" class="d-flex gap-2">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden" name="productId" value="${product.id}"/>
          <input class="form-control" type="number" name="quantity" value="1" min="1" style="width:90px">
          <button class="btn btn-brand btn-min"><t:icon name="cart" size="18"/> Thêm vào giỏ</button>
        </form>
        <form method="post" action="${ctx}/wishlist/toggle">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden" name="productId" value="${product.id}"/>
          <input type="hidden" name="redirect" value="/products/${product.id}"/>
          <button class="btn btn-min ${followed?'btn-outline-danger':'btn-outline-secondary'}" title="Yêu thích">
            <t:icon name="heart" size="18" label="Yêu thích"/> ${followed?'Đã thích':'Thích'}</button>
        </form>
      </div>
    </sec:authorize>
    <sec:authorize access="isAnonymous()">
      <a class="btn btn-brand btn-min mt-3" href="${ctx}/auth/login">Đăng nhập để mua</a>
    </sec:authorize>
  </div>
</div>

<hr style="margin:var(--space-8) 0">

<h2>Đánh giá sản phẩm</h2>
<c:if test="${not empty reviewOk}"><div class="alert alert-success">${reviewOk}</div></c:if>
<c:if test="${not empty reviewError}"><div class="alert alert-danger">${reviewError}</div></c:if>

<c:if test="${canReview}">
  <t:card title="Viết đánh giá">
    <form method="post" action="${ctx}/reviews">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <input type="hidden" name="productId" value="${product.id}"/>
      <div class="mb-3"><label class="form-label" for="rating">Số sao</label>
        <select class="form-select" id="rating" name="rating" style="width:120px">
          <option value="5">5 ⭐</option><option value="4">4 ⭐</option><option value="3">3 ⭐</option>
          <option value="2">2 ⭐</option><option value="1">1 ⭐</option>
        </select></div>
      <t:form-field name="comment" label="Bình luận" type="textarea" required="true" hint="Tối thiểu 50 ký tự"/>
      <t:form-field name="mediaUrls" label="Link ảnh/video (tuỳ chọn)" hint="Nhiều link cách nhau bởi dấu phẩy"/>
      <button class="btn btn-brand btn-min">Gửi đánh giá</button>
    </form>
  </t:card>
  <div style="height:var(--space-4)"></div>
</c:if>

<c:choose>
  <c:when test="${empty reviews}">
    <t:empty-state variant="empty" title="Chưa có đánh giá nào"/>
  </c:when>
  <c:otherwise>
    <c:forEach var="r" items="${reviews}">
      <div class="ss-card" style="margin-bottom:var(--space-3)">
        <div class="d-flex justify-content-between">
          <b>${r.user.fullName}</b><span>⭐ ${r.rating}</span>
        </div>
        <p style="margin:var(--space-2) 0 0">${r.comment}</p>
      </div>
    </c:forEach>
  </c:otherwise>
</c:choose>
</body>
</html>

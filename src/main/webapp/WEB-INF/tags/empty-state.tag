<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%--
  empty-state.tag — trạng thái màn (rules/frontend/05): loading|empty|error.
  Dùng: <t:empty-state variant="empty" title="Chưa có sản phẩm" actionHref="/vendor/products/new" actionLabel="Thêm sản phẩm"/>
--%>
<%@ attribute name="variant" %>
<%@ attribute name="title" %>
<%@ attribute name="actionHref" %>
<%@ attribute name="actionLabel" %>
<c:choose>
  <c:when test="${variant=='loading'}">
    <div class="ss-card"><div class="ss-skel" style="width:60%"></div><div class="ss-skel"></div><div class="ss-skel" style="width:80%"></div></div>
  </c:when>
  <c:when test="${variant=='error'}">
    <div class="ss-state"><t:icon name="x" size="40" label="Lỗi"/><p>${empty title ? 'Đã có lỗi xảy ra' : title}</p>
      <c:if test="${not empty actionHref}"><a class="btn btn-brand btn-min" href="${pageContext.request.contextPath}${actionHref}">${empty actionLabel?'Thử lại':actionLabel}</a></c:if></div>
  </c:when>
  <c:otherwise>
    <div class="ss-state"><t:icon name="inbox" size="40"/><p>${empty title ? 'Chưa có dữ liệu' : title}</p>
      <c:if test="${not empty actionHref}"><a class="btn btn-brand btn-min" href="${pageContext.request.contextPath}${actionHref}">${actionLabel}</a></c:if></div>
  </c:otherwise>
</c:choose>

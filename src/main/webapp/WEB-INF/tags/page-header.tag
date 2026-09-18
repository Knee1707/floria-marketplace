<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="subtitle" %>
<%--
  page-header.tag — tiêu đề trang + vùng nút (body). rules/frontend/01.
  Dùng: <t:page-header title="Sản phẩm"> <a ...>Thêm</a> </t:page-header>
--%>
<div class="d-flex justify-content-between align-items-center flex-wrap gap-2" style="margin-bottom:var(--space-6)">
  <div>
    <h1 style="margin:0">${title}</h1>
    <c:if test="${not empty subtitle}"><div class="muted">${subtitle}</div></c:if>
  </div>
  <div class="d-flex gap-2"><jsp:doBody/></div>
</div>

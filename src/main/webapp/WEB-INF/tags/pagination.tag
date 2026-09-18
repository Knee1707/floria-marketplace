<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  pagination.tag — phân trang (Spring Page). rules/frontend/05.
  Dùng: <t:pagination page="${page}" baseUrl="/products?keyword=${kw}&"/>
  page: đối tượng có number(0-based), totalPages.
--%>
<%@ attribute name="page" required="true" type="java.lang.Object" %>
<%@ attribute name="baseUrl" required="true" %>
<c:if test="${page.totalPages > 1}">
  <nav aria-label="Phân trang" class="d-flex justify-content-center" style="margin-top:var(--space-6)">
    <ul class="pagination">
      <li class="page-item ${page.first?'disabled':''}">
        <a class="page-link" href="${pageContext.request.contextPath}${baseUrl}page=${page.number-1}">Trước</a></li>
      <c:forEach var="i" begin="0" end="${page.totalPages-1}">
        <li class="page-item ${i==page.number?'active':''}">
          <a class="page-link" href="${pageContext.request.contextPath}${baseUrl}page=${i}">${i+1}</a></li>
      </c:forEach>
      <li class="page-item ${page.last?'disabled':''}">
        <a class="page-link" href="${pageContext.request.contextPath}${baseUrl}page=${page.number+1}">Sau</a></li>
    </ul>
  </nav>
</c:if>

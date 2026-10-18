<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.4 Kiểm duyệt sản phẩm (cấm / mở lại). @owner: TV3 --%>
<html>
<head><title>Sản phẩm - Quản trị Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:admin-shell active="products">
  <t:page-header title="Sản phẩm"
                 subtitle="${area == 'manager' ? 'Sản phẩm của các cửa hàng bạn phụ trách' : 'Kiểm duyệt sản phẩm toàn sàn, lọc theo từng cửa hàng'}"/>

  <form class="ss-toolbar" method="get" action="${ctx}/${area}/products" role="search">
    <label class="visually-hidden" for="kw">Tên sản phẩm</label>
    <input class="form-control" id="kw" name="keyword" value="${fn:escapeXml(keyword)}" maxlength="100" placeholder="Tên sản phẩm">
    <label class="visually-hidden" for="f_store">Cửa hàng</label>
    <select class="form-select" id="f_store" name="storeId" style="width:auto">
      <option value="">Mọi cửa hàng</option>
      <c:forEach var="s" items="${stores}">
        <option value="${s.id}" ${s.id == storeId ? 'selected' : ''}><c:out value="${s.name}"/></option>
      </c:forEach>
    </select>
    <button class="btn btn-brand btn-min" type="submit"><t:icon name="filter" size="18"/> Lọc</button>
    <c:if test="${not empty keyword or not empty storeId}"><a class="btn btn-outline-secondary btn-min" href="${ctx}/${area}/products">Xoá lọc</a></c:if>
  </form>

  <c:choose>
    <c:when test="${products.totalElements == 0}">
      <t:empty-state variant="empty" title="Không có sản phẩm phù hợp" actionHref="/${area}/products" actionLabel="Xem tất cả"/>
    </c:when>
    <c:otherwise>
      <div class="ss-table-wrap">
        <table class="ss-table">
          <thead><tr><th>Sản phẩm</th><th>Cửa hàng</th><th>Danh mục</th><th class="num">Giá</th><th class="num">Đã bán</th><th>Trạng thái</th><th class="actions">Thao tác</th></tr></thead>
          <tbody>
          <c:forEach var="p" items="${products.content}">
            <tr>
              <td><a href="${ctx}/products/${p.id}" style="font-weight:var(--fw-semibold)"><c:out value="${p.name}"/></a></td>
              <td><c:out value="${p.store.name}"/></td>
              <td><c:out value="${empty p.category ? '—' : p.category.name}"/></td>
              <td class="num"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/> đ</td>
              <td class="num"><fmt:formatNumber value="${p.soldCount}" type="number"/></td>
              <td><t:status-badge value="${p.status}"/></td>
              <td class="actions">
                <c:choose>
                  <c:when test="${p.status == 'BANNED'}">
                    <form method="post" action="${ctx}/${area}/products/${p.id}/status" class="d-inline"
                          data-confirm="Mở lại sản phẩm ${fn:escapeXml(p.name)}?">
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                      <input type="hidden" name="status" value="ACTIVE"/>
                      <button class="btn btn-sm btn-outline-success" type="submit"><t:icon name="check" size="16"/> Mở lại</button>
                    </form>
                  </c:when>
                  <c:otherwise>
                    <form method="post" action="${ctx}/${area}/products/${p.id}/status" class="d-inline"
                          data-confirm="Cấm sản phẩm ${fn:escapeXml(p.name)}? Sản phẩm sẽ không còn hiển thị với khách.">
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                      <input type="hidden" name="status" value="BANNED"/>
                      <button class="btn btn-sm btn-outline-danger" type="submit"><t:icon name="ban" size="16"/> Cấm</button>
                    </form>
                  </c:otherwise>
                </c:choose>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
      <t:pagination page="${products}" baseUrl="/${area}/products?keyword=${keywordParam}&storeId=${storeId}&"/>
    </c:otherwise>
  </c:choose>
</t:admin-shell>
</body>
</html>

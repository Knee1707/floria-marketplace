<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Quản lý sản phẩm - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:vendor-shell active="products">
  <t:page-header title="Sản phẩm của cửa hàng" subtitle="Quản lý danh mục hoa đang kinh doanh tại ${store.name}">
    <a class="btn btn-brand btn-min d-inline-flex align-items-center gap-2" href="${ctx}/vendor/products/new">
      <t:icon name="add" size="18"/> Thêm sản phẩm
    </a>
  </t:page-header>

  <c:choose>
    <c:when test="${page.totalElements == 0}">
      <t:empty-state variant="empty"
                     title="Cửa hàng chưa có sản phẩm nào"
                     actionHref="/vendor/products/new"
                     actionLabel="Thêm sản phẩm đầu tiên"/>
    </c:when>
    <c:otherwise>
      <div class="table-responsive">
        <table class="ss-table">
          <thead>
            <tr>
              <th>Ảnh</th>
              <th>Tên sản phẩm</th>
              <th>Danh mục</th>
              <th class="num">Giá bán</th>
              <th class="num">Tồn kho</th>
              <th class="num">Đã bán</th>
              <th>Trạng thái</th>
              <th class="num">Thao tác</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="p" items="${page.content}">
              <tr>
                <td>
                  <img src="${empty p.thumbnailUrl ? 'https://images.unsplash.com/photo-1561181286-d3fee7d55364?auto=format&fit=crop&w=120&q=80' : p.thumbnailUrl}"
                       alt="${p.name}"
                       style="width:48px;height:48px;object-fit:cover;border-radius:var(--radius-sm)">
                </td>
                <td>
                  <div style="font-weight:var(--fw-semibold)">${p.name}</div>
                  <div class="muted" style="font-size:var(--text-xs)">Mã SP: #${p.id}</div>
                </td>
                <td>${not empty p.category ? p.category.name : 'Chưa phân loại'}</td>
                <td class="num price"><fmt:formatNumber value="${p.price}" type="number"/> đ</td>
                <td class="num">${p.stock}</td>
                <td class="num">${p.soldCount}</td>
                <td><t:status-badge value="${p.status}"/></td>
                <td class="num">
                  <div class="d-inline-flex gap-2">
                    <a class="btn btn-sm btn-outline-secondary d-inline-flex align-items-center gap-1"
                       href="${ctx}/vendor/products/${p.id}/edit">
                      <t:icon name="edit" size="16"/> Sửa
                    </a>
                    <c:if test="${p.status != 'BANNED'}">
                      <form method="post" action="${ctx}/vendor/products/${p.id}/hide" class="d-inline m-0">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button type="submit" class="btn btn-sm btn-outline-secondary d-inline-flex align-items-center gap-1">
                          <t:icon name="eye" size="16"/> ${p.status == 'HIDDEN' ? 'Hiện bán' : 'Ẩn SP'}
                        </button>
                      </form>
                    </c:if>
                  </div>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      <t:pagination page="${page}" baseUrl="/vendor/products?"/>
    </c:otherwise>
  </c:choose>
</t:vendor-shell>
</body>
</html>

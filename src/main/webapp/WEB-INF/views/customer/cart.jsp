<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Giỏ hàng - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:page-header title="Giỏ hàng"/>

<c:choose>
  <c:when test="${empty cart.items}">
    <t:empty-state variant="empty" title="Giỏ hàng trống" actionHref="/products" actionLabel="Mua sắm ngay"/>
  </c:when>
  <c:otherwise>
    <t:card>
      <table class="ss-table">
        <thead><tr><th>Sản phẩm</th><th>Giá</th><th>Số lượng</th><th class="num">Tạm tính</th><th></th></tr></thead>
        <tbody>
          <c:forEach var="i" items="${cart.items}">
            <tr>
              <td>${i.product.name}<div class="muted" style="font-size:var(--text-xs)">${i.product.store.name}</div></td>
              <td class="num"><fmt:formatNumber value="${i.product.price}" type="number"/> đ</td>
              <td>
                <form method="post" action="${ctx}/cart/update" class="d-flex gap-1">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <input type="hidden" name="cartItemId" value="${i.id}"/>
                  <input class="form-control form-control-sm" type="number" name="quantity" value="${i.quantity}" min="1" style="width:70px">
                  <button class="btn btn-sm btn-outline-secondary">Cập nhật</button>
                </form>
              </td>
              <td class="num"><fmt:formatNumber value="${i.product.price * i.quantity}" type="number"/> đ</td>
              <td>
                <form method="post" action="${ctx}/cart/remove">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <input type="hidden" name="cartItemId" value="${i.id}"/>
                  <button class="btn btn-sm btn-outline-danger" title="Xoá"><t:icon name="delete" size="16" label="Xoá"/></button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      <div class="d-flex justify-content-between align-items-center" style="margin-top:var(--space-4)">
        <div>Tạm tính: <span class="price"><fmt:formatNumber value="${subtotal}" type="number"/> đ</span></div>
        <a class="btn btn-brand btn-min" href="${ctx}/checkout"><t:icon name="check" size="18"/> Thanh toán</a>
      </div>
    </t:card>
  </c:otherwise>
</c:choose>
</body>
</html>

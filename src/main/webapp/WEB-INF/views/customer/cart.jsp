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
      <%-- Form thanh toán (GET /checkout) đặt ở cuối; các checkbox dòng gắn vào form này qua thuộc tính form="checkoutForm"
           nên KHÔNG lồng form trong form (cùng với form Cập nhật/Xoá của mỗi dòng). --%>
      <table class="ss-table">
        <thead><tr>
          <th style="width:40px"><input type="checkbox" id="selAll" class="form-check-input" checked aria-label="Chọn tất cả"></th>
          <th>Sản phẩm</th><th>Giá</th><th>Số lượng</th><th class="num">Tạm tính</th><th></th>
        </tr></thead>
        <tbody>
          <c:forEach var="i" items="${cart.items}">
            <tr>
              <td><input type="checkbox" class="form-check-input co-item" name="itemIds" value="${i.id}"
                         form="checkoutForm" checked data-amount="${i.product.price * i.quantity}"
                         aria-label="Chọn ${i.product.name}"></td>
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
      <form id="checkoutForm" method="get" action="${ctx}/checkout"
            class="d-flex justify-content-between align-items-center" style="margin-top:var(--space-4)">
        <div>Đã chọn: <span class="price" id="selSubtotal"><fmt:formatNumber value="${subtotal}" type="number"/> đ</span></div>
        <button type="submit" class="btn btn-brand btn-min"><t:icon name="check" size="18"/> Thanh toán</button>
      </form>
    </t:card>

    <script>
    (function () {
      var all = document.getElementById('selAll');
      var boxes = Array.prototype.slice.call(document.querySelectorAll('.co-item'));
      var out = document.getElementById('selSubtotal');
      function recompute() {
        var sum = boxes.filter(function (b) { return b.checked; })
                       .reduce(function (s, b) { return s + Number(b.dataset.amount || 0); }, 0);
        out.textContent = sum.toLocaleString('en-US') + ' đ';
        all.checked = boxes.length > 0 && boxes.every(function (b) { return b.checked; });
      }
      all.addEventListener('change', function () {
        boxes.forEach(function (b) { b.checked = all.checked; });
        recompute();
      });
      boxes.forEach(function (b) { b.addEventListener('change', recompute); });
      document.getElementById('checkoutForm').addEventListener('submit', function (e) {
        if (!boxes.some(function (b) { return b.checked; })) {
          e.preventDefault();
          alert('Vui lòng chọn ít nhất một sản phẩm để thanh toán.');
        }
      });
      recompute();
    })();
    </script>
  </c:otherwise>
</c:choose>
</body>
</html>

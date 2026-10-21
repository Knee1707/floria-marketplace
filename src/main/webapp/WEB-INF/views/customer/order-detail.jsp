<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Chi tiết đơn ${order.code} - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:page-header title="Đơn ${order.code}" subtitle="Chi tiết đơn hàng & đánh giá"/>

<c:if test="${not empty toast}">
  <div class="ss-toast success" role="status"><t:icon name="check" size="18"/> <span><c:out value="${toast}"/></span></div>
</c:if>
<c:if test="${not empty toastError}">
  <div class="ss-toast danger" role="alert"><t:icon name="x" size="18"/> <span><c:out value="${toastError}"/></span></div>
</c:if>

<div class="row g-4">
  <div class="col-lg-7">
    <t:card title="Thông tin đơn">
      <div class="d-flex justify-content-between mb-2"><span class="muted">Cửa hàng</span><span>${order.store.name}</span></div>
      <div class="d-flex justify-content-between mb-2"><span class="muted">Trạng thái</span><t:status-badge value="${order.status}"/></div>
      <div class="d-flex justify-content-between mb-2"><span class="muted">Shipper</span>
        <span>${not empty order.shipper ? order.shipper.fullName : 'Chưa phân công'}</span></div>
      <div class="d-flex justify-content-between mb-2"><span class="muted">Thanh toán</span><span>${order.paymentMethod}</span></div>
      <div class="d-flex justify-content-between mb-2"><span class="muted">Địa chỉ nhận</span>
        <span class="text-end" style="max-width:60%"><c:out value="${order.shippingAddress}"/></span></div>
      <hr>
      <table class="ss-table">
        <thead><tr><th>Sản phẩm</th><th class="num">SL</th><th class="num">Đơn giá</th><th class="num">Thành tiền</th></tr></thead>
        <tbody>
          <c:forEach var="it" items="${order.items}">
            <tr>
              <td>${it.product.name}</td>
              <td class="num">${it.quantity}</td>
              <td class="num"><fmt:formatNumber value="${it.unitPrice}" type="number"/> đ</td>
              <td class="num"><fmt:formatNumber value="${it.unitPrice * it.quantity}" type="number"/> đ</td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
      <div class="d-flex justify-content-between mt-3"><span class="muted">Tạm tính</span><span><fmt:formatNumber value="${order.subtotal}" type="number"/> đ</span></div>
      <div class="d-flex justify-content-between"><span class="muted">Phí ship</span><span><fmt:formatNumber value="${order.shippingFee}" type="number"/> đ</span></div>
      <div class="d-flex justify-content-between"><span class="muted">Giảm giá</span><span>- <fmt:formatNumber value="${order.discount}" type="number"/> đ</span></div>
      <div class="d-flex justify-content-between fw-bold"><span>Tổng cộng</span><span class="price"><fmt:formatNumber value="${order.total}" type="number"/> đ</span></div>
      <div class="mt-4"><a class="btn btn-outline-secondary btn-min" href="${ctx}/account/orders"><t:icon name="arrow-left" size="16"/> Về danh sách đơn</a></div>
    </t:card>
  </div>

  <div class="col-lg-5">
    <c:choose>
      <c:when test="${order.status == 'DELIVERED'}">
        <%-- ===== Đánh giá CỬA HÀNG ===== --%>
        <t:card title="Đánh giá cửa hàng">
          <p class="muted" style="margin-bottom:var(--space-3)">${order.store.name}</p>
          <form method="post" action="${ctx}/account/orders/${order.id}/review">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <input type="hidden" name="target" value="SHOP"/>
            <div class="star-rate" role="radiogroup" aria-label="Chọn số sao cho cửa hàng">
              <c:forEach begin="1" end="5" var="n"><c:set var="s" value="${6 - n}"/>
                <input type="radio" id="shop-s${s}" name="rating" value="${s}" required
                       ${reviews.SHOP.rating == s ? 'checked' : ''}>
                <label for="shop-s${s}" title="${s} sao">&#9733;</label>
              </c:forEach>
            </div>
            <textarea class="form-control mt-3" name="comment" rows="3"
                      placeholder="Nhận xét về cửa hàng (tuỳ chọn)"><c:out value="${reviews.SHOP.comment}"/></textarea>
            <button class="btn btn-brand btn-min mt-3" type="submit">
              <t:icon name="check" size="16"/> ${empty reviews.SHOP ? 'Gửi đánh giá' : 'Cập nhật đánh giá'}
            </button>
          </form>
        </t:card>

        <div style="height:var(--space-4)"></div>

        <%-- ===== Đánh giá SHIPPER (nếu đơn đã có shipper) ===== --%>
        <c:choose>
          <c:when test="${not empty order.shipper}">
            <t:card title="Đánh giá shipper">
              <p class="muted" style="margin-bottom:var(--space-3)">${order.shipper.fullName}</p>
              <form method="post" action="${ctx}/account/orders/${order.id}/review">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" name="target" value="SHIPPER"/>
                <div class="star-rate" role="radiogroup" aria-label="Chọn số sao cho shipper">
                  <c:forEach begin="1" end="5" var="n"><c:set var="s" value="${6 - n}"/>
                    <input type="radio" id="ship-s${s}" name="rating" value="${s}" required
                           ${reviews.SHIPPER.rating == s ? 'checked' : ''}>
                    <label for="ship-s${s}" title="${s} sao">&#9733;</label>
                  </c:forEach>
                </div>
                <textarea class="form-control mt-3" name="comment" rows="3"
                          placeholder="Nhận xét về shipper (tuỳ chọn)"><c:out value="${reviews.SHIPPER.comment}"/></textarea>
                <button class="btn btn-brand btn-min mt-3" type="submit">
                  <t:icon name="check" size="16"/> ${empty reviews.SHIPPER ? 'Gửi đánh giá' : 'Cập nhật đánh giá'}
                </button>
              </form>
            </t:card>
          </c:when>
          <c:otherwise>
            <t:card title="Đánh giá shipper">
              <p class="muted m-0">Đơn này chưa gán shipper nên chưa thể đánh giá.</p>
            </t:card>
          </c:otherwise>
        </c:choose>
      </c:when>
      <c:otherwise>
        <t:card title="Đánh giá">
          <t:empty-state variant="empty" title="Chỉ đánh giá khi đơn đã giao"/>
          <p class="muted text-center">Bạn có thể đánh giá cửa hàng và shipper sau khi đơn ở trạng thái Đã giao.</p>
        </t:card>
      </c:otherwise>
    </c:choose>
  </div>
</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Đơn chờ nhận - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:shipper-shell active="available">
  <t:page-header title="Đơn chờ nhận"
                 subtitle="Đơn đã được cửa hàng xác nhận và chưa có shipper. Bấm Nhận đơn để lấy hàng và giao."/>

  <c:choose>
    <c:when test="${empty orders}">
      <t:empty-state variant="empty" title="Hiện chưa có đơn nào chờ nhận"/>
    </c:when>
    <c:otherwise>
      <div class="d-flex flex-column gap-3">
        <c:forEach var="o" items="${orders}">
          <t:card>
            <div class="d-flex justify-content-between align-items-start flex-wrap gap-2"
                 style="padding-bottom:var(--space-3);border-bottom:1px solid var(--color-border)">
              <div>
                <div class="d-flex align-items-center gap-2">
                  <span style="font-weight:var(--fw-bold);font-size:var(--text-xl)">Đơn #${o.code}</span>
                  <t:status-badge value="${o.status}"/>
                  <span class="ss-badge neutral">${o.paymentMethod}</span>
                </div>
                <div class="muted" style="font-size:var(--text-sm);margin-top:var(--space-1)">
                  Lấy hàng tại: <b>${o.store.name}</b> · Giao tới: <b>${o.buyer.fullName}</b>
                </div>
                <div style="font-size:var(--text-sm);margin-top:var(--space-1)">
                  Địa chỉ nhận: <b>${empty o.shippingAddress ? 'Liên hệ người nhận' : o.shippingAddress}</b>
                </div>
              </div>
              <div class="text-end">
                <div class="price" style="font-size:var(--text-xl)">
                  <fmt:formatNumber value="${o.total}" type="number"/> đ
                </div>
                <div class="muted" style="font-size:var(--text-xs)">
                  Phí vận chuyển: <fmt:formatNumber value="${o.shippingFee}" type="number"/> đ
                </div>
              </div>
            </div>

            <div style="margin:var(--space-3) 0">
              <c:forEach var="it" items="${o.items}">
                <div class="d-flex justify-content-between" style="font-size:var(--text-sm);padding:2px 0">
                  <span>${it.product.name} <span class="muted">x${it.quantity}</span></span>
                  <span><fmt:formatNumber value="${it.unitPrice * it.quantity}" type="number"/> đ</span>
                </div>
              </c:forEach>
            </div>

            <div class="d-flex justify-content-end"
                 style="padding-top:var(--space-3);border-top:1px solid var(--color-border)">
              <form method="post" action="${ctx}/shipper/orders/${o.id}/claim" class="m-0">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <button type="submit" class="btn btn-brand btn-min d-inline-flex align-items-center gap-2">
                  <t:icon name="package" size="18"/> Nhận đơn
                </button>
              </form>
            </div>
          </t:card>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>
</t:shipper-shell>
</body>
</html>

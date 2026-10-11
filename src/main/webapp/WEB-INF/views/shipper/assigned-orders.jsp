<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Đơn giao vận được phân công - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:shipper-shell active="orders">
  <t:page-header title="Đơn hàng được phân công"
                 subtitle="Cập nhật trạng thái nhận hàng và giao hàng theo thời gian thực (F5.1, F5.2)"/>

  <div class="row g-3" style="margin-bottom:var(--space-6)">
    <div class="col-sm-4">
      <t:stat-tile label="Chờ nhận giao (ASSIGNED)" value="${assignedCount}" icon="package"/>
    </div>
    <div class="col-sm-4">
      <t:stat-tile label="Đang giao (DELIVERING)" value="${deliveringCount}" icon="truck"/>
    </div>
    <div class="col-sm-4">
      <t:stat-tile label="Đã giao tháng này" value="${deliveredThisMonth}" icon="check"/>
    </div>
  </div>

  <%-- Bộ lọc theo trạng thái giao vận --%>
  <div class="d-flex gap-2 flex-wrap" style="margin-bottom:var(--space-4)">
    <a class="btn btn-sm ${empty selectedStatus ? 'btn-brand' : 'btn-outline-secondary'}"
       href="${ctx}/shipper/orders">Tất cả</a>
    <c:forEach var="st" items="${statuses}">
      <a class="btn btn-sm ${selectedStatus == st ? 'btn-brand' : 'btn-outline-secondary'}"
         href="${ctx}/shipper/orders?status=${st}">
        <t:status-badge value="${st}"/>
      </a>
    </c:forEach>
  </div>

  <c:choose>
    <c:when test="${empty assignments}">
      <t:empty-state variant="empty" title="Chưa có đơn hàng nào được phân công ở trạng thái này"/>
    </c:when>
    <c:otherwise>
      <div class="d-flex flex-column gap-3">
        <c:forEach var="a" items="${assignments}">
          <c:set var="o" value="${a.order}"/>
          <t:card>
            <div class="d-flex justify-content-between align-items-start flex-wrap gap-2"
                 style="padding-bottom:var(--space-3);border-bottom:1px solid var(--color-border)">
              <div>
                <div class="d-flex align-items-center gap-2">
                  <span style="font-weight:var(--fw-bold);font-size:var(--text-xl)">Đơn #${o.code}</span>
                  <t:status-badge value="${a.status}"/>
                  <span class="ss-badge neutral">${o.paymentMethod}</span>
                </div>
                <div class="muted" style="font-size:var(--text-sm);margin-top:var(--space-1)">
                  Lấy hàng tại: <b>${o.store.name}</b> · Giao tới: <b>${o.buyer.fullName}</b> (${o.buyer.phone})
                </div>
                <div style="font-size:var(--text-sm);margin-top:var(--space-1)">
                  Địa chỉ nhận: <b>${empty o.shippingAddress ? 'Liên hệ người nhận' : o.shippingAddress}</b>
                </div>
              </div>
              <div class="text-end">
                <div class="price" style="font-size:var(--text-xl)">
                  Thu hộ / Giá trị: <fmt:formatNumber value="${o.total}" type="number"/> đ
                </div>
                <div class="muted" style="font-size:var(--text-xs)">
                  Phí vận chuyển: <fmt:formatNumber value="${o.shippingFee}" type="number"/> đ
                </div>
              </div>
            </div>

            <%-- Sản phẩm trong đơn --%>
            <div style="margin:var(--space-3) 0">
              <c:forEach var="it" items="${o.items}">
                <div class="d-flex justify-content-between" style="font-size:var(--text-sm);padding:2px 0">
                  <span>${it.product.name} <span class="muted">x${it.quantity}</span></span>
                  <span><fmt:formatNumber value="${it.unitPrice * it.quantity}" type="number"/> đ</span>
                </div>
              </c:forEach>
            </div>

            <%-- Nút cập nhật trạng thái giao hàng (F5.2) --%>
            <div class="d-flex justify-content-end gap-2 flex-wrap"
                 style="padding-top:var(--space-3);border-top:1px solid var(--color-border)">
              <c:if test="${a.status == 'ASSIGNED'}">
                <form method="post" action="${ctx}/shipper/assignments/${a.id}/status" class="m-0">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <input type="hidden" name="status" value="DELIVERING"/>
                  <button type="submit" class="btn btn-brand btn-min d-inline-flex align-items-center gap-2">
                    <t:icon name="truck" size="18"/> Bắt đầu giao hàng
                  </button>
                </form>
              </c:if>

              <c:if test="${a.status == 'DELIVERING'}">
                <form method="post" action="${ctx}/shipper/assignments/${a.id}/status" class="m-0">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <input type="hidden" name="status" value="DELIVERED"/>
                  <button type="submit" class="btn btn-brand btn-min d-inline-flex align-items-center gap-2">
                    <t:icon name="check" size="18"/> Xác nhận đã giao thành công
                  </button>
                </form>

                <form method="post" action="${ctx}/shipper/assignments/${a.id}/status" class="m-0">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <input type="hidden" name="status" value="FAILED"/>
                  <button type="submit" class="btn btn-outline-danger btn-min d-inline-flex align-items-center gap-2">
                    <t:icon name="x" size="18"/> Báo giao thất bại
                  </button>
                </form>
              </c:if>
            </div>
          </t:card>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>
</t:shipper-shell>
</body>
</html>

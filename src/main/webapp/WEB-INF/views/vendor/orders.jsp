<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Quản lý đơn hàng - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:vendor-shell active="orders">
  <t:page-header title="Đơn hàng của cửa hàng"
                 subtitle="Quản lý luồng 7 trạng thái đơn hàng và phân công Shipper giao vận"/>

  <%-- Bộ lọc theo 7 trạng thái đơn hàng (F4.4) --%>
  <div class="d-flex gap-2 flex-wrap" style="margin-bottom:var(--space-4)">
    <a class="btn btn-sm ${empty selectedStatus ? 'btn-brand' : 'btn-outline-secondary'}"
       href="${ctx}/vendor/orders">Tất cả</a>
    <c:forEach var="st" items="${statuses}">
      <a class="btn btn-sm ${selectedStatus == st ? 'btn-brand' : 'btn-outline-secondary'}"
         href="${ctx}/vendor/orders?status=${st}">
        <t:status-badge value="${st}"/>
      </a>
    </c:forEach>
  </div>

  <c:choose>
    <c:when test="${empty orders}">
      <t:empty-state variant="empty" title="Không có đơn hàng nào ở trạng thái này"/>
    </c:when>
    <c:otherwise>
      <div class="d-flex flex-column gap-3">
        <c:forEach var="o" items="${orders}">
          <t:card>
            <div class="d-flex justify-content-between align-items-start flex-wrap gap-2"
                 style="padding-bottom:var(--space-3);border-bottom:1px solid var(--color-border)">
              <div>
                <div class="d-flex align-items-center gap-2">
                  <span style="font-weight:var(--fw-bold);font-size:var(--text-xl)">#${o.code}</span>
                  <t:status-badge value="${o.status}"/>
                  <span class="ss-badge neutral">${o.paymentMethod}</span>
                </div>
                <div class="muted" style="font-size:var(--text-sm);margin-top:var(--space-1)">
                  Khách hàng: <b>${o.buyer.fullName}</b> (${o.buyer.email}) · Địa chỉ giao: ${empty o.shippingAddress ? 'Nhận tại cửa hàng' : o.shippingAddress}
                </div>
              </div>
              <div class="text-end">
                <div class="price" style="font-size:var(--text-xl)">
                  <fmt:formatNumber value="${o.total}" type="number"/> đ
                </div>
                <div class="muted" style="font-size:var(--text-xs)">
                  Shipper: <b>${not empty o.shipper ? o.shipper.fullName : 'Chưa phân công'}</b>
                </div>
              </div>
            </div>

            <%-- Danh sách sản phẩm trong đơn --%>
            <div style="margin:var(--space-3) 0">
              <c:forEach var="it" items="${o.items}">
                <div class="d-flex justify-content-between align-items-center" style="padding:var(--space-1) 0;font-size:var(--text-sm)">
                  <span>${it.product.name} <span class="muted">x${it.quantity}</span></span>
                  <span><fmt:formatNumber value="${it.unitPrice * it.quantity}" type="number"/> đ</span>
                </div>
              </c:forEach>
            </div>

            <%-- Khu vực thao tác chuyển trạng thái & gán Shipper --%>
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2"
                 style="padding-top:var(--space-3);border-top:1px solid var(--color-border)">
              <div class="d-flex gap-2 flex-wrap">
                <c:if test="${o.status == 'NEW'}">
                  <form method="post" action="${ctx}/vendor/orders/${o.id}/status" class="m-0">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="newStatus" value="CONFIRMED"/>
                    <button type="submit" class="btn btn-sm btn-brand d-inline-flex align-items-center gap-1">
                      <t:icon name="check" size="16"/> Xác nhận đơn
                    </button>
                  </form>
                </c:if>

                <c:if test="${o.status == 'CONFIRMED'}">
                  <form method="post" action="${ctx}/vendor/orders/${o.id}/status" class="m-0">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="newStatus" value="PICKED_UP"/>
                    <button type="submit" class="btn btn-sm btn-brand d-inline-flex align-items-center gap-1">
                      <t:icon name="package" size="16"/> Đã chuẩn bị xong (Chờ lấy)
                    </button>
                  </form>
                </c:if>

                <c:if test="${o.status == 'NEW' || o.status == 'CONFIRMED'}">
                  <form method="post" action="${ctx}/vendor/orders/${o.id}/status" class="m-0">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <input type="hidden" name="newStatus" value="CANCELLED"/>
                    <button type="submit" class="btn btn-sm btn-outline-danger d-inline-flex align-items-center gap-1">
                      <t:icon name="x" size="16"/> Huỷ đơn
                    </button>
                  </form>
                </c:if>
              </div>

              <%-- Form phân công Shipper (cho đơn CONFIRMED hoặc PICKED_UP) --%>
              <c:if test="${o.status == 'NEW' || o.status == 'CONFIRMED' || o.status == 'PICKED_UP'}">
                <form method="post" action="${ctx}/vendor/orders/${o.id}/assign-shipper" class="d-flex align-items-center gap-2 m-0">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <select name="shipperId" class="form-select form-select-sm" style="width:auto" required>
                    <option value="">-- Chọn Shipper giao --</option>
                    <c:forEach var="sh" items="${shippers}">
                      <option value="${sh.id}" ${not empty o.shipper && o.shipper.id == sh.id ? 'selected' : ''}>
                        ${sh.fullName} (${sh.phone})
                      </option>
                    </c:forEach>
                  </select>
                  <button type="submit" class="btn btn-sm btn-outline-secondary d-inline-flex align-items-center gap-1">
                    <t:icon name="truck" size="16"/> Phân công Shipper
                  </button>
                </form>
              </c:if>
            </div>
          </t:card>
        </c:forEach>
      </div>
    </c:otherwise>
  </c:choose>
</t:vendor-shell>
</body>
</html>

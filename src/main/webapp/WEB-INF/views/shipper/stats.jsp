<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Thống kê giao vận - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:shipper-shell active="stats">
  <t:page-header title="Thống kê hiệu suất giao vận"
                 subtitle="Tổng hợp số liệu đơn hàng đã nhận, giao thành công và cước phí vận chuyển (F5.3)"/>

  <div class="row g-3" style="margin-bottom:var(--space-6)">
    <div class="col-sm-6 col-xl-3">
      <t:stat-tile label="Tổng đơn được phân công" value="${totalCount}" icon="package"/>
    </div>
    <div class="col-sm-6 col-xl-3">
      <t:stat-tile label="Đã giao trong tháng" value="${deliveredThisMonth}" icon="check"/>
    </div>
    <div class="col-sm-6 col-xl-3">
      <t:stat-tile label="Tỷ lệ giao thành công" value="${successRate}%" icon="chart"/>
    </div>
    <div class="col-sm-6 col-xl-3">
      <fmt:formatNumber value="${totalShippingFeeEarned}" type="number" var="shipFeeFmt"/>
      <t:stat-tile label="Tổng phí vận chuyển (đ)" value="${shipFeeFmt}" icon="truck"/>
    </div>
  </div>

  <div class="row g-4">
    <div class="col-lg-4">
      <t:card title="Phân bổ theo trạng thái">
        <div class="d-flex flex-column gap-3">
          <div class="d-flex justify-content-between align-items-center">
            <span>Chờ nhận giao (ASSIGNED)</span>
            <span class="ss-badge info">${assignedCount} đơn</span>
          </div>
          <div class="d-flex justify-content-between align-items-center">
            <span>Đang trên đường giao (DELIVERING)</span>
            <span class="ss-badge warning">${deliveringCount} đơn</span>
          </div>
          <div class="d-flex justify-content-between align-items-center">
            <span>Đã giao thành công (DELIVERED)</span>
            <span class="ss-badge success">${deliveredCount} đơn</span>
          </div>
          <div class="d-flex justify-content-between align-items-center">
            <span>Giao thất bại / Hoàn (FAILED)</span>
            <span class="ss-badge danger">${failedCount} đơn</span>
          </div>
          <div style="padding-top:var(--space-3);border-top:1px solid var(--color-border)" class="d-flex justify-content-between align-items-center">
            <span style="font-weight:var(--fw-semibold)">Tổng giá trị hàng đã giao:</span>
            <span class="price"><fmt:formatNumber value="${totalDeliveredValue}" type="number"/> đ</span>
          </div>
        </div>
      </t:card>
    </div>

    <div class="col-lg-8">
      <t:card title="Lịch sử các đơn đã giao thành công gần đây">
        <c:choose>
          <c:when test="${empty recentDelivered}">
            <t:empty-state variant="empty" title="Chưa có đơn hàng nào giao hoàn tất"/>
          </c:when>
          <c:otherwise>
            <div class="table-responsive">
              <table class="ss-table">
                <thead>
                  <tr>
                    <th>Mã đơn</th>
                    <th>Cửa hàng</th>
                    <th>Khách nhận</th>
                    <th class="num">Phí ship</th>
                    <th class="num">Tổng đơn</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="a" items="${recentDelivered}">
                    <tr>
                      <td style="font-weight:var(--fw-semibold)">#${a.order.code}</td>
                      <td>${a.order.store.name}</td>
                      <td>${a.order.buyer.fullName}</td>
                      <td class="num"><fmt:formatNumber value="${a.order.shippingFee}" type="number"/> đ</td>
                      <td class="num price"><fmt:formatNumber value="${a.order.total}" type="number"/> đ</td>
                      <td><t:status-badge value="${a.status}"/></td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:otherwise>
        </c:choose>
      </t:card>
    </div>
  </div>
</t:shipper-shell>
</body>
</html>

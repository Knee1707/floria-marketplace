<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Ví cửa hàng - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:vendor-shell active="wallet">
  <t:page-header title="Ví cửa hàng & Lịch sử giao dịch"
                 subtitle="Theo dõi dòng tiền doanh thu, hoa hồng sàn và rút tiền về ngân hàng (F4.6)"/>

  <div class="row g-3" style="margin-bottom:var(--space-6)">
    <div class="col-md-4">
      <fmt:formatNumber value="${summary.walletBalance}" type="number" var="balFmt"/>
      <t:stat-tile label="Số dư khả dụng (đ)" value="${balFmt}" icon="inbox"/>
    </div>
    <div class="col-md-4">
      <fmt:formatNumber value="${summary.netRevenue}" type="number" var="netFmt"/>
      <t:stat-tile label="Tổng doanh thu ròng (đ)" value="${netFmt}" icon="chart"/>
    </div>
    <div class="col-md-4">
      <fmt:formatNumber value="${summary.totalWithdrawn}" type="number" var="withdrawnFmt"/>
      <t:stat-tile label="Tổng đã rút (đ)" value="${withdrawnFmt}" icon="check"/>
    </div>
  </div>

  <div class="row g-4">
    <%-- Form tạo lệnh rút tiền (WITHDRAW) --%>
    <div class="col-lg-4">
      <t:card title="Tạo yêu cầu rút tiền">
        <form method="post" action="${ctx}/vendor/wallet/withdraw">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

          <t:form-field name="amount" label="Số tiền muốn rút (VNĐ)" type="number" required="true"
                        placeholder="Tối thiểu 10.000 đ"
                        hint="Số tiền rút không được vượt quá số dư khả dụng hiện tại."/>

          <t:form-field name="note" label="Thông tin tài khoản nhận" required="true"
                        placeholder="Ví dụ: Vietcombank - 0123456789 - TRAN THI VENDOR"/>

          <button type="submit" class="btn btn-brand btn-min w-100 d-inline-flex align-items-center justify-content-center gap-2">
            <t:icon name="check" size="18"/> Xác nhận rút tiền
          </button>
        </form>
      </t:card>
    </div>

    <%-- Bảng lịch sử biến động số dư (SALE, COMMISSION, WITHDRAW, REFUND) --%>
    <div class="col-lg-8">
      <t:card title="Lịch sử giao dịch ví">
        <c:choose>
          <c:when test="${empty transactions}">
            <t:empty-state variant="empty" title="Chưa có giao dịch tài chính nào phát sinh"/>
          </c:when>
          <c:otherwise>
            <div class="table-responsive">
              <table class="ss-table">
                <thead>
                  <tr>
                    <th>Mã GD</th>
                    <th>Loại giao dịch</th>
                    <th>Đơn hàng</th>
                    <th>Diễn giải</th>
                    <th class="num">Số tiền</th>
                  </tr>
                </thead>
                <tbody>
                  <c:forEach var="tx" items="${transactions}">
                    <tr>
                      <td>#${tx.id}</td>
                      <td>
                        <c:choose>
                          <c:when test="${tx.type == 'SALE'}">
                            <span class="ss-badge success">Doanh thu bán</span>
                          </c:when>
                          <c:when test="${tx.type == 'COMMISSION'}">
                            <span class="ss-badge warning">Chiết khấu sàn</span>
                          </c:when>
                          <c:when test="${tx.type == 'WITHDRAW'}">
                            <span class="ss-badge info">Rút tiền</span>
                          </c:when>
                          <c:otherwise>
                            <span class="ss-badge danger">Hoàn tiền</span>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td>${not empty tx.order ? tx.order.code : '—'}</td>
                      <td><c:out value="${tx.note}"/></td>
                      <td class="num" style="font-weight:var(--fw-bold);color:${tx.type == 'SALE' ? 'var(--success)' : 'var(--danger)'}">
                        ${tx.type == 'SALE' ? '+' : '-'}<fmt:formatNumber value="${tx.amount}" type="number"/> đ
                      </td>
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
</t:vendor-shell>
</body>
</html>

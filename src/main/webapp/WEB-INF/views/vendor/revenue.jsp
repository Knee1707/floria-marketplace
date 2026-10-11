<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>Thống kê doanh thu - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:vendor-shell active="revenue">
  <t:page-header title="Báo cáo & Biểu đồ Doanh thu"
                 subtitle="Công thức chuẩn: Doanh thu ròng = Tổng đơn DELIVERED − Hoa hồng sàn (${summary.commissionRate}%)"/>

  <div class="row g-3" style="margin-bottom:var(--space-6)">
    <div class="col-sm-6 col-xl-3">
      <t:stat-tile label="Đơn giao thành công" value="${summary.deliveredOrders}" icon="check"/>
    </div>
    <div class="col-sm-6 col-xl-3">
      <fmt:formatNumber value="${summary.grossRevenue}" type="number" var="grossFmt"/>
      <t:stat-tile label="Tổng doanh thu gộp (đ)" value="${grossFmt}" icon="chart"/>
    </div>
    <div class="col-sm-6 col-xl-3">
      <fmt:formatNumber value="${summary.commissionAmount}" type="number" var="commFmt"/>
      <t:stat-tile label="Hoa hồng sàn (${summary.commissionRate}%)" value="${commFmt}" icon="tag"/>
    </div>
    <div class="col-sm-6 col-xl-3">
      <fmt:formatNumber value="${summary.netRevenue}" type="number" var="netFmt"/>
      <t:stat-tile label="Doanh thu thực nhận (đ)" value="${netFmt}" icon="store"/>
    </div>
  </div>

  <div class="row g-4">
    <%-- Biểu đồ Chart.js (F4.7) --%>
    <div class="col-lg-7">
      <t:card title="Biểu đồ doanh thu 7 ngày gần nhất">
        <div style="height:320px;position:relative">
          <canvas id="vendorRevenueChart"></canvas>
        </div>
      </t:card>
    </div>

    <%-- Bảng chi tiết doanh thu theo ngày --%>
    <div class="col-lg-5">
      <t:card title="Chi tiết theo ngày">
        <div class="table-responsive">
          <table class="ss-table">
            <thead>
              <tr>
                <th>Ngày</th>
                <th class="num">Số đơn</th>
                <th class="num">DT Gộp</th>
                <th class="num">DT Ròng</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="pt" items="${summary.dailyPoints}">
                <tr>
                  <td style="font-weight:var(--fw-semibold)">${pt.label}</td>
                  <td class="num">${pt.orderCount}</td>
                  <td class="num"><fmt:formatNumber value="${pt.gross}" type="number"/> đ</td>
                  <td class="num price"><fmt:formatNumber value="${pt.net}" type="number"/> đ</td>
                </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
      </t:card>
    </div>
  </div>
</t:vendor-shell>

<script src="${ctx}/webjars/chart.js/4.4.6/dist/chart.umd.js"></script>
<script>
  (function() {
    var canvas = document.getElementById('vendorRevenueChart');
    if (!canvas || typeof Chart === 'undefined') return;
    var rootStyle = getComputedStyle(document.documentElement);
    var primaryColor = rootStyle.getPropertyValue('--color-primary').trim();
    var accentColor = rootStyle.getPropertyValue('--pink-300').trim();

    new Chart(canvas, {
      type: 'bar',
      data: {
        labels: ${summary.chartLabelsJson},
        datasets: [
          {
            label: 'Doanh thu gộp (VNĐ)',
            data: ${summary.chartGrossJson},
            backgroundColor: accentColor,
            borderRadius: 6
          },
          {
            label: 'Doanh thu ròng sau chiết khấu (VNĐ)',
            data: ${summary.chartNetJson},
            backgroundColor: primaryColor,
            borderRadius: 6
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'bottom' }
        },
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  })();
</script>
</body>
</html>

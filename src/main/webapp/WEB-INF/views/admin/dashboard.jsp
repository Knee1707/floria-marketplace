<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.8–F6.10 Dashboard (Admin: toàn hệ thống · Manager: shop được giao). @owner: TV3 --%>
<html>
<head><title>Tổng quan quản trị - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="base" value="${ctx}/${area}"/>
<t:admin-shell active="dashboard">
  <t:page-header title="Tổng quan"
                 subtitle="${dash.systemWide ? 'Toàn hệ thống' : 'Các cửa hàng bạn được phân công'} · ${dash.days} ngày gần nhất">
    <nav class="seg" aria-label="Khoảng thời gian">
      <c:forEach var="d" items="7,30,90">
        <a href="${base}?days=${d}" class="${dash.days == d ? 'active' : ''}">${d} ngày</a>
      </c:forEach>
    </nav>
  </t:page-header>

  <c:if test="${not dash.systemWide and dash.kpis[1].value == 0}">
    <t:empty-state variant="empty" title="Bạn chưa được phân công cửa hàng nào. Hãy liên hệ Admin."/>
  </c:if>

  <div class="kpi-grid">
    <c:forEach var="k" items="${dash.kpis}">
      <c:choose>
        <c:when test="${k.money}"><fmt:formatNumber var="kv" value="${k.value}" type="number" maxFractionDigits="0"/><c:set var="kv" value="${kv} đ"/></c:when>
        <c:otherwise><fmt:formatNumber var="kv" value="${k.value}" type="number"/></c:otherwise>
      </c:choose>
      <t:stat-tile label="${k.label}" value="${kv}" icon="${k.icon}"/>
    </c:forEach>
  </div>

  <div class="row g-4">
    <div class="col-lg-8">
      <t:card title="Doanh thu & số đơn theo ngày">
        <div class="chart-box" id="revenueBox">
          <canvas id="revenueChart" role="img" aria-label="Biểu đồ doanh thu và số đơn theo ngày"></canvas>
        </div>
      </t:card>
    </div>
    <div class="col-lg-4">
      <t:card title="Trạng thái đơn">
        <c:choose>
          <c:when test="${dash.chart.totalOrders == 0}">
            <t:empty-state variant="empty" title="Chưa có đơn trong khoảng này"/>
          </c:when>
          <c:otherwise>
            <div class="chart-box sm"><canvas id="statusChart" role="img" aria-label="Biểu đồ tỉ lệ trạng thái đơn"></canvas></div>
          </c:otherwise>
        </c:choose>
      </t:card>
    </div>

    <c:if test="${dash.systemWide}">
      <div class="col-lg-6">
        <t:card title="Người dùng & cửa hàng mới">
          <div class="chart-box sm"><canvas id="growthChart" role="img" aria-label="Biểu đồ người dùng và cửa hàng mới theo ngày"></canvas></div>
        </t:card>
      </div>
    </c:if>

    <div class="${dash.systemWide ? 'col-lg-6' : 'col-12'}">
      <t:card title="Top cửa hàng theo doanh thu">
        <c:choose>
          <c:when test="${empty dash.topStores}">
            <t:empty-state variant="empty" title="Chưa có đơn đã giao trong khoảng này"/>
          </c:when>
          <c:otherwise>
            <div class="ss-table-wrap">
              <table class="ss-table">
                <thead><tr><th>Cửa hàng</th><th class="num">Đơn đã giao</th><th class="num">Doanh thu</th></tr></thead>
                <tbody>
                  <c:forEach var="s" items="${dash.topStores}">
                    <tr><td><c:out value="${s.name}"/></td>
                      <td class="num"><fmt:formatNumber value="${s.orders}" type="number"/></td>
                      <td class="num"><fmt:formatNumber value="${s.revenue}" type="number" maxFractionDigits="0"/> đ</td></tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:otherwise>
        </c:choose>
      </t:card>
    </div>
  </div>
</t:admin-shell>

<%-- Dữ liệu biểu đồ: JSON đã thoát "<" ở server --%>
<script type="application/json" id="dashData">${chartJson}</script>
<script src="${ctx}/webjars/chart.js/4.4.6/dist/chart.umd.js"></script>
<script>
(function () {
  var box = document.getElementById('revenueBox');
  var data = JSON.parse(document.getElementById('dashData').textContent || 'null');
  if (!window.Chart || !data) {
    box.innerHTML = '';
    var p = document.createElement('p'); p.className = 'ss-state';
    p.textContent = 'Không tải được biểu đồ. Hãy tải lại trang.';
    box.appendChild(p);
    return;
  }
  // Màu lấy từ token (tokens.css) — không viết mã màu trong JS.
  var css = getComputedStyle(document.documentElement);
  function tk(n) { return css.getPropertyValue(n).trim(); }
  var C = {
    primary: tk('--color-primary'), accent: tk('--color-accent'), muted: tk('--color-text-muted'),
    border: tk('--color-border'), info: tk('--info'), success: tk('--success'),
    warning: tk('--warning'), danger: tk('--danger'), neutral: tk('--neutral'), soft: tk('--pink-100')
  };
  var R = parseInt(tk('--radius-sm'), 10) || 0; // bo góc cột = token --radius-sm
  Chart.defaults.font.family = tk('--font-sans');
  Chart.defaults.color = C.muted;
  Chart.defaults.borderColor = C.border;

  var labels = data.labels.map(function (d) { var p = d.split('-'); return p[2] + '/' + p[1]; });
  var money = new Intl.NumberFormat('vi-VN');

  new Chart(document.getElementById('revenueChart'), {
    data: {
      labels: labels,
      datasets: [
        { type: 'line', label: 'Doanh thu (đ)', data: data.revenue, yAxisID: 'y',
          borderColor: C.primary, backgroundColor: C.soft, fill: true, tension: .3, pointRadius: 2 },
        { type: 'bar', label: 'Số đơn', data: data.orders, yAxisID: 'y1', backgroundColor: C.accent, borderRadius: R }
      ]
    },
    options: {
      maintainAspectRatio: false, interaction: { mode: 'index', intersect: false },
      scales: {
        y: { beginAtZero: true, ticks: { callback: function (v) { return money.format(v); } } },
        y1: { beginAtZero: true, position: 'right', grid: { drawOnChartArea: false }, ticks: { precision: 0 } }
      },
      plugins: { tooltip: { callbacks: { label: function (c) {
        return c.dataset.label + ': ' + money.format(c.parsed.y);
      } } } }
    }
  });

  // Màu trạng thái đơn khớp status-badge.tag (SSOT): NEW info · CONFIRMED primary · PICKED_UP accent ·
  // SHIPPING warning · DELIVERED success · CANCELLED neutral · RETURNED danger
  var statusMeta = {
    NEW: ['Đơn mới', C.info], CONFIRMED: ['Đã xác nhận', C.primary], PICKED_UP: ['Đã lấy hàng', C.accent],
    SHIPPING: ['Đang giao', C.warning], DELIVERED: ['Đã giao', C.success], CANCELLED: ['Đã huỷ', C.neutral],
    RETURNED: ['Trả/hoàn', C.danger]
  };
  var statusEl = document.getElementById('statusChart');
  if (statusEl) {
    var keys = Object.keys(data.statusCounts).filter(function (k) { return data.statusCounts[k] > 0; });
    new Chart(statusEl, {
      type: 'doughnut',
      data: {
        labels: keys.map(function (k) { return statusMeta[k] ? statusMeta[k][0] : k; }),
        datasets: [{ data: keys.map(function (k) { return data.statusCounts[k]; }),
                     backgroundColor: keys.map(function (k) { return statusMeta[k] ? statusMeta[k][1] : C.neutral; }) }]
      },
      options: { maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } }
    });
  }

  var growthEl = document.getElementById('growthChart');
  if (growthEl && data.newUsers) {
    new Chart(growthEl, {
      type: 'bar',
      data: { labels: labels, datasets: [
        { label: 'Người dùng mới', data: data.newUsers, backgroundColor: C.info, borderRadius: R },
        { label: 'Cửa hàng mới', data: data.newStores, backgroundColor: C.accent, borderRadius: R }
      ] },
      options: { maintainAspectRatio: false, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
    });
  }
})();
</script>
</body>
</html>

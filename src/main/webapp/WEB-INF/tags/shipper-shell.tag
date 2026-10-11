<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%--
  shipper-shell.tag — App Shell "Shipper" (rules/frontend/01 §2): Topbar gọn + list đơn (mobile-first) + WebSocket STOMP.
  @owner: TV2 | F5.1–F5.3, F7.1
--%>
<%@ attribute name="active" required="true" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<style>
  .shipper-nav-bar{display:flex;align-items:center;justify-content:space-between;flex-wrap:wrap;gap:var(--space-3);background:var(--color-surface);border:1px solid var(--color-border);border-radius:var(--radius-md);padding:var(--space-3) var(--space-4);margin-bottom:var(--space-6);box-shadow:var(--shadow-sm)}
  .shipper-tabs{display:flex;gap:var(--space-2);flex-wrap:wrap}
  .shipper-toast{display:flex;align-items:center;gap:var(--space-2);padding:var(--space-3) var(--space-4);border-radius:var(--radius);margin-bottom:var(--space-4);font-weight:var(--fw-medium)}
  .shipper-toast.success{background:var(--success-bg);color:var(--success)}
  .shipper-toast.danger{background:var(--danger-bg);color:var(--danger)}
  .shipper-toast.info{background:var(--info-bg);color:var(--info)}
</style>

<div class="shipper-shell">
  <div class="shipper-nav-bar">
    <div class="d-flex align-items-center gap-2" style="font-weight:var(--fw-bold);color:var(--color-primary)">
      <t:icon name="truck" size="22"/>
      <span>Kênh Giao Vận (Shipper)</span>
    </div>
    <div class="shipper-tabs">
      <a class="btn btn-sm ${active=='orders'?'btn-brand':'btn-outline-secondary'} d-inline-flex align-items-center gap-1"
         href="${ctx}/shipper/orders">
        <t:icon name="package" size="16"/> Đơn phân công
      </a>
      <a class="btn btn-sm ${active=='stats'?'btn-brand':'btn-outline-secondary'} d-inline-flex align-items-center gap-1"
         href="${ctx}/shipper/stats">
        <t:icon name="chart" size="16"/> Thống kê giao hàng
      </a>
    </div>
  </div>

  <div id="wsShipperBanner" class="shipper-toast info d-none" role="status">
    <t:icon name="bell" size="18"/>
    <span id="wsShipperText">Bạn có thông báo phân công mới</span>
  </div>
  <c:if test="${not empty toast}">
    <div class="shipper-toast success" role="status">
      <t:icon name="check" size="18"/> <span><c:out value="${toast}"/></span>
    </div>
  </c:if>
  <c:if test="${not empty toastError}">
    <div class="shipper-toast danger" role="alert">
      <t:icon name="x" size="18"/> <span><c:out value="${toastError}"/></span>
    </div>
  </c:if>

  <jsp:doBody/>
</div>

<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
<script>
  (function() {
    try {
      var sock = new SockJS('${ctx}/ws');
      var stomp = Stomp.over(sock);
      stomp.debug = null;
      stomp.connect({}, function() {
        stomp.subscribe('/user/queue/noti', function(frame) {
          try {
            var data = JSON.parse(frame.body);
            var banner = document.getElementById('wsShipperBanner');
            var txt = document.getElementById('wsShipperText');
            if (banner && txt && data.content) {
              txt.textContent = data.content;
              banner.classList.remove('d-none');
            }
          } catch (e) {}
        });
      });
    } catch (err) {}
  })();
</script>

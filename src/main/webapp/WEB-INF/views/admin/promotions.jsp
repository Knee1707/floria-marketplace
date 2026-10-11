<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.Date" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.6 Khuyến mãi hệ thống: voucher + chương trình (store_id = null). @owner: TV3 --%>
<html>
<head><title>Khuyến mãi hệ thống - Quản trị Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<fmt:setTimeZone value="Asia/Ho_Chi_Minh"/>
<t:admin-shell active="promotions">
  <t:page-header title="Khuyến mãi hệ thống" subtitle="Áp dụng toàn sàn: giảm % tiền hàng hoặc giảm phí ship"/>

  <%-- ===== Voucher ===== --%>
  <div class="row g-4" style="margin-bottom:var(--space-6)">
    <div class="col-lg-8">
      <t:card title="Mã giảm giá (voucher)">
        <c:choose>
          <c:when test="${empty vouchers}">
            <t:empty-state variant="empty" title="Chưa có voucher toàn sàn"/>
          </c:when>
          <c:otherwise>
            <div class="ss-table-wrap">
              <table class="ss-table">
                <thead><tr><th>Mã</th><th>Loại</th><th class="num">Giá trị</th><th>Điều kiện</th><th class="num">Đã dùng</th><th>Hiệu lực</th><th>Trạng thái</th><th class="actions">Thao tác</th></tr></thead>
                <tbody>
                <c:forEach var="v" items="${vouchers}">
                  <tr>
                    <td style="font-weight:var(--fw-semibold)"><c:out value="${v.code}"/></td>
                    <td>${v.type == 'SHIPPING' ? 'Giảm phí ship' : 'Giảm % tiền hàng'}</td>
                    <td class="num"><c:choose>
                      <c:when test="${v.type == 'PRODUCT_PERCENT'}"><fmt:formatNumber value="${v.value}" maxFractionDigits="2"/>%</c:when>
                      <c:otherwise><fmt:formatNumber value="${v.value}" type="number" maxFractionDigits="0"/> đ</c:otherwise>
                    </c:choose></td>
                    <td class="muted" style="font-size:var(--text-xs)">
                      <c:if test="${not empty v.minOrder}">Đơn từ <fmt:formatNumber value="${v.minOrder}" type="number" maxFractionDigits="0"/> đ<br></c:if>
                      <c:if test="${not empty v.maxDiscount}">Tối đa <fmt:formatNumber value="${v.maxDiscount}" type="number" maxFractionDigits="0"/> đ</c:if>
                    </td>
                    <td class="num">${empty v.usedCount ? 0 : v.usedCount}<c:if test="${not empty v.quota}"> / ${v.quota}</c:if></td>
                    <td class="muted" style="font-size:var(--text-xs)">
                      <c:choose>
                        <c:when test="${empty v.startAt and empty v.endAt}">Không giới hạn</c:when>
                        <c:otherwise>
                          <c:choose><c:when test="${empty v.startAt}">…</c:when><c:otherwise><fmt:formatDate value="${Date.from(v.startAt)}" pattern="dd/MM/yyyy"/></c:otherwise></c:choose>
                          –
                          <c:choose><c:when test="${empty v.endAt}">…</c:when><c:otherwise><fmt:formatDate value="${Date.from(v.endAt)}" pattern="dd/MM/yyyy"/></c:otherwise></c:choose>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td><t:status-badge value="${v.active ? 'ENABLED' : 'DISABLED'}"/></td>
                    <td class="actions">
                      <form method="post" action="${ctx}/admin/promotions/vouchers/${v.id}/active" class="d-inline"
                            data-confirm="${v.active ? 'Tắt' : 'Bật'} voucher ${fn:escapeXml(v.code)}?">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="hidden" name="active" value="${not v.active}"/>
                        <button class="btn btn-sm ${v.active ? 'btn-outline-danger' : 'btn-outline-success'}" type="submit">
                          ${v.active ? 'Tắt' : 'Bật'}</button>
                      </form>
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
    <div class="col-lg-4">
      <t:card title="Tạo voucher">
        <form method="post" action="${ctx}/admin/promotions/vouchers">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <div class="mb-3">
            <label class="form-label" for="v_code">Mã <span style="color:var(--danger)">*</span></label>
            <input class="form-control" id="v_code" name="code" required pattern="[A-Za-z0-9_\-]{3,40}" maxlength="40"
                   placeholder="Vd: SALE20" style="text-transform:uppercase">
            <div class="form-text muted">3–40 ký tự: chữ, số, - hoặc _</div>
          </div>
          <div class="mb-3">
            <label class="form-label" for="v_type">Loại <span style="color:var(--danger)">*</span></label>
            <select class="form-select" id="v_type" name="type" required>
              <option value="PRODUCT_PERCENT">Giảm % tiền hàng</option>
              <option value="SHIPPING">Giảm phí ship (đ)</option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-label" for="v_value">Giá trị <span style="color:var(--danger)">*</span></label>
            <input class="form-control" id="v_value" name="value" type="number" min="0.01" step="0.01" required placeholder="% hoặc số tiền">
          </div>
          <div class="row g-2">
            <div class="col-6 mb-3"><label class="form-label" for="v_min">Đơn tối thiểu</label>
              <input class="form-control" id="v_min" name="minOrder" type="number" min="0" step="1000"></div>
            <div class="col-6 mb-3"><label class="form-label" for="v_max">Giảm tối đa</label>
              <input class="form-control" id="v_max" name="maxDiscount" type="number" min="0" step="1000"></div>
            <div class="col-12 mb-3"><label class="form-label" for="v_quota">Số lượt</label>
              <input class="form-control" id="v_quota" name="quota" type="number" min="1" step="1" placeholder="Để trống = không giới hạn"></div>
            <div class="col-6 mb-3"><label class="form-label" for="v_start">Từ ngày</label>
              <input class="form-control" id="v_start" name="startDate" type="date"></div>
            <div class="col-6 mb-3"><label class="form-label" for="v_end">Đến ngày</label>
              <input class="form-control" id="v_end" name="endDate" type="date"></div>
          </div>
          <button class="btn btn-brand btn-min w-100" type="submit"><t:icon name="add" size="18"/> Tạo voucher</button>
        </form>
      </t:card>
    </div>
  </div>

  <%-- ===== Chương trình khuyến mãi ===== --%>
  <div class="row g-4">
    <div class="col-lg-8">
      <t:card title="Chương trình khuyến mãi">
        <c:choose>
          <c:when test="${empty promotions}">
            <t:empty-state variant="empty" title="Chưa có chương trình toàn sàn"/>
          </c:when>
          <c:otherwise>
            <div class="ss-table-wrap">
              <table class="ss-table">
                <thead><tr><th>Loại</th><th class="num">Giá trị</th><th class="num">Số lượt</th><th>Hiệu lực</th><th class="actions">Thao tác</th></tr></thead>
                <tbody>
                <c:forEach var="pr" items="${promotions}">
                  <tr>
                    <td>${pr.type == 'SHIPPING' ? 'Giảm phí ship' : 'Giảm % sản phẩm'}</td>
                    <td class="num"><c:choose>
                      <c:when test="${pr.type == 'PRODUCT_PERCENT'}"><fmt:formatNumber value="${pr.value}" maxFractionDigits="2"/>%</c:when>
                      <c:otherwise><fmt:formatNumber value="${pr.value}" type="number" maxFractionDigits="0"/> đ</c:otherwise>
                    </c:choose></td>
                    <td class="num">${empty pr.quota ? '∞' : pr.quota}</td>
                    <td class="muted" style="font-size:var(--text-xs)"><c:choose>
                        <c:when test="${empty pr.startAt and empty pr.endAt}">Không giới hạn</c:when>
                        <c:otherwise>
                          <c:choose><c:when test="${empty pr.startAt}">…</c:when><c:otherwise><fmt:formatDate value="${Date.from(pr.startAt)}" pattern="dd/MM/yyyy"/></c:otherwise></c:choose>
                          –
                          <c:choose><c:when test="${empty pr.endAt}">…</c:when><c:otherwise><fmt:formatDate value="${Date.from(pr.endAt)}" pattern="dd/MM/yyyy"/></c:otherwise></c:choose>
                        </c:otherwise>
                      </c:choose></td>
                    <td class="actions">
                      <form method="post" action="${ctx}/admin/promotions/programs/${pr.id}/delete" class="d-inline"
                            data-confirm="Xoá chương trình khuyến mãi #${pr.id}?">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button class="btn btn-sm btn-outline-danger" type="submit" title="Xoá"><t:icon name="delete" size="16" label="Xoá"/></button>
                      </form>
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
    <div class="col-lg-4">
      <t:card title="Tạo chương trình">
        <form method="post" action="${ctx}/admin/promotions/programs">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <div class="mb-3">
            <label class="form-label" for="p_type">Loại <span style="color:var(--danger)">*</span></label>
            <select class="form-select" id="p_type" name="type" required>
              <option value="PRODUCT_PERCENT">Giảm % sản phẩm</option>
              <option value="SHIPPING">Giảm phí ship (đ)</option>
            </select>
          </div>
          <div class="mb-3">
            <label class="form-label" for="p_value">Giá trị <span style="color:var(--danger)">*</span></label>
            <input class="form-control" id="p_value" name="value" type="number" min="0.01" step="0.01" required>
          </div>
          <div class="row g-2">
            <div class="col-12 mb-3"><label class="form-label" for="p_quota">Số lượt</label>
              <input class="form-control" id="p_quota" name="quota" type="number" min="1" step="1" placeholder="Để trống = không giới hạn"></div>
            <div class="col-6 mb-3"><label class="form-label" for="p_start">Từ ngày</label>
              <input class="form-control" id="p_start" name="startDate" type="date"></div>
            <div class="col-6 mb-3"><label class="form-label" for="p_end">Đến ngày</label>
              <input class="form-control" id="p_end" name="endDate" type="date"></div>
          </div>
          <button class="btn btn-brand btn-min w-100" type="submit"><t:icon name="add" size="18"/> Tạo chương trình</button>
        </form>
      </t:card>
    </div>
  </div>
</t:admin-shell>
</body>
</html>

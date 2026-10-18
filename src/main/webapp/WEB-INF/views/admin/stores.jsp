<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.4 Duyệt / cấm cửa hàng (Admin: tất cả · Manager: shop được giao) + Admin phân công Manager. @owner: TV3 --%>
<html>
<head><title>Cửa hàng - Quản trị Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isAdmin" value="${area != 'manager'}"/>
<t:admin-shell active="stores">
  <t:page-header title="Cửa hàng"
                 subtitle="${isAdmin ? 'Duyệt shop mới, cấm shop vi phạm, phân công Manager phụ trách' : 'Các cửa hàng bạn được phân công'}"/>

  <c:if test="${isAdmin and empty managerCandidates}">
    <div class="muted" style="font-size:var(--text-sm);margin-bottom:var(--space-3)">
      Chưa có tài khoản MANAGER để phân công — tạo ở mục <a href="${ctx}/admin/users?role=MANAGER">Người dùng</a>.
    </div>
  </c:if>

  <c:choose>
    <c:when test="${empty stores}">
      <t:empty-state variant="empty"
                     title="${isAdmin ? 'Chưa có cửa hàng nào đăng ký' : 'Bạn chưa được phân công cửa hàng nào'}"/>
    </c:when>
    <c:otherwise>
      <div class="ss-table-wrap">
        <table class="ss-table">
          <thead><tr><th>Cửa hàng</th><th>Chủ shop</th><th>Manager phụ trách</th><th>Trạng thái</th><th class="actions">Thao tác</th></tr></thead>
          <tbody>
          <c:forEach var="s" items="${stores}">
            <c:set var="mgrs" value="${managersByStore[s.id]}"/>
            <tr>
              <td><div style="font-weight:var(--fw-semibold)"><c:out value="${s.name}"/></div>
                  <div class="muted" style="font-size:var(--text-xs)">#${s.id}</div></td>
              <td><c:out value="${empty s.owner.fullName ? s.owner.email : s.owner.fullName}"/>
                  <div class="muted" style="font-size:var(--text-xs)"><c:out value="${s.owner.email}"/></div></td>
              <td>
                <c:if test="${empty mgrs}"><span class="muted">Chưa phân công</span></c:if>
                <c:forEach var="m" items="${mgrs}">
                  <div class="d-flex align-items-center gap-1" style="margin-bottom:var(--space-1)">
                    <span><c:out value="${empty m.fullName ? m.email : m.fullName}"/></span>
                    <c:if test="${isAdmin}">
                      <form method="post" action="${ctx}/admin/stores/${s.id}/managers/${m.id}/delete" class="d-inline m-0"
                            data-confirm="Gỡ ${fn:escapeXml(empty m.fullName ? m.email : m.fullName)} khỏi ${fn:escapeXml(s.name)}?">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <button class="btn btn-sm btn-outline-danger" type="submit" title="Gỡ phân công"><t:icon name="x" size="16" label="Gỡ phân công"/></button>
                      </form>
                    </c:if>
                  </div>
                </c:forEach>
                <c:if test="${isAdmin and not empty managerCandidates}">
                  <form method="post" action="${ctx}/admin/stores/${s.id}/managers" class="d-flex gap-1 m-0">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <label class="visually-hidden" for="mgr_${s.id}">Chọn Manager</label>
                    <select class="form-select form-select-sm" id="mgr_${s.id}" name="managerId" required style="width:auto">
                      <option value="">Chọn Manager…</option>
                      <c:forEach var="cand" items="${managerCandidates}">
                        <c:set var="assigned" value="false"/>
                        <c:forEach var="m" items="${mgrs}"><c:if test="${m.id == cand.id}"><c:set var="assigned" value="true"/></c:if></c:forEach>
                        <c:if test="${not assigned}">
                          <option value="${cand.id}"><c:out value="${empty cand.fullName ? cand.email : cand.fullName}"/></option>
                        </c:if>
                      </c:forEach>
                    </select>
                    <button class="btn btn-sm btn-outline-secondary" type="submit"><t:icon name="add" size="16"/> Gán</button>
                  </form>
                </c:if>
              </td>
              <td><t:status-badge value="${s.approved ? 'APPROVED' : 'PENDING'}"/></td>
              <td class="actions">
                <form method="post" action="${ctx}/${area}/stores/${s.id}/approve" class="d-inline"
                      data-confirm="${s.approved ? 'Cấm' : 'Duyệt'} cửa hàng ${fn:escapeXml(s.name)}?">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <input type="hidden" name="approved" value="${not s.approved}"/>
                  <c:choose>
                    <c:when test="${s.approved}">
                      <button class="btn btn-sm btn-outline-danger" type="submit"><t:icon name="ban" size="16"/> Cấm</button>
                    </c:when>
                    <c:otherwise>
                      <button class="btn btn-sm btn-outline-success" type="submit"><t:icon name="check" size="16"/> Duyệt</button>
                    </c:otherwise>
                  </c:choose>
                </form>
                <a class="btn btn-sm btn-outline-secondary" href="${ctx}/${area}/products?storeId=${s.id}" title="Sản phẩm của shop">
                  <t:icon name="package" size="16"/> Sản phẩm</a>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </c:otherwise>
  </c:choose>
</t:admin-shell>
</body>
</html>

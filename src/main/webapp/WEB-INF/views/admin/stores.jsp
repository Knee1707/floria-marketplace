<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.4 Duyệt / cấm cửa hàng (Admin: tất cả · Manager: shop được giao). @owner: TV3 --%>
<html>
<head><title>Cửa hàng - Quản trị Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:admin-shell active="stores">
  <t:page-header title="Cửa hàng"
                 subtitle="${area == 'manager' ? 'Các cửa hàng bạn được phân công' : 'Duyệt shop mới, cấm shop vi phạm'}"/>

  <c:choose>
    <c:when test="${empty stores}">
      <t:empty-state variant="empty"
                     title="${area == 'manager' ? 'Bạn chưa được phân công cửa hàng nào' : 'Chưa có cửa hàng nào đăng ký'}"/>
    </c:when>
    <c:otherwise>
      <div class="ss-table-wrap">
        <table class="ss-table">
          <thead><tr><th>Cửa hàng</th><th>Chủ shop</th><th>Trạng thái</th><th class="actions">Thao tác</th></tr></thead>
          <tbody>
          <c:forEach var="s" items="${stores}">
            <tr>
              <td><div style="font-weight:var(--fw-semibold)"><c:out value="${s.name}"/></div>
                  <div class="muted" style="font-size:var(--text-xs)">#${s.id}</div></td>
              <td><c:out value="${empty s.owner.fullName ? s.owner.email : s.owner.fullName}"/>
                  <div class="muted" style="font-size:var(--text-xs)"><c:out value="${s.owner.email}"/></div></td>
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

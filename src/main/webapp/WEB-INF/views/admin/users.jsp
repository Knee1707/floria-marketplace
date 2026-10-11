<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.1 Quản lý người dùng. @owner: TV3 --%>
<html>
<head><title>Người dùng - Quản trị Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<t:admin-shell active="users">
  <t:page-header title="Người dùng" subtitle="Tìm kiếm, khoá / mở khoá, phân vai trò"/>

  <form class="ss-toolbar" method="get" action="${ctx}/admin/users" role="search">
    <label class="visually-hidden" for="kw">Từ khoá</label>
    <input class="form-control" id="kw" name="keyword" value="${fn:escapeXml(keyword)}" maxlength="100"
           placeholder="Email, họ tên hoặc số điện thoại">
    <button class="btn btn-brand btn-min" type="submit"><t:icon name="search" size="18"/> Tìm</button>
    <c:if test="${not empty keyword}"><a class="btn btn-outline-secondary btn-min" href="${ctx}/admin/users">Xoá lọc</a></c:if>
  </form>

  <c:choose>
    <c:when test="${users.totalElements == 0}">
      <t:empty-state variant="empty" title="Không tìm thấy người dùng phù hợp" actionHref="/admin/users" actionLabel="Xem tất cả"/>
    </c:when>
    <c:otherwise>
      <div class="muted" style="font-size:var(--text-sm);margin-bottom:var(--space-2)">${users.totalElements} người dùng</div>
      <div class="ss-table-wrap">
        <table class="ss-table">
          <thead><tr><th>Người dùng</th><th>Điện thoại</th><th>Vai trò</th><th>Trạng thái</th><th class="actions">Thao tác</th></tr></thead>
          <tbody>
          <c:forEach var="u" items="${users.content}">
            <c:set var="uName" value="${empty u.fullName ? u.email : u.fullName}"/>
            <tr>
              <td><div style="font-weight:var(--fw-semibold)"><c:out value="${uName}"/></div>
                  <div class="muted" style="font-size:var(--text-xs)"><c:out value="${u.email}"/></div></td>
              <td><c:out value="${u.phone}"/></td>
              <td>
                <form method="post" action="${ctx}/admin/users/${u.id}/role" class="d-flex gap-1"
                      data-confirm="Đổi vai trò của ${fn:escapeXml(uName)}?">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <label class="visually-hidden" for="role_${u.id}">Vai trò</label>
                  <select class="form-select form-select-sm" id="role_${u.id}" name="role" style="width:auto">
                    <c:forEach var="r" items="${roles}">
                      <option value="${r}" ${r == u.role ? 'selected' : ''}>${r}</option>
                    </c:forEach>
                  </select>
                  <button class="btn btn-sm btn-outline-secondary" type="submit">Đổi</button>
                </form>
              </td>
              <td>
                <c:choose>
                  <c:when test="${u.locked}"><t:status-badge value="LOCKED"/></c:when>
                  <c:when test="${not u.enabled}"><t:status-badge value="UNVERIFIED"/></c:when>
                  <c:otherwise><t:status-badge value="ENABLED"/></c:otherwise>
                </c:choose>
              </td>
              <td class="actions">
                <form method="post" action="${ctx}/admin/users/${u.id}/lock" class="d-inline"
                      data-confirm="${u.locked ? 'Mở khoá' : 'Khoá'} tài khoản ${fn:escapeXml(uName)}?">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <input type="hidden" name="locked" value="${not u.locked}"/>
                  <c:choose>
                    <c:when test="${u.locked}">
                      <button class="btn btn-sm btn-outline-success" type="submit"><t:icon name="unlock" size="16"/> Mở khoá</button>
                    </c:when>
                    <c:otherwise>
                      <button class="btn btn-sm btn-outline-danger" type="submit"><t:icon name="lock" size="16"/> Khoá</button>
                    </c:otherwise>
                  </c:choose>
                </form>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
      <t:pagination page="${users}" baseUrl="/admin/users?keyword=${keywordParam}&"/>
    </c:otherwise>
  </c:choose>
</t:admin-shell>
</body>
</html>

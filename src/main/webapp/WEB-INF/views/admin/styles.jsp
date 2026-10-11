<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.3 Thuộc tính sản phẩm (master Style – detail StyleValue). @owner: TV3 --%>
<html>
<head><title>Thuộc tính sản phẩm - Quản trị Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:forEach var="x" items="${styles}"><c:if test="${x.id == selectedId}"><c:set var="selected" value="${x}"/></c:if></c:forEach>
<t:admin-shell active="styles">
  <t:page-header title="Thuộc tính sản phẩm" subtitle="Vendor chọn các giá trị này khi đăng sản phẩm (màu, dịp, kiểu dáng…)"/>

  <div class="row g-4">
    <div class="col-lg-5">
      <t:card title="Thuộc tính">
        <c:choose>
          <c:when test="${empty styles}">
            <t:empty-state variant="empty" title="Chưa có thuộc tính nào"/>
          </c:when>
          <c:otherwise>
            <div class="list-group" style="margin-bottom:var(--space-4)">
              <c:forEach var="s" items="${styles}">
                <a class="list-group-item list-group-item-action d-flex justify-content-between align-items-center ${s.id == selectedId ? 'active' : ''}"
                   href="${ctx}/admin/styles?styleId=${s.id}" ${s.id == selectedId ? 'aria-current="true"' : ''}>
                  <span><c:out value="${s.name}"/></span><t:icon name="chevron-right" size="16"/>
                </a>
              </c:forEach>
            </div>
          </c:otherwise>
        </c:choose>
        <form method="post" action="${ctx}/admin/styles">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <label class="form-label" for="f_newStyle">Thêm thuộc tính</label>
          <div class="d-flex gap-2">
            <input class="form-control" id="f_newStyle" name="name" required maxlength="80" placeholder="Vd: Màu sắc">
            <button class="btn btn-brand btn-min" type="submit"><t:icon name="add" size="18"/> Thêm</button>
          </div>
        </form>
      </t:card>
    </div>

    <div class="col-lg-7">
      <c:choose>
        <c:when test="${empty selected}">
          <t:card><t:empty-state variant="empty" title="Chọn hoặc thêm một thuộc tính để quản lý giá trị"/></t:card>
        </c:when>
        <c:otherwise>
          <t:card title="Giá trị của: ${fn:escapeXml(selected.name)}">
            <form method="post" action="${ctx}/admin/styles" class="d-flex gap-2" style="margin-bottom:var(--space-4)">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              <input type="hidden" name="id" value="${selected.id}"/>
              <label class="visually-hidden" for="f_rename">Đổi tên thuộc tính</label>
              <input class="form-control" id="f_rename" name="name" required maxlength="80" value="${fn:escapeXml(selected.name)}">
              <button class="btn btn-outline-secondary btn-min" type="submit"><t:icon name="edit" size="18"/> Đổi tên</button>
            </form>

            <c:choose>
              <c:when test="${empty values}">
                <t:empty-state variant="empty" title="Thuộc tính này chưa có giá trị"/>
              </c:when>
              <c:otherwise>
                <div class="d-flex flex-wrap gap-2" style="margin-bottom:var(--space-4)">
                  <c:forEach var="v" items="${values}">
                    <form method="post" action="${ctx}/admin/styles/values/${v.id}/delete" class="d-inline"
                          data-confirm="Xoá giá trị ${fn:escapeXml(v.value)}?">
                      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                      <input type="hidden" name="styleId" value="${selected.id}"/>
                      <button class="btn btn-sm btn-outline-secondary d-inline-flex align-items-center gap-1" type="submit"
                              style="border-radius:var(--radius-full)" title="Xoá">
                        <c:out value="${v.value}"/> <t:icon name="x" size="14" label="Xoá"/></button>
                    </form>
                  </c:forEach>
                </div>
              </c:otherwise>
            </c:choose>

            <form method="post" action="${ctx}/admin/styles/values">
              <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
              <input type="hidden" name="styleId" value="${selected.id}"/>
              <label class="form-label" for="f_value">Thêm giá trị</label>
              <div class="d-flex gap-2">
                <input class="form-control" id="f_value" name="value" required maxlength="80" placeholder="Vd: Đỏ">
                <button class="btn btn-brand btn-min" type="submit"><t:icon name="add" size="18"/> Thêm</button>
              </div>
            </form>
          </t:card>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</t:admin-shell>
</body>
</html>

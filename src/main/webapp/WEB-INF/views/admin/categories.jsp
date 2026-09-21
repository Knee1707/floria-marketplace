<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- F6.2 Danh mục hoa (2 cấp). @owner: TV3 --%>
<html>
<head><title>Danh mục - Quản trị Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:forEach var="x" items="${categories}"><c:if test="${not empty param.editId and fn:trim(x.id) == param.editId}"><c:set var="editing" value="${x}"/></c:if></c:forEach>
<t:admin-shell active="categories">
  <t:page-header title="Danh mục" subtitle="Danh mục hoa theo dịp — tối đa 2 cấp (cha / con)"/>

  <div class="row g-4">
    <div class="col-lg-7">
      <t:card title="Danh sách">
        <c:choose>
          <c:when test="${empty categories}">
            <t:empty-state variant="empty" title="Chưa có danh mục — thêm danh mục đầu tiên ở khung bên cạnh"/>
          </c:when>
          <c:otherwise>
            <div class="ss-table-wrap">
              <table class="ss-table">
                <thead><tr><th>Tên danh mục</th><th>Thuộc</th><th class="actions">Thao tác</th></tr></thead>
                <tbody>
                <c:forEach var="cat" items="${categories}">
                  <tr>
                    <td style="${empty cat.parent ? 'font-weight:var(--fw-semibold)' : 'padding-left:var(--space-8)'}">
                      <c:out value="${cat.name}"/></td>
                    <td class="muted"><c:out value="${empty cat.parent ? 'Danh mục gốc' : cat.parent.name}"/></td>
                    <td class="actions">
                      <a class="btn btn-sm btn-outline-secondary" href="${ctx}/admin/categories?editId=${cat.id}" title="Sửa">
                        <t:icon name="edit" size="16" label="Sửa"/></a>
                      <form method="post" action="${ctx}/admin/categories/${cat.id}/delete" class="d-inline"
                            data-confirm="Xoá danh mục ${fn:escapeXml(cat.name)}?">
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

    <div class="col-lg-5">
      <t:card title="${empty editing ? 'Thêm danh mục' : 'Sửa danh mục'}">
        <form method="post" action="${ctx}/admin/categories" class="needs-validation">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <c:if test="${not empty editing}"><input type="hidden" name="id" value="${editing.id}"/></c:if>
          <t:form-field name="name" label="Tên danh mục" required="true" value="${fn:escapeXml(editing.name)}"
                        placeholder="Vd: Hoa sinh nhật" hint="2–120 ký tự"/>
          <div class="mb-3">
            <label class="form-label" for="f_parentId">Danh mục cha</label>
            <select class="form-select" id="f_parentId" name="parentId">
              <option value="">— Danh mục gốc —</option>
              <c:forEach var="p" items="${categories}">
                <c:if test="${empty p.parent and p.id != editing.id}">
                  <option value="${p.id}" ${not empty editing.parent and editing.parent.id == p.id ? 'selected' : ''}><c:out value="${p.name}"/></option>
                </c:if>
              </c:forEach>
            </select>
          </div>
          <t:form-field name="imageUrl" label="Ảnh (URL Cloudinary)" type="url" value="${fn:escapeXml(editing.imageUrl)}"
                        placeholder="https://res.cloudinary.com/..." hint="Tuỳ chọn — DB chỉ lưu URL ảnh"/>
          <div class="d-flex gap-2">
            <button class="btn btn-brand btn-min" type="submit"><t:icon name="${empty editing ? 'add' : 'check'}" size="18"/>
              ${empty editing ? 'Thêm danh mục' : 'Lưu thay đổi'}</button>
            <c:if test="${not empty editing}"><a class="btn btn-outline-secondary btn-min" href="${ctx}/admin/categories">Huỷ</a></c:if>
          </div>
        </form>
      </t:card>
    </div>
  </div>
</t:admin-shell>
</body>
</html>

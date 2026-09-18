<%@ tag body-content="scriptless" pageEncoding="UTF-8" %>
<%@ attribute name="id" required="true" %>
<%@ attribute name="title" required="true" %>
<%-- modal.tag — modal Bootstrap dùng chung (rules/frontend/05) --%>
<div class="modal fade" id="${id}" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content" style="border-radius:var(--radius-md)">
      <div class="modal-header"><h3 class="modal-title" style="margin:0">${title}</h3>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button></div>
      <div class="modal-body"><jsp:doBody/></div>
    </div>
  </div>
</div>

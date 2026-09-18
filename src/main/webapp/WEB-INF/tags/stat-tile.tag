<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%-- stat-tile.tag — ô số liệu dashboard (rules/frontend/05) --%>
<%@ attribute name="label" required="true" %>
<%@ attribute name="value" required="true" %>
<%@ attribute name="icon" %>
<div class="ss-stat d-flex align-items-center justify-content-between">
  <div><div class="val">${value}</div><div class="lbl">${label}</div></div>
  <span style="color:var(--color-accent)"><t:icon name="${empty icon?'chart':icon}" size="28"/></span>
</div>

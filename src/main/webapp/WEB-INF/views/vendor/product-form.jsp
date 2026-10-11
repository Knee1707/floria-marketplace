<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<html>
<head><title>${empty product ? 'Thêm sản phẩm' : 'Chỉnh sửa sản phẩm'} - Floria</title></head>
<body>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<t:vendor-shell active="products">
  <t:page-header title="${empty product ? 'Thêm sản phẩm hoa mới' : 'Cập nhật sản phẩm'}"
                 subtitle="Cửa hàng: ${store.name}">
    <a class="btn btn-outline-secondary btn-min d-inline-flex align-items-center gap-1" href="${ctx}/vendor/products">
      Quay lại danh sách
    </a>
  </t:page-header>

  <t:card>
    <form method="post" action="${ctx}/vendor/products/save" enctype="multipart/form-data">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <c:if test="${not empty product}">
        <input type="hidden" name="id" value="${product.id}"/>
      </c:if>

      <div class="row g-3">
        <div class="col-md-8">
          <t:form-field name="name" label="Tên sản phẩm hoa" value="${product.name}" required="true"
                        placeholder="Ví dụ: Bó Hoa Hồng Ecuador Pastel 25 Bông"/>
        </div>
        <div class="col-md-4">
          <div class="mb-3">
            <label class="form-label" for="categoryId">Danh mục hoa</label>
            <select class="form-select" id="categoryId" name="categoryId">
              <option value="">-- Chọn danh mục --</option>
              <c:forEach var="cat" items="${categories}">
                <option value="${cat.id}" ${not empty product.category && product.category.id == cat.id ? 'selected' : ''}>
                  ${cat.name}
                </option>
              </c:forEach>
            </select>
          </div>
        </div>

        <div class="col-md-6">
          <t:form-field name="price" label="Giá bán (VNĐ)" type="number" value="${product.price}" required="true"
                        placeholder="Ví dụ: 450000"/>
        </div>
        <div class="col-md-6">
          <t:form-field name="stock" label="Số lượng tồn kho" type="number" value="${empty product ? 20 : product.stock}" required="true"/>
        </div>

        <div class="col-12">
          <t:form-field name="description" label="Mô tả chi tiết sản phẩm" type="textarea" value="${product.description}"
                        placeholder="Mô tả thành phần hoa, ý nghĩa, kích thước và hướng dẫn bảo quản tươi lâu..."/>
        </div>

        <div class="col-12">
          <div class="mb-3">
            <label class="form-label" for="imageFile">Ảnh đại diện sản phẩm (Upload Cloudinary)</label>
            <input class="form-control" type="file" id="imageFile" name="imageFile" accept="image/*"/>
            <input class="form-control mt-2" type="text" name="thumbnailUrl" value="${product.thumbnailUrl}"
                   placeholder="Hoặc dán trực tiếp đường dẫn URL ảnh sản phẩm"/>
            <div class="form-text muted">Ảnh tải lên được lưu trữ qua Cloudinary CDN; DB chỉ lưu đường dẫn URL.</div>
          </div>
          <c:if test="${not empty product.thumbnailUrl}">
            <div class="d-flex align-items-center gap-3" style="margin-bottom:var(--space-3)">
              <img src="${product.thumbnailUrl}" alt="${product.name}"
                   style="width:80px;height:80px;object-fit:cover;border-radius:var(--radius)">
              <span class="muted" style="font-size:var(--text-sm)">Ảnh hiện tại của sản phẩm</span>
            </div>
          </c:if>
        </div>
      </div>

      <div class="d-flex justify-content-end gap-2" style="margin-top:var(--space-4)">
        <a class="btn btn-outline-secondary btn-min d-inline-flex align-items-center" href="${ctx}/vendor/products">Huỷ</a>
        <button type="submit" class="btn btn-brand btn-min d-inline-flex align-items-center gap-2">
          <t:icon name="check" size="18"/> ${empty product ? 'Tạo sản phẩm' : 'Lưu thay đổi'}
        </button>
      </div>
    </form>
  </t:card>
</t:vendor-shell>
</body>
</html>

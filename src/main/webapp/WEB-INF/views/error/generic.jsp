<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Lỗi ${code}</title></head>
<body>
<div class="text-center py-5">
    <h1 class="display-4 text-danger">${code}</h1>
    <p class="lead">${message}</p>
    <a class="btn btn-danger" href="${pageContext.request.contextPath}/">Về trang chủ</a>
</div>
</body>
</html>

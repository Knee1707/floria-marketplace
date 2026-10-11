/*
 * admin.js — hành vi dùng chung khu quản trị (Admin/Manager). @owner: TV3
 * 1) <form data-confirm="Câu hỏi"> → mở modal #confirmModal, chỉ gửi khi bấm "Đồng ý"
 *    (rules/frontend/05 §6: thao tác nhạy cảm như khoá user, cấm shop phải xác nhận qua modal).
 * 2) Nút submit chuyển trạng thái loading khi form được gửi (chống bấm 2 lần).
 * Không JS → form vẫn gửi bình thường (progressive enhancement).
 */
(function () {
  'use strict';
  var pending = null;

  function setLoading(form) {
    if ((form.method || 'get').toLowerCase() !== 'post') return; // form tìm kiếm GET: không khoá nút
    var btn = form.querySelector('button[type="submit"], button:not([type])');
    if (!btn || btn.disabled) return;
    btn.disabled = true;
    btn.setAttribute('aria-busy', 'true');
    var spin = document.createElement('span');
    spin.className = 'spinner-border spinner-border-sm';
    spin.setAttribute('aria-hidden', 'true');
    btn.prepend(spin, ' ');
  }

  document.addEventListener('submit', function (e) {
    var form = e.target;
    if (!(form instanceof HTMLFormElement)) return;
    if (form.dataset.confirm && form.dataset.confirmed !== '1') {
      var modalEl = document.getElementById('confirmModal');
      if (!modalEl || !window.bootstrap) {
        // Không có Bootstrap JS → dùng hộp thoại mặc định để vẫn có bước xác nhận.
        if (!window.confirm(form.dataset.confirm)) e.preventDefault();
        else setLoading(form);
        return;
      }
      e.preventDefault();
      pending = form;
      document.getElementById('confirmMessage').textContent = form.dataset.confirm; // textContent: chống XSS
      window.bootstrap.Modal.getOrCreateInstance(modalEl).show();
      return;
    }
    setLoading(form);
  });

  document.addEventListener('click', function (e) {
    if (!e.target.closest || !e.target.closest('#confirmOk') || !pending) return;
    var form = pending;
    pending = null;
    form.dataset.confirmed = '1';
    window.bootstrap.Modal.getOrCreateInstance(document.getElementById('confirmModal')).hide();
    setLoading(form);
    form.submit(); // submit() không bắn lại sự kiện submit
  });
})();

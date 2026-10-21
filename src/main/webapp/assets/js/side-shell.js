/*
 * side-shell.js — thanh bên dùng chung các shell có sidebar (Admin/Manager, Vendor). @owner: TV3 (UI chung F8)
 * - Desktop (≥992px): nút [data-side-toggle] thu gọn / mở rộng thanh bên thành dải icon; nhớ lựa chọn giữa các trang.
 * - Mobile  (<992px): thanh bên là ngăn kéo; [data-side-toggle] mở/đóng, bấm nền mờ hoặc Esc để đóng.
 * Nạp NGAY là con đầu tiên của .ss-shell (đồng bộ) để áp trạng thái thu gọn trước khi vẽ → không bị nháy.
 * Không JS → thanh bên vẫn hiện đầy đủ (progressive enhancement).
 */
(function () {
  'use strict';
  var KEY = 'ss-side-collapsed';
  var script = document.currentScript;
  var shell = script && script.parentElement;
  if (!shell || !shell.classList.contains('ss-shell')) return;
  var mq = window.matchMedia('(max-width: 991.98px)');

  // localStorage có thể bị chặn (cửa sổ ẩn danh / chặn cookie) → bỏ qua, mặc định mở rộng.
  try { if (localStorage.getItem(KEY) === '1') shell.classList.add('is-collapsed'); } catch (e) { /* ignore */ }

  function expanded() {
    return mq.matches ? shell.classList.contains('is-open') : !shell.classList.contains('is-collapsed');
  }

  function sync() {
    var open = expanded();
    shell.querySelectorAll('[data-side-toggle]').forEach(function (btn) {
      btn.setAttribute('aria-expanded', String(open));
      if (btn.classList.contains('ss-side-toggle')) { // nút chỉ có icon → cần nhãn
        var label = open ? 'Thu gọn menu' : 'Mở menu';
        btn.setAttribute('aria-label', label);
        btn.title = label;
      }
    });
  }

  function setOpen(open) {
    shell.classList.toggle('is-open', open);
    document.body.classList.toggle('ss-side-lock', open);
    sync();
  }

  shell.addEventListener('click', function (e) {
    if (e.target.closest('[data-side-toggle]')) {
      if (mq.matches) {
        setOpen(!shell.classList.contains('is-open'));
      } else {
        var collapsed = shell.classList.toggle('is-collapsed');
        try { localStorage.setItem(KEY, collapsed ? '1' : '0'); } catch (err) { /* ignore */ }
        sync();
      }
    } else if (e.target.closest('.ss-side-backdrop')) {
      setOpen(false);
    }
  });

  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape' && shell.classList.contains('is-open')) setOpen(false);
  });

  // Xoay màn hình / đổi cỡ cửa sổ qua mốc 992px → đóng ngăn kéo, cập nhật aria.
  var onChange = function () { setOpen(false); };
  if (mq.addEventListener) mq.addEventListener('change', onChange); else mq.addListener(onChange);

  // Các nút toggle nằm SAU script này → cập nhật aria khi DOM xong.
  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', sync); else sync();
})();

(function (window) {
  'use strict';
  function $(sel) { return document.querySelector(sel); }

  function openAddressModal() {
    const modal = $('#addressModal');
    if (!modal) return;
    modal.style.display = 'flex';
    document.body.style.overflow = 'hidden';
  }

  function closeAddressModal() {
    const modal = $('#addressModal');
    if (!modal) return;
    modal.style.display = 'none';
    document.body.style.overflow = '';
  }

  function init() {
    const openBtn = $('#openAddressModal');
    const closeBtn = $('#closeAddressModal');
    const overlay = $('#addressModalOverlay');

    if (openBtn) openBtn.addEventListener('click', function (e) { e.preventDefault(); openAddressModal(); });
    if (closeBtn) closeBtn.addEventListener('click', function (e) { e.preventDefault(); closeAddressModal(); });
    if (overlay) overlay.addEventListener('click', function () { closeAddressModal(); });

    document.addEventListener('keydown', function (e) { if (e.key === 'Escape') closeAddressModal(); });
  }

  document.addEventListener('DOMContentLoaded', init);

  window.AddressModal = { open: openAddressModal, close: closeAddressModal };
})(window);

/* Delete confirmation modal management */
(function (window) {
  'use strict';
  function $(sel) { return document.querySelector(sel); }

  let selectedAddressId = null;

  function openDeleteConfirmModal(addressId) {
    selectedAddressId = addressId;
    const modal = $('#deleteConfirmModal');
    if (!modal) return;
    modal.style.display = 'flex';
    document.body.style.overflow = 'hidden';
  }

  function closeDeleteConfirmModal() {
    const modal = $('#deleteConfirmModal');
    if (!modal) return;
    modal.style.display = 'none';
    document.body.style.overflow = '';
    selectedAddressId = null;
  }

  function confirmDelete() {
    if (!selectedAddressId) return;
    // Create and submit a form to delete
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/addresses/' + encodeURIComponent(selectedAddressId) + '/delete';
    form.style.display = 'none';
    document.body.appendChild(form);
    form.submit();
  }

  function init() {
    // Event delegation for delete trigger buttons
    document.addEventListener('click', function (e) {
      const btn = e.target.closest('.btn-delete-trigger');
      if (btn) {
        e.preventDefault();
        const addressId = btn.getAttribute('data-address-id');
        if (addressId) openDeleteConfirmModal(addressId);
      }
    });

    // Modal controls
    const overlay = document.querySelector('.modal-confirmation-overlay');
    const cancelBtn = $('#cancelDeleteBtn');
    const confirmBtn = $('#confirmDeleteBtn');

    if (overlay) overlay.addEventListener('click', closeDeleteConfirmModal);
    if (cancelBtn) cancelBtn.addEventListener('click', closeDeleteConfirmModal);
    if (confirmBtn) confirmBtn.addEventListener('click', confirmDelete);

    // ESC to close
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') closeDeleteConfirmModal();
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})(window);

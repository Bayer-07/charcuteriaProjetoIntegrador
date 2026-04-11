// Delete button
function openDeleteModal(productId) {
    const modal = document.getElementById('deleteModal');
    const deleteForm = document.getElementById('deleteForm');
    deleteForm.action = '/admin/product/delete/' + productId;

    modal.style.display = 'flex';
}

function closeDeleteModal() {
    document.getElementById('deleteModal').style.display = 'none';
}

// Create button
function openCreateModal() {
    const modal = document.getElementById('createModal');
    const createForm = document.getElementById('deleteForm');
    createForm.action = '/admin/product/create';

    modal.style.display = 'flex';
}

function closeCreateModal() {
    document.getElementById('createModal').style.display = 'none';
}

window.onclick = function(event) {
    const modalDelete = document.getElementById('deleteModal')
    const modalCreate = document.getElementById('createModal');
    if (event.target == modalDelete) {
        closeDeleteModal();
    }
    if (event.target == modalCreate) {
        closeCreateModal();
    }
}

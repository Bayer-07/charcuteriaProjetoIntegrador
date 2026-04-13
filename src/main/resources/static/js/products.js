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

// Edit button
async function loadAndOpenEditModal(productId) {
    const modal = document.getElementById('editModal');

    if (!modal) return;

    const previewImg = modal.querySelector('img[id*="preview"]');
    const form = modal.querySelector('#editForm');

    try {
        const response = await fetch('/admin/product/' + productId);
        if (!response.ok) throw new Error('Erro ao buscar produto');

        const product = await response.json();

        form.action = '/admin/product/update/' + productId;

        modal.querySelector('#edit-name').value = product.name || '';
        modal.querySelector('#edit-description').value = product.description || '';
        modal.querySelector('#edit-category').value = product.category || '';
        modal.querySelector('#edit-price').value = product.price || 0;
        modal.querySelector('#edit-stock').value = product.stock || 0;

        if (product.file) {
            previewImg.setAttribute('src', '/uploads/products/' + product.file);
        } else {
            previewImg.setAttribute('src', '/images/logo.png'); //-> essa é a img padrão
        }

        modal.style.display = 'flex';

    } catch (error) {
        console.error('Erro na carga dos dados:', error);
        alert('Não foi possível carregar os dados deste produto.');
    }
}

function handleImagePreview(input) {
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('edit-image-preview').src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function closeEditModal() {
    document.getElementById('editModal').style.display = 'none';
}

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

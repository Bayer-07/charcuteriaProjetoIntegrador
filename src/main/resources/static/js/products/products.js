// Create button
function openCreateProductModal() {
    const modal = document.getElementById('createProductModal');
    const createForm = document.getElementById('deleteForm');
    createForm.action = '/admin/product/create';

    modal.style.display = 'flex';
}

function closeCreateProductModal() {
    document.getElementById('createProductModal').style.display = 'none';
}

// Edit button
async function loadAndOpenEditProductModal(productId) {
    const modal = document.getElementById('editProductModal');

    if (!modal) return;

    const previewImg = modal.querySelector('img[id*="preview"]');
    const form = modal.querySelector('#editForm');

    try {
        const response = await fetch('/admin/product/' + productId);
        if (!response.ok) throw new Error('Erro ao buscar produto');

        const product = await response.json();

        form.action = '/admin/product/update';

        modal.querySelector('#edit-id').value = productId;
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

function closeEditProductModal() {
    document.getElementById('editProductModal').style.display = 'none';
}

// Delete button
function openDeleteProductModal(productId) {
    const modal = document.getElementById('deleteProductModal');
    const deleteForm = document.getElementById('deleteForm');
    deleteForm.action = '/admin/product/delete/' + productId;

    modal.style.display = 'flex';
}

function closeDeleteProductModal() {
    document.getElementById('deleteProductModal').style.display = 'none';
}

window.onclick = function(event) {
    const modalCreateProduct = document.getElementById('createProductModal');
    const modalDeleteProduct = document.getElementById('deleteProductModal');
    const modalEditProduct = document.getElementById('editProductModal');
    if (event.target == modalCreateProduct) {
        closeCreateProductModal();
    }
    if (event.target == modalDeleteProduct) {
        closeDeleteProductModal();
    }
    if (event.target == modalEditProduct) {
        closeEditProductModal();
    }
}

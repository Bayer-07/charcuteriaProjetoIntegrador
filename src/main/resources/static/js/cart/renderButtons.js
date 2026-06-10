window.onclick = function(event) {
    // product
    const modalDeleteProduct = document.getElementById("deleteProductModal");
    if (event.target == modalDeleteProduct) {
        closeDeleteProductModal();
    }
}

// Delete button
export function openDeleteProductModal(productId) {
    const modal = document.getElementById("deleteProductModal");
    const deleteForm = document.getElementById("deleteProductForm");
    deleteForm.action = "/cart/delete/" + productId;

    modal.style.display = "flex";
}

export function closeDeleteProductModal() {
    document.getElementById("deleteProductModal").style.display = "none";
}

window.openDeleteProductModal = openDeleteProductModal;
window.closeDeleteProductModal = closeDeleteProductModal;

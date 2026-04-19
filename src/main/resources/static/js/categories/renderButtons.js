// Create button
function openCreateCategoryModal() {
    const modal = document.getElementById("createCategoryModal");
    const createForm = document.getElementById("deleteForm");
    createForm.action = "/admin/categories/create";

    modal.style.display = "flex";
}

function closeCreateCategoryModal() {
    document.getElementById("createCategoryModal").style.display = "none";
}

// Delete button
function openDeleteCategoryModal(categoryId) {
    const modal = document.getElementById("deleteCategoryModal");
    const createForm = document.getElementById("deleteForm");
    createForm.action = "/admin/categories/delete/" + categoryId;

    modal.style.display = "flex";
}

function closeDeleteCategoryModal() {
    document.getElementById("deleteCategoryModal").style.display = "none";
}

window.onclick = function(event) {
    const modalCreateCategory = document.getElementById("createCategoryModal");
    const modalDeleteCategory = document.getElementById("deleteCategoryModal");
    // const modalEditProduct = document.getElementById("editProductModal");
    if (event.target == modalCreateCategory) {
        closeCreateCategoryModal();
    }
    if (event.target == modalDeleteCategory) {
        closeDeleteCategoryModal();
    }
    // if (event.target == modalEditProduct) {
    //     closeEditProductModal();
    // }
}

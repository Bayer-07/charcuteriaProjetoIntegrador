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

// Edit button
async function loadAndOpenEditCategoryModal(categoryId) {
    const modal = document.getElementById("editCategoryModal");

    if (!modal) {
        console.log("travou aq")
        return;
}
    const form = modal.querySelector("#editCategoryForm");

    try {
        const response = await fetch("/admin/categories/" + categoryId);
        if (!response.ok) throw new Error("Erro ao buscar categoria");

        const category = await response.json();

        form.action = "/admin/categories/update";

        modal.querySelector("#edit-category-id").value = categoryId;
        modal.querySelector("#edit-category-name").value = category.name || "";
        modal.querySelector("#edit-category-description").value = category.description || "";
    } catch (error) {
        console.error("Erro na carga dos dados:", error);
        alert("Não foi possivel carregar os dados deste produto.");
    }

    modal.style.display = "flex";
}

function closeEditCategoryModal() {
    document.getElementById("editCategoryModal").style.display = "none";
}

// Delete button
function openDeleteCategoryModal(categoryId) {
    const modal = document.getElementById("deleteCategoryModal");
    const deleteForm = document.getElementById("deleteCategoryForm");
    deleteForm.action = "/admin/categories/delete/" + categoryId;

    modal.style.display = "flex";
}

function closeDeleteCategoryModal() {
    document.getElementById("deleteCategoryModal").style.display = "none";
}

window.onclick = function(event) {
    // category
    const modalCreateCategory = document.getElementById("createCategoryModal");
    const modalDeleteCategory = document.getElementById("deleteCategoryModal");
    const modalEditCategory = document.getElementById("editCategoryModal");
    if (event.target == modalCreateCategory) {
        closeCreateCategoryModal();
    }
    if (event.target == modalDeleteCategory) {
        closeDeleteCategoryModal();
    }
    if (event.target == modalEditCategory) {
        closeEditCategoryModal();
    }
    // product
    const modalCreateProduct = document.getElementById("createProductModal");
    const modalDeleteProduct = document.getElementById("deleteProductModal");
    const modalEditProduct = document.getElementById("editProductModal");
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

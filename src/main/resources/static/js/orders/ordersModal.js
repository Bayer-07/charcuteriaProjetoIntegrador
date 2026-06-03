function openModal(id) {
    document
    .getElementById("modal-" + id)
    .classList.add("active");
}

function closeModal(id) {
    document
    .getElementById("modal-" + id)
    .classList.remove("active");
}
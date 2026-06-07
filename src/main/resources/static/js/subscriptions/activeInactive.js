document.querySelectorAll(".cell-status").forEach(cell => {
    if (cell.textContent.trim() === "ACTIVE") {
        cell.style.color = "#27ae60";
    } else if (cell.textContent.trim() === "INACTIVE") {
        cell.style.color = "#e74c3c";
    }

    cell.style.fontWeight = "600";
});
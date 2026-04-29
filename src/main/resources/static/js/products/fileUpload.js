function updateFileName(input) {
    const display = document.getElementById('file-name-display');
    if (input.files && input.files.length > 0) {
        display.innerText = "Arquivo selecionado: " + input.files[0].name;
        display.style.color = "#2e7d32";
    } else {
        display.innerText = "Clique para selecionar uma imagem";
        display.style.color = "#666";
    }
}

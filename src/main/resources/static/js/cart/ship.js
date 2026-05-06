let totalOriginal = null;

function calculateShipping() {
    const cepInput = document.getElementById('cepInput');
    const resultDiv = document.getElementById('shippingResult');
    const totalElement = document.querySelector('.total-value');

    if (!totalElement) {
        console.error("Elemento .total-value não encontrado!");
        return;
    }
    if (totalOriginal === null) {
        let rawValue = totalElement.innerText
            .replace('R$', '')
            .replace(/\./g, '')
            .replace(',', '.')
            .trim();
        totalOriginal = parseFloat(rawValue);
    }

    const cep = cepInput.value.replace(/\D/g, '');

    if (cep.length === 8) {
        resultDiv.innerText = "Frete fixo: 15%";
        resultDiv.style.display = "block";
        resultDiv.style.color = "#27ae60";

        const valorComAumento = totalOriginal / 100 * 1.15;

        totalElement.innerText = valorComAumento.toLocaleString('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        });
    } else {
        alert("Por favor, digite um CEP válido com 8 dígitos.");
        cepInput.focus();
    }
}

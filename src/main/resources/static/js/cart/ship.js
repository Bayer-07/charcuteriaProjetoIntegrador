document.getElementById('cepInput').addEventListener('input', function (e) {
    // Remove tudo o que não for número
    let value = e.target.value.replace(/\D/g, '');

    // Aplica a máscara 00000-000 dinamicamente
    if (value.length > 5) {
        value = value.replace(/^(\d{5})(\d)/, '$1-$2');
    }

    e.target.value = value;
});

let totalOriginal = null;

async function calculateShipping() {
    const cepInput = document.getElementById('cepInput');
    const resultDiv = document.getElementById('shippingResult');
    const totalElement = document.querySelector('.total-value');

    if (!totalElement) return;

    if (totalOriginal === null) {
        let rawValue = totalElement.innerText
            .replace('R$', '')
            .replace(/\./g, '')
            .replace(',', '.')
            .trim();

        totalOriginal = parseFloat(rawValue);
    }

    const cep = cepInput.value.replace(/\D/g, '');

    if (cep.length !== 8) {
        alert("CEP inválido");
        return;
    }

    try {
        const payload = { cep: cep };
        console.log('Enviando requisição:', payload);

        const { data } = await axios.post('/api/shipping/calculate', payload);

        console.log('Resposta recebida:', data);

        const menorFrete = data.price || 15.0;

        resultDiv.innerText = `Frete: R$ ${menorFrete.toFixed(2)}`;
        resultDiv.style.display = "block";

    } catch (err) {
        console.error(err);
        resultDiv.innerText = "Erro ao calcular frete";
        resultDiv.style.display = "block";
    }
}

// 🔥 ESSENCIAL (senão dá "not defined")
window.calculateShipping = calculateShipping;
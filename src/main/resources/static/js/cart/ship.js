function closePopup(popupId) {
    const popup = document.getElementsByClassName(popupId);
    if (popup) {
        popup[0].classList.add('inactive');
    }
}

let totalOriginal = null;

function getSelectedAddressZip() {
    const select = document.getElementById('shippingAddressSelect');
    if (select && select.selectedOptions && select.selectedOptions[0]) {
        return select.selectedOptions[0].dataset.zip || '';
    }

    const hiddenZip = document.getElementById('selectedAddressZip');
    return hiddenZip ? hiddenZip.value : '';
}

function syncSelectedAddressZip() {
    const hiddenZip = document.getElementById('selectedAddressZip');
    const select = document.getElementById('shippingAddressSelect');

    if (!hiddenZip || !select || !select.selectedOptions || !select.selectedOptions[0]) {
        return;
    }

    hiddenZip.value = select.selectedOptions[0].dataset.zip || '';
}

async function validaCep(cep) {
    try {
        const { data } = await axios.post('/api/shipping/validate', { cep: cep });
        ('Resposta validação:', data);
        return data;
    } catch (error) {
        console.error("Erro ao validar CEP:", error);
        document.getElementsByClassName('popup-cep-invalido')[0].classList.remove('inactive');
        return { valid: false, message: "CEP_INVALIDO" };
    }
}


async function calculateShipping() {
    const resultDiv = document.getElementById('shippingResult');
    const totalElement = document.querySelector('.total-value');
    const cep = getSelectedAddressZip().replace(/\D/g, '');

    if (!totalElement) return;

    if (totalOriginal === null) {
        let rawValue = totalElement.innerText
            .replace('R$', '')
            .replace(/\s/g, '')
            .trim();

        ('Valor original capturado:', totalElement.innerText);
        ('Após limpeza:', rawValue);

        if (rawValue.includes(',')) {
            rawValue = rawValue.replace(/\./g, '').replace(',', '.');
        }

        totalOriginal = parseFloat(rawValue);
        ('Total original parseado:', totalOriginal);
    }

    if (cep.length !== 8) {
        document.getElementsByClassName('popup-cep-invalido')[0].classList.remove('inactive');
        return;
    }

    const validation = await validaCep(cep);
    ('Resultado validação:', validation);

    if (!validation.valid) {
        ('CEP inválido. Motivo:', validation.message);
        if (validation.message === "FORA_PR") {
            document.getElementsByClassName('popup-cep-fora-pr')[0].classList.remove('inactive');
        } else {
            document.getElementsByClassName('popup-cep-invalido')[0].classList.remove('inactive');
        }
        return;
    }

    ('CEP válido. Prosseguindo com cálculo de frete.');

    try {
        const payload = { cep: cep };
        ('Enviando requisição:', payload);

        const { data } = await axios.post('/api/shipping/calculate', payload);

        ('Resposta recebida:', data);

        const menorFrete = parseFloat(data.price) || 15.0;
        ('Valor do frete:', menorFrete);

        resultDiv.innerText = `Frete: R$ ${menorFrete.toFixed(2).replace('.', ',')}`;
        resultDiv.style.display = "block";

        const novoTotal = totalOriginal + menorFrete;
        ('Cálculo: totalOriginal:', totalOriginal, '+ menorFrete:', menorFrete, '= novoTotal:', novoTotal);
        const formattedTotal = novoTotal.toFixed(2).replace('.', ',');
        totalElement.innerText = `R$ ${formattedTotal}`;

    } catch (err) {
        console.error(err);
        resultDiv.innerText = "Erro ao calcular frete";
        resultDiv.style.display = "block";
    }
}

document.addEventListener('DOMContentLoaded', function () {
    syncSelectedAddressZip();

    const select = document.getElementById('shippingAddressSelect');
    if (select) {
        select.addEventListener('change', syncSelectedAddressZip);
    }
});

// 🔥 ESSENCIAL (senão dá "not defined")
window.syncSelectedAddressZip = syncSelectedAddressZip;
window.calculateShipping = calculateShipping;

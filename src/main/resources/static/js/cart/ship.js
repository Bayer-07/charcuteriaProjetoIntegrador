document.getElementById('cepInput').addEventListener('input', function (e) {
    let value = e.target.value.replace(/\D/g, '');

    if (value.length > 5) {
        value = value.replace(/^(\d{5})(\d)/, '$1-$2');
    }

    e.target.value = value;
});

function closePopup(popupId) {
    const popup = document.getElementsByClassName(popupId);
    if (popup) {
        popup[0].classList.add('inactive');
    }
}

let totalOriginal = null;

async function validaCep(cep) {
    console.log('=== VALIDAÇÃO DE CEP ===');
    console.log('CEP enviado:', cep);
    try {
        const { data } = await axios.post('/api/shipping/validate', { cep: cep });
        console.log('Resposta validação:', data);
        return data;
    } catch (error) {
        console.error("Erro ao validar CEP:", error);
        document.getElementsByClassName('popup-cep-invalido')[0].classList.remove('inactive');
        return { valid: false, message: "CEP_INVALIDO" };
    }
}


async function calculateShipping() {
    const cepInput = document.getElementById('cepInput');
    const resultDiv = document.getElementById('shippingResult');
    const totalElement = document.querySelector('.total-value');

    if (!totalElement) return;

    if (totalOriginal === null) {
        let rawValue = totalElement.innerText
            .replace('R$', '')
            .replace(/\s/g, '')
            .trim();

        console.log('Valor original capturado:', totalElement.innerText);
        console.log('Após limpeza:', rawValue);

        if (rawValue.includes(',')) {
            rawValue = rawValue.replace(/\./g, '').replace(',', '.');
        }

        totalOriginal = parseFloat(rawValue);
        console.log('Total original parseado:', totalOriginal);
    }

    const cep = cepInput.value.replace(/\D/g, '');

    if (cep.length !== 8) {
        document.getElementsByClassName('popup-cep-invalido')[0].classList.remove('inactive');
        return;
    }

    const validation = await validaCep(cep);
    console.log('Resultado validação:', validation);

    if (!validation.valid) {
        console.log('CEP inválido. Motivo:', validation.message);
        if (validation.message === "FORA_PR") {
            document.getElementsByClassName('popup-cep-fora-pr')[0].classList.remove('inactive');
        } else {
            document.getElementsByClassName('popup-cep-invalido')[0].classList.remove('inactive');
        }
        return;
    }

    console.log('CEP válido. Prosseguindo com cálculo de frete.');

    try {
        const payload = { cep: cep };
        console.log('Enviando requisição:', payload);

        const { data } = await axios.post('/api/shipping/calculate', payload);

        console.log('Resposta recebida:', data);

        const menorFrete = parseFloat(data.price) || 15.0;
        console.log('Valor do frete:', menorFrete);

        resultDiv.innerText = `Frete: R$ ${menorFrete.toFixed(2).replace('.', ',')}`;
        resultDiv.style.display = "block";

        const novoTotal = totalOriginal + menorFrete;
        console.log('Cálculo: totalOriginal:', totalOriginal, '+ menorFrete:', menorFrete, '= novoTotal:', novoTotal);
        const formattedTotal = novoTotal.toFixed(2).replace('.', ',');
        totalElement.innerText = `R$ ${formattedTotal}`;
        console.log('Total atualizado para:', totalElement.innerText);

    } catch (err) {
        console.error(err);
        resultDiv.innerText = "Erro ao calcular frete";
        resultDiv.style.display = "block";
    }
}

// 🔥 ESSENCIAL (senão dá "not defined")
window.calculateShipping = calculateShipping;
async function updateQty(itemId, delta) {
    const url = `/cart/update-quantity?itemId=${itemId}&delta=${delta}`;

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        });

        if (response.ok) {
            location.reload();
        } else {
            console.error("Erro no servidor: ", response.status);
        }
    } catch (error) {
        console.error("Erro na requisição: ", error);
    }
}

function updateGlobalTotal() {
    let total = 0;
    document.querySelectorAll('.item-subtotal').forEach(el => {
        total += parseFloat(el.innerText.replace('R$ ', '').replace(/\./g, '').replace(',', '.'));
    });

    document.querySelector('.total-value').innerText = total.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    totalOriginal = total;
}

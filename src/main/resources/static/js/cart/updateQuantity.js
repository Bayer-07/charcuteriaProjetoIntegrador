import { openDeleteProductModal } from "./renderButtons.js";

async function updateQty(itemId, delta) {
    const quantityNode = document.getElementById(`qty-${itemId}`);
    const priceNode = document.getElementById(`price-${itemId}`);

    if (!quantityNode || !priceNode) {
        console.error(`Required DOM nodes missing for itemId: ${itemId}`);
        return;
    }

    const currentQty = parseInt(quantityNode.textContent, 10);
    const parsedDelta = parseInt(delta, 10);
    const newQty = currentQty + parsedDelta;

    if (newQty <= 0) {
        openDeleteProductModal(itemId);
        return;
    }

    const url = `/cart/ajax/update-quantity?itemId=${itemId}&delta=${parsedDelta}`;

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'Accept': 'application/json'
            }
        });

        if (!response.ok) throw new Error(`Server error: ${response.status}`);

        const state = await response.json();

        if (state.success) {
            quantityNode.textContent = newQty;

            const rawUnitPriceString = priceNode.getAttribute('data-unit-price').toString().replace(',', '.');
            const unitPrice = parseFloat(rawUnitPriceString);
            const newSubtotal = unitPrice * newQty;

            priceNode.textContent = newSubtotal.toLocaleString('pt-BR', {
                style: 'currency',
                currency: 'BRL'
            });

            priceNode.setAttribute('data-current-subtotal', newSubtotal);

            updateGlobalTotal();
        }
    } catch (error) {
        console.error("Failed to update cart state: ", error);
    }
}

function updateGlobalTotal() {
    let total = 0;

    document.querySelectorAll('.item-subtotal').forEach(node => {
        let rawValue = node.getAttribute('data-current-subtotal') || node.getAttribute('data-unit-price');
        rawValue = rawValue.toString().replace(',', '.');
        total += parseFloat(rawValue);
    });

    const formattedTotal = total.toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    });

    // Patch ALL relevant summary nodes in the DOM in O(1) time
    const summarySubtotalNode = document.querySelector('.summary-subtotal');
    const finalTotalNode = document.querySelector('.total-value');

    if (summarySubtotalNode) summarySubtotalNode.textContent = formattedTotal;
    if (finalTotalNode) finalTotalNode.textContent = formattedTotal;
}

window.updateQty = updateQty;
window.updateGlobalTotal = updateGlobalTotal;

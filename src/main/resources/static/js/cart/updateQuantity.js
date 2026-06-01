/**
 * Updates item quantity via the AJAX-specific endpoint.
 */
async function updateQty(itemId, delta) {
    const url = `/cart/ajax/update-quantity?itemId=${itemId}&delta=${delta}`;

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
            const quantityNode = document.getElementById(`qty-${itemId}`);
            const priceNode = document.getElementById(`price-${itemId}`);

            if (quantityNode && priceNode) {
                // 1. Update Quantity
                const currentQty = parseInt(quantityNode.textContent, 10);
                const newQty = Math.max(0, currentQty + parseInt(delta, 10));
                quantityNode.textContent = newQty;

                // 2. Defensive Parsing: Handle potential Brazilian commas from Java
                const rawUnitPriceString = priceNode.getAttribute('data-unit-price').toString().replace(',', '.');
                const unitPrice = parseFloat(rawUnitPriceString);

                const newSubtotal = unitPrice * newQty;

                // 3. Update Item Subtotal UI
                priceNode.textContent = newSubtotal.toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                });

                // Store the new raw subtotal in a data attribute
                priceNode.setAttribute('data-current-subtotal', newSubtotal);

                // 4. Trigger global total recalculation
                updateGlobalTotal();
            }
        }
    } catch (error) {
        console.error("Failed to update cart state: ", error);
    }
}

/**
 * Calculates the global cart totals with defensive type coercion.
 */
function updateGlobalTotal() {
    let total = 0;

    // Iterate over all item subtotals
    document.querySelectorAll('.item-subtotal').forEach(node => {
        let rawValue = node.getAttribute('data-current-subtotal') || node.getAttribute('data-unit-price');
        // Ensure decimal consistency before parsing
        rawValue = rawValue.toString().replace(',', '.');
        total += parseFloat(rawValue);
    });

    // Format the final value
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

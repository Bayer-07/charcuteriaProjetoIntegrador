document.addEventListener('DOMContentLoaded', async function () {
    const container = document.getElementById('featured-products-list');

    if (!container) return;

    function formatPrice(value) {
        return Number(value).toLocaleString('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        });
    }

    function getImagePath(imagePath) {
        if (!imagePath || imagePath.trim() === '') return '/images/logo.png';
        if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) return imagePath;
        if (imagePath.startsWith('/')) return imagePath;
        return '/uploads/products/' + imagePath;
    }

    function truncateText(text, maxLength) {
        if (!text) return 'Selecao premium da Koch Charcutaria para sua experiencia gastronomica.';
        if (text.length <= maxLength) return text;
        return text.slice(0, maxLength - 3).trim() + '...';
    }

    function renderProducts(products) {
        if (!Array.isArray(products) || products.length === 0) {
            container.innerHTML = '<p class="featured-products-empty">Nenhum produto encontrado no momento.</p>';
            return;
        }

        container.innerHTML = products.map(function (product) {
            return '<article class="product-card">' +
                '<img class="product-image" src="' + getImagePath(product.imagePath) + '" alt="' + (product.name || 'Produto') + '">' +
                '<div class="product-content">' +
                '<h3>' + (product.name || 'Produto') + '</h3>' +
                '<p>' + truncateText(product.description, 72) + '</p>' +
                '<div class="product-footer">' +
                '<span class="product-price">' + formatPrice(product.price || 0) + '</span>' +
                '<form action="/cart/add" method="post">' +
                '<input type="hidden" name="productId" value="' + (product.id || '') + '" />' +
                '<button type="submit" class="add-button">Adicionar</button>' +
                '</form>' +
                '</div>' +
                '</div>' +
                '</article>';
        }).join('');
    }

    try {
        const response = await fetch('/index/top-products');
        if (!response.ok) throw new Error('Falha ao carregar produtos');

        const products = await response.json();
        renderProducts(products);
    } catch (error) {
        container.innerHTML = '<p class="featured-products-empty">Nao foi possivel carregar os produtos agora.</p>';
        console.error(error);
    }
});

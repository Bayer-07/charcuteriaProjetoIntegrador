// Função de busca
const searchInput = document.getElementById('searchInput');
const filterButtons = document.querySelectorAll('.filter-btn');
const productCards = document.querySelectorAll('.product-card');

function filterProducts() {
    const searchTerm = searchInput.value.toLowerCase();
    const activeFilter = document.querySelector('.filter-btn.active').dataset.filter;

    productCards.forEach(card => {
        const productName = card.querySelector('.product-name').textContent.toLowerCase();
        const productDescription = card.querySelector('.product-description').textContent.toLowerCase();
        const productCategory = card.dataset.category;

        const matchesSearch = productName.includes(searchTerm) || productDescription.includes(searchTerm);
        const matchesFilter = activeFilter === 'todos' || productCategory === activeFilter;

        if (matchesSearch && matchesFilter) {
            card.style.display = 'flex';
        } else {
            card.style.display = 'none';
        }
    });
}

// Event listeners para busca
searchInput.addEventListener('input', filterProducts);

// Event listeners para filtros
filterButtons.forEach(button => {
    button.addEventListener('click', (e) => {
        filterButtons.forEach(btn => btn.classList.remove('active'));
        e.target.classList.add('active');
        filterProducts();
    });
});
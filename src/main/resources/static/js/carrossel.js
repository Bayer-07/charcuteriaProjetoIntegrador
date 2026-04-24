document.addEventListener('DOMContentLoaded', function () {
    const slides = document.querySelectorAll('.carousel-slide');
    const indicators = document.querySelectorAll('.indicator');
    const prevBtn = document.querySelector('.carousel-control.prev');
    const nextBtn = document.querySelector('.carousel-control.next');
    const carousel = document.querySelector('.hero-carousel');
    let currentIndex = 0;
    let autoplay;
    let isTransitioning = false;

    function showSlide(index) {
        if (isTransitioning) return;
        isTransitioning = true;

        const prevIndex = currentIndex;
        currentIndex = (index + slides.length) % slides.length;

        if (prevIndex === currentIndex) {
            isTransitioning = false;
            return;
        }

        const currentSlide = slides[prevIndex];
        const nextSlide = slides[currentIndex];

        nextSlide.classList.add('entering');
        nextSlide.classList.add('active');
        nextSlide.setAttribute('aria-hidden', 'false');

        nextSlide.getBoundingClientRect();

        currentSlide.classList.add('leaving');
        nextSlide.classList.remove('entering');

        const TRANSITION_DURATION = 600;

        setTimeout(() => {
            currentSlide.classList.remove('active', 'leaving');
            currentSlide.setAttribute('aria-hidden', 'true');
            isTransitioning = false;
        }, TRANSITION_DURATION);

        indicators.forEach((dot, i) => {
            const isActive = i === currentIndex;
            dot.classList.toggle('active', isActive);
            dot.setAttribute('aria-selected', String(isActive));
        });
    }

    function startAutoplay() {
        stopAutoplay();
        autoplay = setInterval(function () {
            showSlide(currentIndex + 1);
        }, 5000);
    }

    function stopAutoplay() {
        clearInterval(autoplay);
    }

    prevBtn.addEventListener('click', function () {
        showSlide(currentIndex - 1);
        startAutoplay();
    });

    nextBtn.addEventListener('click', function () {
        showSlide(currentIndex + 1);
        startAutoplay();
    });

    indicators.forEach((dot) => {
        dot.addEventListener('click', function () {
            showSlide(Number(dot.dataset.slide));
            startAutoplay();
        });
    });

    carousel.addEventListener('mouseenter', stopAutoplay);
    carousel.addEventListener('mouseleave', startAutoplay);

    showSlide(0);
    startAutoplay();
});
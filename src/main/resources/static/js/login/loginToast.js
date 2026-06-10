document.addEventListener('DOMContentLoaded', () => {
    const toast = document.getElementById('toast-error');

    if (toast) {
        setTimeout(() => {
            toast.style.opacity = '0';

            setTimeout(() => {
                toast.remove();
            }, 500);
        }, 3000);
    }
});
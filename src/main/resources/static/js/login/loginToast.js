console.log("Js carregado");

document.addEventListener('DOMContentLoaded', () => {
    const toast = document.getElementById('toast-error');
    console.log(toast);

    if (toast) {
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transition = '0.5s';

            setTimeout(() => {
                toast.remove();
            }, 500);
        }, 3000);
    }
});
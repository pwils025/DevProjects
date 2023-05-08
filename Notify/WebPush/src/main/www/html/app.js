const publicVapidKey = 'BAe_UlsoXEWHi7dQV0qPvd-qoACUTk6tui_hSrNi855Sr-AbUPagBTRy1gVcc11cIXBdaM5o1RdaCaqBP_Qydnw=';
const subscribeButton = document.getElementById('subscribe');

async function subscribe() {
    const registration = await navigator.serviceWorker.register('service-worker.js', {
        scope: '/'
    });

    const subscription = await registration.pushManager.subscribe({
        userVisibleOnly: true,
        applicationServerKey: urlBase64ToUint8Array(publicVapidKey)
    });

    await fetch('http://localhost:8080/subscribe', {
        method: 'POST',
        body: JSON.stringify(subscription),
        headers: {
            'Content-Type': 'application/json'
        }
    });

    console.log('Subscribed:', subscription);
}

function urlBase64ToUint8Array(base64String) {
    const padding = '='.repeat((4 - base64String.length % 4) % 4);
    const base64 = (base64String + padding)
        .replace(/-/g, '+')
        .replace(/_/g, '/');
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
}

subscribeButton.addEventListener('click', subscribe);

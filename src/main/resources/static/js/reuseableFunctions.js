

export function showLoader(containerId = "app") {
    const container = document.getElementById(containerId);

    // Avoid adding multiple loaders
    if (document.getElementById("loader")) return;

    const loaderSection = document.createElement("section");
    loaderSection.id = "loader";
    loaderSection.classList.add("loader-section");

    // Create 5 sliders with unique classes
    for (let i = 0; i < 5; i++) {
        const slider = document.createElement("div");
        slider.classList.add("slider", `slider-${i}`);
        loaderSection.appendChild(slider);
    }

    container.appendChild(loaderSection);
}

// Hide loader
export function hideLoader() {
    const loader = document.getElementById("loader");
    if (loader) loader.remove();
}


export function showPopUp(message, duration = 3000) {
    // Create popup container
    const popup = document.createElement("div");
    popup.classList.add("popup-message");
    popup.textContent = message;

    // Append to body
    document.body.appendChild(popup);

    // Trigger fade-in
    setTimeout(() => {
        popup.classList.add("visible");
    }, 50); // small delay to allow CSS transition

    // Remove popup after duration
    setTimeout(() => {
        popup.classList.remove("visible");
        // Remove from DOM after fade-out transition
        setTimeout(() => {
            popup.remove();
        }, 500); // match fade-out duration in CSS
    }, duration);
}
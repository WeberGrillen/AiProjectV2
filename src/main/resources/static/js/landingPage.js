import { displayStoryPicker } from "./storyPicker.js";
import {showPopUp} from "./reuseableFunctions.js";

const app = document.getElementById("app")

export async function loadLandingPage(popUpMessage) {
    app.innerHTML = ""

    if (popUpMessage) {
        showPopUp(popUpMessage)
    }

    // Hero Container
    const heroContainer = document.createElement("div")
    heroContainer.classList.add("hero")

    // StoryCraft label
    const heroLabel = document.createElement("label")
    heroLabel.textContent = "StoryCraft"

    heroLabel.classList.add("hero-label", "fade-up")
    heroContainer.appendChild(heroLabel)
    app.appendChild(heroContainer)

    heroLabel.style.opacity = 1


    // Wrapper til textarea + button
    const inputWrapper = document.createElement("div")
    inputWrapper.classList.add("input-wrapper", "fade-up")


    // Textarea
    const heroInput = document.createElement("textarea")
    heroInput.placeholder = "Write anything to craft your own story"
    heroInput.classList.add("hero-textarea")
    heroInput.rows = 4;
    inputWrapper.appendChild(heroInput)


    // Submit button
    const submitButtonDiv = document.createElement("div")
    submitButtonDiv.classList.add("submit-button-div")
    inputWrapper.appendChild(submitButtonDiv)

    const submitBtn = document.createElement("button")
    submitBtn.classList.add("submit-btn");
    submitBtn.addEventListener("click", () => {
        console.log("Send clicked!");
    });

    const submitBtnText = document.createElement("a")
    submitBtnText.textContent = "Create Story"
    submitBtn.appendChild(submitBtnText)

    submitButtonDiv.appendChild(submitBtn)

    heroContainer.appendChild(inputWrapper) // wrapperen tilføjes til heroContainer


    submitBtn.addEventListener("click", async ()=> {
        const prompt = heroInput.value.trim()
        if (!prompt.trim()) return alert("You need input to make a story")
        return displayStoryPicker(prompt)

    });




    // Få elementer til at fade ind **sekventielt**
    setTimeout(() => {
        heroLabel.classList.add("visible") // først label
    }, 700) // start lidt efter siden loader

    setTimeout(() => {
        inputWrapper.classList.add("visible") // derefter textarea + knap
    }, 1000)





}
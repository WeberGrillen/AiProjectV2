import {hideLoader, showLoader} from "./reuseableFunctions.js";
import {loadLandingPage} from "./landingPage.js";

const app = document.getElementById("app")

export async function displayStoryPicker(prompt) {
    app.innerHTML = ""

    console.log("Sending prompt to backend: " + prompt)

    showLoader();

    const response = await fetch("http://localhost:8080/api/story", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify( { prompt })
    });

    hideLoader()


    if (!response.ok) {
        return loadLandingPage(await response.text())
    }

    const stories = await response.json()
    console.log("Received stories" + stories)


    // --- UI setup ---
    const storypickerPage = document.createElement("div")
    storypickerPage.classList.add("page-storypicker")

    // Header
    const header = document.createElement("div")
    header.classList.add("header")
    storypickerPage.appendChild(header)

    // StoryCraft label
    const heroLabel = document.createElement("label")
    heroLabel.textContent = "StoryCraft"
    heroLabel.classList.add("storypicker-label")
    header.appendChild(heroLabel)


    // Hero container
    const herocontainer = document.createElement("div")
    herocontainer.classList.add("hero-storypicker")


    // === Dynamisk oprettelse af kort ===
    stories.forEach((story, index) => {
        const storyCard = document.createElement("div")
        storyCard.classList.add("story-card")

        const cardLabel = document.createElement("label")
        cardLabel.classList.add("card-label")
        cardLabel.textContent = story.title

        const tldrLabel = document.createElement("label")
        tldrLabel.classList.add("card-tldr-label")
        tldrLabel.textContent = "TL:DR"

        const tldr = document.createElement("textarea")
        tldr.classList.add("card-tldr")
        tldr.textContent = story.tldr

        const storyLabel = document.createElement("label")
        storyLabel.classList.add("card-story-label")
        storyLabel.textContent = "Story"

        const mainStory = document.createElement("textarea")
        mainStory.classList.add("card-story")
        mainStory.textContent = story.story

        const cardBtn = document.createElement("button")
        cardBtn.classList.add("card-button")

        const cardBtnText = document.createElement("a")
        cardBtnText.textContent = "Pick Story"
        cardBtn.appendChild(cardBtnText)


        storyCard.appendChild(cardLabel);
        storyCard.appendChild(tldrLabel)
        storyCard.appendChild(tldr);
        storyCard.appendChild(storyLabel)
        storyCard.appendChild(mainStory)
        storyCard.appendChild(cardBtn);


        herocontainer.appendChild(storyCard);


        cardBtn.addEventListener("click", async () => {
            console.log(`🪄 Picked story #${index + 1}:`, story.title);

            // 1️⃣ Fjern alt indhold fra app
            app.innerHTML = "";

            // 2️⃣ Vis loader
            showLoader();

            try {
                // 3️⃣ Send request til backend for at lave video med TTS
                const response = await fetch("http://localhost:8080/api/tts/video", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        videoName: "MinecraftParkour.mp4",  // brug det rigtige videonavn
                        storyText: story.story
                    })
                });

                if (!response.ok) throw new Error("Failed to generate video");

                const blob = await response.blob(); // få video som blob
                const url = URL.createObjectURL(blob);

                // 4️⃣ Lav et midlertidigt download-link og klik på det
                const a = document.createElement("a");
                a.href = url;
                a.download = `video-${story.title}.mp4`;
                document.body.appendChild(a);
                a.click();
                a.remove();
                URL.revokeObjectURL(url);

            } catch (err) {
                console.error("❌ Error downloading video:", err);
                alert("Failed to download video");
            } finally {
                // 5️⃣ Fjern loader, evt. gå tilbage til landing page eller vis en succesbesked
                hideLoader();
                loadLandingPage(); // hvis du vil gå tilbage til landing page
            }
        });






    })
    storypickerPage.appendChild(herocontainer)
    app.appendChild(storypickerPage)



}
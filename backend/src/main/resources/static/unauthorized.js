document.addEventListener("DOMContentLoaded", async () => {
  const infoEl = document.getElementById("location-info");
  const audio = new Audio("/assets/warning.mp3"); // Make sure this is in resources/static/assets/

  try {
    const res = await fetch("https://ipinfo.io/json?token=0d3757cbb90dba");
    const data = await res.json();
    const { ip, city, region, country, org, loc } = data;

    const [latitude, longitude] = loc.split(',');

    infoEl.innerHTML = `
      <p><strong>IP:</strong> ${ip}</p>
      <p><strong>Location:</strong> ${city}, ${region}, ${country}</p>
      <p><strong>ISP:</strong> ${org}</p>
      <p><strong>Coordinates:</strong> ${latitude}, ${longitude}</p>
    `;

    // Log to backend
    await fetch("https://uhs-backend.onrender.com/api/log-ip", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ip, city, region, country, org, latitude, longitude })
    });
  } catch (err) {
    infoEl.textContent = "Could not retrieve your location.";
    console.error("Location fetch error:", err);
  }

  // 🔐 Require user interaction to play warning
  const triggerWarning = () => {
    audio.volume = 1.0;
    audio.play().catch(err => console.warn("Audio play error:", err));
    document.removeEventListener("click", triggerWarning);
    document.removeEventListener("mousemove", triggerWarning);
  };

  document.addEventListener("click", triggerWarning);
  document.addEventListener("mousemove", triggerWarning);
});

document.addEventListener("DOMContentLoaded", async function () {
  const locationEl = document.getElementById("locationDisplay");

  try {
    const res = await fetch("https://ipinfo.io/json?token=0d3757cbb90dba");
    const data = await res.json();

    const { ip, city, region, country, org, postal } = data;
    locationEl.innerHTML = `
      <strong>IP:</strong> ${ip}<br/>
      <strong>Location:</strong> ${city}, ${region}, ${postal}, ${country}<br/>
      <strong>ISP:</strong> ${org}<br/>
      <br/><em>This incident will be reported.</em>
    `;
  } catch (err) {
    locationEl.textContent = "Unable to fetch your location. But you're still being watched.";
  }
});

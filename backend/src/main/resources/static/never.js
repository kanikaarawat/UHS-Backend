document.addEventListener("DOMContentLoaded", function () {
    const quotes = [
      "Patience, dear traveler — this path leads nowhere.",
      "Reload if you must, but the end shall never come.",
      "This page is but a mirage; seek no answers here.",
      "A thousand clicks won't break the spell.",
      "Wanderer beware — this page is cursed to never bloom."
    ];
  
    const quoteEl = document.getElementById("quoteDisplay");
    if (quoteEl) {
      quoteEl.style.opacity = 0;
      const random = Math.floor(Math.random() * quotes.length);
      quoteEl.textContent = quotes[random];
      setTimeout(() => quoteEl.style.opacity = 1, 100); // fade in
    }
  });
  
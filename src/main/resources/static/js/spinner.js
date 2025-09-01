document.addEventListener("DOMContentLoaded", () => {
  const spinnerOverlay = document.getElementById("spinner-overlay");

  function showSpinner() {
    if (!spinnerOverlay) return;
    spinnerOverlay.style.display = "flex";   // make visible
    setTimeout(() => spinnerOverlay.classList.add("active"), 10); // fade in
  }

  function hideSpinner() {
    if (!spinnerOverlay) return;
    spinnerOverlay.classList.remove("active");
    setTimeout(() => {
      spinnerOverlay.style.display = "none";
    }, 300); // wait for fade-out
  }

  // ✅ Show spinner on form submissions
  document.querySelectorAll("form").forEach(form => {
    form.addEventListener("submit", () => {
      showSpinner();
    });
  });

  // ✅ Show spinner on pagination & action links
  document.querySelectorAll("a").forEach(link => {
    const href = link.getAttribute("href");
    if (href && !href.startsWith("#") && !href.startsWith("javascript")) {
      link.addEventListener("click", (e) => {
        showSpinner();
      });
    }
  });

  // Expose globally if needed
  window.showSpinner = showSpinner;
  window.hideSpinner = hideSpinner;
});

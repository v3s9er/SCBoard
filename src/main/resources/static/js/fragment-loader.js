document.addEventListener("DOMContentLoaded", async () => {
  const header = document.querySelector('[data-fragment-target="header"]');
  if (!header) return;

  try {
    const response = await fetch("/fragment", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: new URLSearchParams({ section: "header" })
    });
    if (!response.ok) throw new Error("Failed to load header fragment");

    header.outerHTML = await response.text();
  } catch (error) {
    console.error("Header fragment could not be refreshed.", error);
  }
});

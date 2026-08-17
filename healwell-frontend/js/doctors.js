document.addEventListener("DOMContentLoaded", async () => {
  const grid = document.getElementById("doctors-grid");
  const statusEl = document.getElementById("doctors-status");
  const specialtySelect = document.getElementById("specialty-select");

  let allDoctors = [];

  // Read ?specialty=... from URL, if present
  const urlParams = new URLSearchParams(window.location.search);
  const initialSpecialty = urlParams.get("specialty") || "";

  function showStatus(message, isError = false) {
    statusEl.textContent = message;
    statusEl.className = isError ? "doctors-status error" : "doctors-status";
  }

  function renderDoctors(doctors) {
    grid.innerHTML = "";

    if (doctors.length === 0) {
      showStatus("No doctors found for this specialty.");
      return;
    }

    showStatus("");

    doctors.forEach((doc) => {
      const card = document.createElement("div");
      card.className = "doctor-card";
      card.innerHTML = `
        <div class="doctor-card-header">
          <h3>${doc.name}</h3>
          <span class="doctor-specialty">${doc.specialization}</span>
        </div>
        <p class="doctor-qualification">${doc.qualification}</p>
        <p class="doctor-experience">${doc.experienceYears} years experience</p>
        <p class="doctor-fee">Consultation fee: ₹${doc.consultationFee}</p>
        <button class="btn btn-primary btn-full" onclick="window.location.href='doctor-detail.html?id=${doc.id}'">
          View Profile
        </button>
      `;
      grid.appendChild(card);
    });
  }

  function populateSpecialtyDropdown(doctors) {
    const specialties = [...new Set(doctors.map((d) => d.specialization))].sort();
    specialties.forEach((spec) => {
      const option = document.createElement("option");
      option.value = spec;
      option.textContent = spec;
      specialtySelect.appendChild(option);
    });

    if (initialSpecialty && specialties.includes(initialSpecialty)) {
      specialtySelect.value = initialSpecialty;
    }
  }

  function applyFilter() {
    const selected = specialtySelect.value;
    const filtered = selected
      ? allDoctors.filter((d) => d.specialization === selected)
      : allDoctors;
    renderDoctors(filtered);
  }

  specialtySelect.addEventListener("change", applyFilter);

  try {
    showStatus("Loading doctors...");
    allDoctors = await apiRequest("/doctors");
    populateSpecialtyDropdown(allDoctors);
    applyFilter(); // renders with initial specialty filter (from URL) if present
  } catch (err) {
    showStatus("Could not load doctors. Please try again later.", true);
  }
});
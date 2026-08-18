document.addEventListener("DOMContentLoaded", async () => {
  const statusEl = document.getElementById("detail-status");
  const contentEl = document.getElementById("doctor-detail-content");

  const urlParams = new URLSearchParams(window.location.search);
  const doctorId = urlParams.get("id");

  function showStatus(message, isError = false) {
    statusEl.textContent = message;
    statusEl.className = isError ? "detail-status error" : "detail-status";
  }

  if (!doctorId) {
    showStatus("No doctor selected. Please go back and choose a doctor.", true);
    return;
  }

  function renderBookingSection(doctorId, availabilitySlots) {
    const token = localStorage.getItem("healwell_token");

    if (!token) {
      return `
        <div class="booking-card">
          <h2>Book an Appointment</h2>
          <p class="booking-login-prompt">
            Please <a href="login.html">log in</a> to book an appointment.
          </p>
        </div>
      `;
    }

    return `
      <div class="booking-card">
        <h2>Book an Appointment</h2>
        <div class="booking-form">
          <label for="booking-date">Select a date</label>
          <input type="date" id="booking-date" min="${new Date().toISOString().split("T")[0]}">
          <div id="slots-container" class="slots-container"></div>
          <div id="booking-message" class="booking-message"></div>
        </div>
      </div>
    `;
  }

  function renderDoctor(doctor, slots) {
    contentEl.innerHTML = `
      <div class="doctor-profile-card">
        <h1>${doctor.name}</h1>
        <span class="doctor-specialty">${doctor.specialization}</span>
        <p class="doctor-qualification">${doctor.qualification}</p>
        <p class="doctor-experience">${doctor.experienceYears} years experience</p>
        <p class="doctor-fee">Consultation fee: ₹${doctor.consultationFee}</p>
        <p class="doctor-bio">${doctor.bio}</p>
      </div>

      ${renderBookingSection(doctorId, slots)}
    `;
  }

  try {
    showStatus("Loading doctor profile...");
    const [doctor, slots] = await Promise.all([
      apiRequest(`/doctors/${doctorId}`),
      apiRequest(`/availability/${doctorId}`)
    ]);
    showStatus("");
    renderDoctor(doctor, slots);
  } catch (err) {
    showStatus("Could not load doctor profile. Please try again later.", true);
  }

    function formatTime12hr(time24) {
    const [hourStr, minute] = time24.split(":");
    let hour = parseInt(hourStr, 10);
    const period = hour >= 12 ? "PM" : "AM";
    hour = hour % 12;
    if (hour === 0) hour = 12;
    return `${hour}:${minute} ${period}`;
  }
});
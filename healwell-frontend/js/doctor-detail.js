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
          <button type="button" id="confirm-booking-btn" class="btn btn-primary" style="display:none; margin-top:16px;" disabled>Confirm Booking</button>
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

    const dateInput = document.getElementById("booking-date");
    if (dateInput) {
      dateInput.addEventListener("change", async () => {
        await renderTimeSlots(dateInput.value, slots);
      });
    }
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

  function getDayName(dateStr) {
    const days = ["SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"];
    const d = new Date(dateStr + "T00:00:00");
    return days[d.getDay()];
  }

  async function renderTimeSlots(dateStr, weeklySlots) {
    const container = document.getElementById("slots-container");
    const confirmBtn = document.getElementById("confirm-booking-btn");
    if (!container) return;

    container.innerHTML = "Loading slots...";
    confirmBtn.style.display = "none";
    confirmBtn.disabled = true;

    const dayName = getDayName(dateStr);
    const dayBlocks = weeklySlots.filter((s) => s.dayOfWeek === dayName);

    if (dayBlocks.length === 0) {
      container.innerHTML = `<p class="no-slots">No availability on this day.</p>`;
      return;
    }

    let bookedTimes = [];
    try {
      const response = await apiRequest(`/appointments/doctor/${doctorId}/booked-slots?date=${dateStr}`);
      bookedTimes = response.bookedTimes;
    } catch (err) {
      container.innerHTML = `<p class="no-slots">Could not load slot availability.</p>`;
      return;
    }

    const now = new Date();
    const isToday = dateStr === now.toISOString().split("T")[0];

    const allSlots = [];
    dayBlocks.forEach((block) => {
      let [h, m] = block.startTime.split(":").map(Number);
      const [endH, endM] = block.endTime.split(":").map(Number);

      while (h < endH || (h === endH && m < endM)) {
        const slotTime = `${String(h).padStart(2, "0")}:${String(m).padStart(2, "0")}:00`;
        allSlots.push(slotTime);

        m += block.slotDurationMinutes;
        while (m >= 60) {
          m -= 60;
          h += 1;
        }
      }
    });

    container.innerHTML = "";
    let anyVisible = false;

    allSlots.forEach((slotTime) => {
      if (isToday) {
        const [sh, sm] = slotTime.split(":").map(Number);
        const slotDate = new Date();
        slotDate.setHours(sh, sm, 0, 0);
        if (slotDate <= now) return;
      }

      anyVisible = true;
      const isBooked = bookedTimes.includes(slotTime);

      const btn = document.createElement("button");
      btn.type = "button";
      btn.textContent = isBooked ? "Booked" : formatTime12hr(slotTime);
      btn.className = isBooked ? "slot-btn slot-booked" : "slot-btn slot-free";
      btn.disabled = isBooked;

      if (!isBooked) {
        btn.addEventListener("click", () => {
          document.querySelectorAll(".slot-btn.selected").forEach((b) => b.classList.remove("selected"));
          btn.classList.add("selected");
          confirmBtn.style.display = "inline-block";
          confirmBtn.disabled = false;
          confirmBtn.dataset.selectedTime = slotTime;
          confirmBtn.dataset.selectedDate = dateStr;
        });
      }

      container.appendChild(btn);
    });

    if (!anyVisible) {
      container.innerHTML = `<p class="no-slots">No remaining slots for this day.</p>`;
    }
  }

  async function confirmBooking() {
    const confirmBtn = document.getElementById("confirm-booking-btn");
    const messageEl = document.getElementById("booking-message");
    const token = localStorage.getItem("healwell_token");

    const date = confirmBtn.dataset.selectedDate;
    const time = confirmBtn.dataset.selectedTime;

    if (!date || !time) {
      messageEl.textContent = "Please select a time slot first.";
      messageEl.className = "booking-message error";
      return;
    }

    const dateTime = `${date}T${time}`;

    confirmBtn.disabled = true;
    confirmBtn.textContent = "Booking...";
    messageEl.textContent = "";

    try {
      await apiRequest(`/appointments/book/${doctorId}`, "POST", { dateTime }, token);
      messageEl.textContent = "Appointment booked successfully!";
      messageEl.className = "booking-message success";
      confirmBtn.style.display = "none";

      const dateInput = document.getElementById("booking-date");
      const [doctor, slots] = await Promise.all([
        apiRequest(`/doctors/${doctorId}`),
        apiRequest(`/availability/${doctorId}`)
      ]);
      await renderTimeSlots(dateInput.value, slots);
    } catch (err) {
      messageEl.textContent = err.message || "Could not book this appointment. Please try another slot.";
      messageEl.className = "booking-message error";
      confirmBtn.disabled = false;
      confirmBtn.textContent = "Confirm Booking";
    }
  }

  document.addEventListener("click", (e) => {
    if (e.target && e.target.id === "confirm-booking-btn") {
      confirmBooking();
    }
  });
});
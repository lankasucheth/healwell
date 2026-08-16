const specialties = [
  { name: "Cardiologist", description: "Heart & cardiovascular care" },
  { name: "Dermatologist", description: "Skin, hair & nail health" },
  { name: "Pediatrician", description: "Child & infant care" },
  { name: "Orthopedic", description: "Bone & joint specialists" },
  { name: "Gynecologist", description: "Women's health care" },
  { name: "Neurologist", description: "Nervous system specialists" },
  { name: "ENT Specialist", description: "Ear, nose & throat care" },
  { name: "General Physician", description: "Everyday health concerns" },
  { name: "Psychiatrist", description: "Mental health & wellbeing" },
  { name: "Dentist", description: "Dental & oral care" }
];

function renderSpecialties() {
  const grid = document.getElementById("specialty-grid");
  if (!grid) return;

  grid.innerHTML = specialties.map(spec => `
    <div class="specialty-card" data-specialty="${spec.name}">
      <h3>${spec.name}</h3>
      <p>${spec.description}</p>
    </div>
  `).join("");

  document.querySelectorAll(".specialty-card").forEach(card => {
    card.addEventListener("click", () => {
      const specialty = card.getAttribute("data-specialty");
      window.location.href = `pages/doctors.html?specialty=${encodeURIComponent(specialty)}`;
    });
  });
}

document.addEventListener("DOMContentLoaded", renderSpecialties);
document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("healwell_token");
  if (!token) return; // not logged in, leave Login/Sign Up buttons as-is

  const navActions = document.querySelector(".navbar-actions");
  if (!navActions) return;

  const loginBtn = navActions.querySelector(".nav-link-text");
  const signupBtn = navActions.querySelector(".btn-primary");

  // Decode JWT payload (2nd part, base64url encoded)
  function decodeToken(t) {
    try {
      const payload = t.split(".")[1];
      const decoded = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
      return JSON.parse(decoded);
    } catch (e) {
      return null;
    }
  }

  const claims = decodeToken(token);
  if (!claims) return; // invalid/corrupt token, leave default buttons

  const email = claims.sub || "user";
  const role = claims.role || "";
  const displayName = email.split("@")[0]; // placeholder until /me endpoint exists
  const initial = displayName.charAt(0).toUpperCase();

  if (loginBtn) loginBtn.style.display = "none";
  if (signupBtn) signupBtn.style.display = "none";

  const userMenu = document.createElement("div");
  userMenu.className = "user-menu";
  userMenu.innerHTML = `
    <button class="user-menu-trigger" id="userMenuTrigger">
      <span class="avatar-circle">${initial}</span>
      <span class="user-name">${displayName}</span>
      <span class="dropdown-arrow">&#9662;</span>
    </button>
    <div class="user-dropdown" id="userDropdown">
      <a href="pages/profile.html">Profile</a>
      <a href="#appointments">My Appointments</a>
      <button id="logoutBtn" type="button">Logout</button>
    </div>
  `;

  navActions.appendChild(userMenu);

  const trigger = document.getElementById("userMenuTrigger");
  const dropdown = document.getElementById("userDropdown");

  trigger.addEventListener("click", () => {
    dropdown.classList.toggle("open");
  });

  document.addEventListener("click", (e) => {
    if (!userMenu.contains(e.target)) {
      dropdown.classList.remove("open");
    }
  });

  document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.removeItem("healwell_token");
    window.location.href = "index.html";
  });
});
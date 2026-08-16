document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("login-form");
  const submitBtn = document.getElementById("submit-btn");
  const formMessage = document.getElementById("form-message");

  const fields = {
    email: { el: document.getElementById("email"), error: document.getElementById("email-error") },
    password: { el: document.getElementById("password"), error: document.getElementById("password-error") }
  };

  function validateField(key) {
    const { el, error } = fields[key];
    const value = el.value.trim();
    let message = "";

    if (!value) {
      message = `Please enter your ${key}`;
    } else if (key === "email" && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
      message = "Please enter a valid email address";
    }

    error.textContent = message;
    el.classList.toggle("input-error", !!message);
    return !message;
  }

  Object.keys(fields).forEach(key => {
    fields[key].el.addEventListener("blur", () => validateField(key));
  });

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    formMessage.textContent = "";
    formMessage.className = "form-message";

    const allValid = Object.keys(fields).every(key => validateField(key));
    if (!allValid) return;

    submitBtn.disabled = true;
    submitBtn.textContent = "Logging in...";

    try {
      const data = await apiRequest("/auth/login", "POST", {
        email: fields.email.el.value.trim(),
        password: fields.password.el.value
      });

      const parsed = typeof data === "string" ? JSON.parse(data) : data;
      localStorage.setItem("healwell_token", parsed.token);

      formMessage.textContent = "Login successful! Redirecting...";
      formMessage.classList.add("success");

      setTimeout(() => {
        window.location.href = "../index.html";
      }, 1000);

    } catch (err) {
      formMessage.textContent = err.message || "Invalid email or password";
      formMessage.classList.add("error");
      submitBtn.disabled = false;
      submitBtn.textContent = "Login";
    }
  });
});
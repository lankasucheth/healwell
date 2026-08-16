document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("signup-form");
  const submitBtn = document.getElementById("submit-btn");
  const formMessage = document.getElementById("form-message");

  const fields = {
    name: { el: document.getElementById("name"), error: document.getElementById("name-error") },
    email: { el: document.getElementById("email"), error: document.getElementById("email-error") },
    phoneNumber: { el: document.getElementById("phoneNumber"), error: document.getElementById("phoneNumber-error") },
    password: { el: document.getElementById("password"), error: document.getElementById("password-error") }
  };

  function validateField(key) {
    const { el, error } = fields[key];
    const value = el.value.trim();
    let message = "";

    if (!value) {
      message = `Please enter your ${key === "phoneNumber" ? "phone number" : key}`;
    } else if (key === "email" && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
      message = "Please enter a valid email address";
    } else if (key === "phoneNumber" && !/^[0-9]{10}$/.test(value)) {
      message = "Phone number must be exactly 10 digits";
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
    submitBtn.textContent = "Creating account...";

    try {
      await apiRequest("/auth/signup", "POST", {
        name: fields.name.el.value.trim(),
        email: fields.email.el.value.trim(),
        phoneNumber: fields.phoneNumber.el.value.trim(),
        password: fields.password.el.value
      });

      formMessage.textContent = "Account created! Redirecting to login...";
      formMessage.classList.add("success");

      setTimeout(() => {
        window.location.href = "login.html";
      }, 1500);

    } catch (err) {
      formMessage.textContent = err.message;
      formMessage.classList.add("error");
      submitBtn.disabled = false;
      submitBtn.textContent = "Sign Up";
    }
  });
});
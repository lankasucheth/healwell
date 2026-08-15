# HealWell — Progress & Memory Log

## Project overview
- Name: HealWell (Patients & Appointments system for doctors and users)
- Started: 2026-08-16

## Locked-in tech stack
- Backend: Java 26 (installed), Spring Boot 4.1.0, Maven
- ORM: Hibernate (Spring Data JPA)
- Database: MySQL 9.3 (local, root, 127.0.0.1:3306)
- Auth: Spring Security + JWT (jjwt) + BCrypt
- Frontend: Plain HTML/CSS/JS first, React added later
- API Testing: Postman
- Architecture: Monolith first, microservices as a later phase
- Build tool: Maven for this project, Gradle to be learned separately (next project)

## Key requirements decided so far
- One shared login page for Patient, Doctor, Admin (role-based redirect).
- Signup: Patients self-register. Doctors added by Admin panel (not public signup). Admin seeded manually.
- Patient/Doctor can edit/delete only their own appointments.
- Admin can manage Doctors, oversee Patients, view ALL appointments system-wide.
- Appointment date/time picker: past dates fully blocked; today's past hours blocked; enforced on BOTH frontend and backend.
- CRUD operations required on all entities (Patients, Doctors, Appointments) — core learning goal, tested via Postman before frontend.

## Session log

### 2026-08-16 — Project kickoff
- Discussed and locked full tech stack (see above).
- Verified installed tools: Java 26.0.1, Maven 3.9.16, Git 2.55.0, VS Code 1.133.0, MySQL Server 9.3 (via Workbench connection "Local", 127.0.0.1:3306).
- Created project folder: D:\healwell
- Ran `git init`, renamed default branch from `master` to `main`.
- Created RULES.md and PROGRESS.md.

## Mistakes & fixes log
(none yet)

## Open questions / not yet decided
- Exact fields for Users, Patient, Doctor, Appointment tables — not finalized yet.
- GitHub remote — not created yet, local-only for now.
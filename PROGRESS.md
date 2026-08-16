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

## Git / GitHub status
- Local repo: D:\healwell, branch main.
- GitHub remote: connected. Repo: github.com/lankasucheth/healwell (Public).
- First push done, commit 037b49b.
- Rule: never push without explicit user permission, every time.

## Session log

### 2026-08-16 — Project kickoff
- Discussed and locked full tech stack (see above).
- Verified installed tools: Java 26.0.1, Maven 3.9.16, Git 2.55.0, VS Code 1.133.0, MySQL Server 9.3 (via Workbench connection "Local", 127.0.0.1:3306).
- Created project folder: D:\healwell
- Ran git init, renamed default branch from master to main.
- Created RULES.md and PROGRESS.md.

### 2026-08-16 — First commit and GitHub connected
- Committed RULES.md + PROGRESS.md (commit 037b49b).
- Published local repo to GitHub via VS Code "Publish Branch".
- Repo: github.com/lankasucheth/healwell (Public).

## Mistakes & fixes log
(none yet)

## Open questions / not yet decided
- Exact fields for Users, Patient, Doctor, Appointment tables — not finalized yet.

## Database schema (finalized — Phase 2 complete)

### Users
- id (PK), name, email (unique, used for login), phoneNumber (10 digits),
  password (BCrypt hash), role (PATIENT/DOCTOR/ADMIN), isActive (default true), createdAt

### Patient (all fields optional, filled later in profile)
- id (PK), userId (FK -> Users), dateOfBirth, gender (MALE/FEMALE/OTHER),
  address, bloodGroup (A+/A-/B+/B-/O+/O-/AB+/AB-)

### Doctor (all fields mandatory, set by Admin at creation)
- id (PK), userId (FK -> Users), specialization, qualification,
  experienceYears, consultationFee, bio

### DoctorAvailability
- id (PK), doctorId (FK -> Doctor), dayOfWeek (MON-SUN), startTime, endTime,
  slotDurationMinutes, isAvailable (toggle without deleting schedule)

### Appointment
- id (PK), patientId (FK -> Patient), doctorId (FK -> Doctor), dateTime, status
- Rule: past dates/times blocked, enforced on BOTH frontend and backend
- Patient/Doctor can edit/delete only their own appointments

### MedicalRecord
- id (PK), patientId (FK -> Patient), doctorId (FK -> Doctor),
  appointmentId (FK -> Appointment), diagnosis, prescription, recordDate
- Only Doctor can create records. Patient can view own (read-only). Admin cannot view medical content.
- One row per visit -> patient history = all rows where patientId matches, sorted by recordDate
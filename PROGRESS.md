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

## Phase 3 complete — Backend skeleton
- Created healwell-backend via start.spring.io: Maven, Java 17 target, Spring Boot 4.1.0
- Dependencies: Spring Web, Spring Data JPA, Spring Security, MySQL Driver, Validation, DevTools
- Package structure: model, repository, service, controller, config (under com.healwell.healwell_backend)
- application.properties (real, git-ignored) holds DB credentials
- application.properties.example (tracked in git) holds placeholder template
- Created all 6 JPA entity classes in model/ (Users, Patient, Doctor, DoctorAvailability, Appointment, MedicalRecord)
- Created all 6 repository interfaces in repository/
- First successful run: connected to MySQL, auto-created healwell_db with all 6 tables,
  foreign keys and unique constraints applied correctly. Confirmed visually in MySQL Workbench.
- Note: Spring Security currently active with auto-generated random password (default behavior) —
  will be replaced with proper JWT-based auth in Phase 4.
  ## Mistakes & fixes log

### 2026-08-16 — Duplicate SecurityConfig class
- Accidentally created SecurityConfig.java twice: once correctly in config/, once mistakenly in controller/ (still carrying the config package declaration).
- Caused: "duplicate class: com.healwell.healwell_backend.config.SecurityConfig" compile error.
- Fix: deleted the misplaced file in controller/ via `Remove-Item`, kept the correct one in config/.
- Lesson: always double-check which folder is selected before "New File" — package declaration alone doesn't fix wrong file location.
- Postman: use a separate "HealWell" collection, distinct from old Meditrack collection. Claude reminds every time before testing.
### 2026-08-16 — JWT dependency not actually saved, then duplicated
- Added jjwt dependencies to pom.xml, but the file wasn't actually saved to disk before compiling —
  compile errors showed "package io.jsonwebtoken does not exist" even though the pasted content looked correct in chat.
- Verified using `Select-String -Path pom.xml -Pattern "jjwt"` — confirmed empty, proving the save never happened.
- Re-added the dependencies, but accidentally pasted them twice (duplicate entries).
- Fixed by fully replacing pom.xml content clean, then verified again with Select-String before compiling.
- Lesson: always verify a file's real content on disk via terminal (Select-String / cat) after
  editing critical config files, rather than trusting the VS Code tab's unsaved-dot indicator alone.
  Also: `./mvnw clean compile` (not just `compile`) is the reliable way to force Maven to fully
  re-check everything when something seems stuck.
  ## Phase 4 complete — Authentication
- BCrypt password hashing configured (SecurityConfig)
- Signup endpoint (/api/auth/signup) - tested, creates User + blank Patient row
- Login endpoint (/api/auth/login) - tested, returns valid JWT token
- Security rules: /api/auth/** is public, everything else requires authentication (not yet enforced per-role)
- Known limitation: JWT secret key is randomly generated on each restart (fine for dev, needs fixing before production)
- Next: Phase 5 - full CRUD APIs for Patient, Doctor, Appointment, DoctorAvailability, MedicalRecord
## Phase 5 in progress — Core CRUD APIs

### Patient CRUD - complete
- GET /api/patients/profile - view own profile (JWT-protected)
- PUT /api/patients/profile - update own profile (dateOfBirth, gender, address, bloodGroup)
- PUT /api/patients/deactivate - built, not yet tested (would block test account from further testing)
- JwtAuthFilter created and wired into SecurityConfig - all /api/** routes except /api/auth/** now require valid Bearer token
- Tested successfully in Postman (HealWell collection), verified in MySQL Workbench
- Known improvement needed later: API responses currently expose the full Users object including hashed
  password - should use a DTO (clean response shape) to hide sensitive fields. Not urgent, noted for later.
  ## Mistakes & fixes log

### 2026-08-16 — Guessed BCrypt hash for Admin seed (my error)
- Attempted to seed an Admin account by giving a BCrypt hash for "Admin@123" without actually
  generating/verifying it through real code - the hash was wrong, login failed with "Invalid email or password".
- Fix: deleted the broken row, signed up admin@healwell.com through the normal, tested Signup endpoint
  (guarantees a correct hash via our own passwordEncoder.encode()), then manually updated only the
  `role` column to ADMIN via SQL - never touching/guessing the password hash again.
- Lesson: never hand-write or guess cryptographic values (hashes, keys, tokens) - always generate
  them through real, tested code and verify before using.
- Minor cleanup note: this admin account has a stray blank Patient row from the brief signup step -
  harmless, can be deleted later for tidiness.

### 2026-08-16 — Duplicate SecurityConfig class
...(existing entry stays)
### Doctor CRUD - complete
- POST /api/doctors - Admin only (creates Users + Doctor together)
- GET /api/doctors, GET /api/doctors/{id} - any logged-in user
- PUT /api/doctors/{id} - Admin only
- DELETE /api/doctors/{id} - Admin only
- Role-based security (@PreAuthorize("hasRole('ADMIN')")) tested and confirmed working:
  Patient token correctly blocked (403) from create/delete; Admin token succeeds.
- Admin seeded manually: signed up via normal endpoint, then role changed to ADMIN via SQL
  (see mistakes log for why - avoids hand-crafted password hashes).
  ### DoctorAvailability CRUD - complete
- POST /api/availability/{doctorId} - Doctor can only add to own schedule (doctorId ignored, taken from token);
  Admin can add to any doctor's schedule
- GET /api/availability/{doctorId} - public, view a doctor's availability slots
- DELETE /api/availability/{id} - Doctor can delete own slots only; Admin can delete any
- Tested: Doctor successfully added own Monday 9am-5pm slot, verified via GET
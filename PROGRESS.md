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
### Appointment CRUD - complete
- POST /api/appointments/book/{doctorId} - Patient books, validated against:
  past-date block, doctor's actual availability (day+time), no double-booking same slot
- GET /api/appointments/mine - shows Patient's own bookings OR Doctor's own schedule, based on token role
- GET /api/appointments/all - Admin only, system-wide view
- PUT /api/appointments/{id}/status - owner (Patient/Doctor) or Admin can update status
- DELETE /api/appointments/{id} - cancels (sets status=CANCELLED), never hard-deletes
- Status lifecycle: PENDING -> CONFIRMED -> COMPLETED (doctor marks after visit) or CANCELLED anytime
- Design decision: "Missed" is a computed display-only label for past CONFIRMED appointments,
  not a stored status - to be implemented in frontend/response formatting later
- Tested: successful booking, rejected outside-availability booking, rejected double-booking
## Phase 5 COMPLETE — all Core CRUD APIs done

- Patient CRUD ✅
- Doctor CRUD ✅ (Admin-only create/update/delete, role security tested)
- DoctorAvailability CRUD ✅ (Doctor own + Admin any)
- Appointment CRUD ✅ (full validation: past-date block, availability match, no double-booking)
- MedicalRecord CRUD ✅ (Doctor creates only, auto-completes appointment, Patient views own, Admin excluded)

Backend is functionally complete for the core feature set. Not yet built: password reset,
DTOs to hide sensitive fields in responses, fixed JWT secret key (currently regenerates on restart).

## Session end — 2026-08-16
- 25 local commits, nothing pushed to GitHub yet.
- Next session: push this backend work to GitHub (user to confirm), then begin Phase 6 (frontend).
- Reminder for next session: re-read RULES.md and this file before starting.
### Test data seeded
- 20 doctors created across 10 specializations (2 each) via Postman Pre-request script loop
  (free alternative to paid Collection Runner CSV feature)
- Script technique: array of doctor objects in Pre-request script, pm.collectionVariables
  tracks index, increments on each Send click
  ### Test data seeded — complete
- 20 doctors across 10 specializations (2 each), created via Postman Pre-request script loop
- 11 patients (1 original Ravi Kumar + 10 new), created via signup (3 done manually after script
  index drifted past 3 failed early attempts, before app was running)
- 1 admin (unchanged)
- Technique: Pre-request script with array + pm.collectionVariables index, free alternative to
  paid Collection Runner CSV data files
- Lesson: pre-request script index still increments even if the actual request fails (e.g. server
  down) - always verify actual DB count after bulk operations, don't assume click-count = success-count
  ## Phase 6 started — Frontend

### Setup
- healwell-frontend/ created: index.html, css/style.css, js/main.js
- Home page built: navbar (Option B), hero (medium height), "Why HealWell" section,
  specialty grid (10 specialties, dynamically rendered from main.js), CTA section, footer
- pages/ folder created for sub-pages

### Signup page - complete and tested
- pages/signup.html, css/auth.css, js/api.js (reusable fetch wrapper), js/signup.js
- Field-by-field validation on blur, inline error messages, submit blocked until all valid
- Successfully calls real POST /api/auth/signup, redirects to login.html on success
- Fixed CORS: backend was blocking requests from Live Server origin (127.0.0.1:5500) -
  added CorsConfig.java to backend, allowing that origin
- Tested end-to-end: created real account (Sucheth) via the actual frontend form, verified in MySQL

### Next: login.html
### Login page - complete and tested
- pages/login.html, js/login.js
- Field validation (blur), inline errors, same pattern as signup
- Calls real POST /api/auth/login, stores JWT token in localStorage (key: healwell_token)
- Redirects to home page on success
- Tested end-to-end: signed up (Dhanu), redirected to login, logged in successfully, landed on home page
## [17-08-2026] Navbar User Menu (Avatar + Dropdown)

**Status:** Done & tested

**What was built:**
- js/navbar.js — checks localStorage for JWT on page load; if present, decodes 
  the token (email + role from claims) and replaces Login/Sign Up buttons with 
  an avatar (initials) + name + dropdown (Profile, My Appointments, Logout)
- CSS added to style.css (.user-menu, .avatar-circle, .user-dropdown, etc.)
- Script tag added to index.html after main.js

**Known limitation:** Display name is currently derived from the email prefix 
(e.g. "dhanu" from dhanu@example.com), not a real name — because the login API 
only returns { token }, and the JWT itself only contains email (sub) + role. 
A real name requires either a new /me endpoint or adding a "name" claim to 
the JWT at login time. Deferred for now — planned as a future improvement.

**Mistake & fix logged:**
- Last session we discussed navbar.js but never actually saved it to disk. 
  This session we assumed it existed and tried to "debug" a bug that was 
  actually just a missing file. 
- Caught by running `Get-ChildItem -Recurse` to verify files on disk instead 
  of trusting the VS Code Explorer view or memory of what we "should have" done.
- Lesson: always verify file existence via terminal before debugging — don't 
  trust assumption or a stale Explorer pane.

**Tested:**
- Avatar shows correct initial + email-prefix name after login ✅
- Dropdown opens/closes on click, closes on outside click ✅
- Logout clears token and reverts to Login/Sign Up buttons ✅
## [17-08-2026] Security Fix: Password Exposure in Doctor API

**Status:** Done & tested

**Problem found:** GET /api/doctors and GET /api/doctors/{id} were returning 
the full Doctor entity, including the nested Users object — which exposed 
each doctor's BCrypt password hash publicly (endpoint has no auth requirement).

**Fix:** Created a DoctorResponse DTO (new dto/ package — first DTO in the 
project) containing only safe fields: id, name, email, phoneNumber, 
specialization, qualification, experienceYears, consultationFee, bio. 
Updated DoctorController's getAllDoctors() and getDoctorById() to map 
Doctor -> DoctorResponse before returning.

**Mistake & fix logged:**
- After editing DoctorController.java, `mvn compile` said "Nothing to compile" 
  even though the file had unsaved changes shown in VS Code (M indicator).
- Lesson: always check the VS Code tab for the unsaved-changes dot/M before 
  trusting a build result. When in doubt, use `mvn clean compile` instead of 
  `mvn compile` — it forces a full rebuild and won't hide stale-cache issues.

**Tested:**
- GET /api/doctors — confirmed no password field, all 20 doctors returned 
  with correct flat structure ✅
- Backend still compiles clean, no regressions to existing Doctor CRUD ✅

**Next planned:** GET /api/doctors/{id} spot-check (single doctor), then 
build frontend Doctors browse page using this response shape.
## [18-08-2026] Doctors Browse Page + CORS/Security Config Bug Fix

**Status:** Done & tested

**What was built:**
- pages/doctors.html — doctors browse page with specialty filter dropdown
- js/doctors.js — fetches all doctors via GET /api/doctors, renders cards, 
  client-side filter by specialty, reads ?specialty=X from URL for 
  pre-filtering when navigating from home page specialty cards
- css/doctors.css — card grid styling matching site theme

**Bug found & fixed:** SecurityConfig.java had `.anyRequest().authenticated()` 
with no explicit CORS or public-GET rule for /api/doctors — even though 
CorsConfig.java existed, Spring Security blocked the request before CORS 
could apply, surfacing as a confusing CORS error in the browser console 
rather than a clear 401/403.

**Fix:** Added `.cors(cors -> {})` and an explicit permitAll rule for 
GET /api/doctors/** in SecurityConfig's filter chain, matching the 
controller's actual intent (public browsing, admin-only writes).

**Mistake & fix logged (repeat pattern):** Same "unsaved file + mvn compile 
says nothing to compile" issue happened again. Reinforces the lesson: 
always use `mvn clean compile` after editing Java files, and verify the 
VS Code tab shows no unsaved indicator before trusting a build.

**Tested:**
- Doctors page loads all 20 doctors correctly ✅
- Specialty filter dropdown works ✅
- Navigating from home page specialty card pre-filters correctly ✅
- Navbar (login state/avatar) works identically on this page ✅

## [18-08-2026] Doctor Detail Page + Availability DTO Fix + Test Data Seeding

**Status:** Done & tested

**What was built:**
- pages/doctor-detail.html + js/doctor-detail.js + css/doctor-detail.css
  Shows a single doctor's full profile (bio, qualification, fee) and 
  weekly availability, fetched via GET /api/doctors/{id} and 
  GET /api/availability/{id} in parallel.

**Backend fixes (same pattern as DoctorResponse):**
- Created AvailabilityResponse DTO — GET /api/availability/{doctorId} was 
  leaking full nested Doctor -> Users object (including password hash), 
  same issue as the doctors list bug fixed earlier today.
- Updated DoctorAvailabilityController to return the DTO.
- Added GET /api/availability/** to SecurityConfig's permitAll list — 
  was blocked by the same CORS+Security gotcha as before.

**Test data seeded:** Added Mon-Fri, 9-5, 30-min availability slots for 
doctors 3-21 via direct SQL (doctor 2 already had Monday-only data from 
original seed). 95 new rows inserted, verified via GROUP BY count.

**Tested:**
- Doctor detail page renders profile + availability correctly ✅
- Confirmed empty-state message before seeding, populated state after ✅
- Verified in Postman: no password leak in availability response ✅

**Known gap for next session:** Booking endpoint (POST /api/appointments/
book/{doctorId}) takes a raw dateTime, not a slot ID — booking form will 
need to combine weekly availability pattern + a real calendar date. 
Not yet built. Plan to design this carefully next session, after a break.
## [18-08-2026] Booking Flow — Started (In Progress)

**Status:** Partial, not complete — continuing next session

**What was built:**
- js/doctor-detail.js — added renderBookingSection(): shows login prompt if 
  not authenticated, otherwise a date picker (min=today) if logged in
- Added formatTime12hr() helper — converts 24hr time strings to 12hr AM/PM 
  format, applied to the Weekly Availability list display

**Data change:** Replaced all doctor availability with a realistic schedule — 
morning 10:00 AM-2:00 PM + evening 4:00 PM-7:00 PM (lunch break 2-4 PM 
excluded), Mon-Fri, 30-min slots, for all 20 doctors. 200 rows total 
(replaced the old 96-row 9-5 dataset via DELETE + re-INSERT in MySQL).

**Mistake & fix logged:** Pasted new code into doctor-detail.js without 
replacing old content first, causing duplicated function definitions 
(same DOMContentLoaded listener and renderDoctor appearing twice). Caught 
by checking VS Code's Outline panel, which clearly showed the duplicates. 
Fixed by fully clearing the file and pasting one clean version.
Lesson: when replacing significant chunks of a JS file,
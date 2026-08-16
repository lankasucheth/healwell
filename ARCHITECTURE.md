# HealWell — Architecture & Roadmap

## Part 1 — What we planned vs what we achieved

| Phase | Planned | Status |
|---|---|---|
| 1. Project setup | Git, GitHub, RULES.md, PROGRESS.md | ✅ Done |
| 2. Database design | 6 entities, relationships, business rules | ✅ Done |
| 3. Backend skeleton | Spring Boot, Maven, entities, repos, MySQL connected | ✅ Done |
| 4. Authentication | Signup, login, JWT, password hashing | ✅ Done |
| 5. Core CRUD APIs | Patient, Doctor, DoctorAvailability, Appointment, MedicalRecord | ✅ Done |
| 6. Frontend | Plain HTML/CSS/JS - login, signup, navbar, dashboards | ⏳ Next |
| 7. React | Added on top of plain frontend | Not started |
| 8. Docker | Containerize backend + MySQL (already installed on PC) | Not started |
| 9. Microservices | Split monolith into services | Not started |

## The layer flow (every feature follows this same chain) 

Config → Model/Entity → Repository → Service → Controller → API request → Database

- **Config** — `SecurityConfig` (password hashing, security rules), `JwtUtil` (create/read tokens), `JwtAuthFilter` (checks every request's token)
- **Model/Entity** — one Java class per database table (`@Entity`)
- **Repository** — interface that talks to MySQL, no SQL written by us (Spring Data JPA generates it)
- **Service** — the actual business logic and rules (validation, who's allowed to do what)
- **Controller** — defines the URL endpoints (`@RestController`, e.g. `/api/patients`)
- **API request** — what Postman actually calls
- **Database** — `healwell_db`, MySQL, where it all lands

## Database schema — 6 tables, relationships

Users (PK: id, unique: email)
├── Patient (PK: id, FK: user_id → Users)
│ └── Appointment (FK: patient_id → Patient)
│ └── MedicalRecord (FK: patient_id → Patient)
└── Doctor (PK: id, FK: user_id → Users)
└── DoctorAvailability (FK: doctor_id → Doctor)
└── Appointment (FK: doctor_id → Doctor)
└── MedicalRecord (FK: doctor_id → Doctor)

MedicalRecord also has FK: appointment_id → Appointment (unique — one record per visit)

| Table | Primary Key | Foreign Keys | Key Fields |
|---|---|---|---|
| `users` | id | — | email (unique), role (PATIENT/DOCTOR/ADMIN), is_active |
| `patient` | id | user_id → users | dateOfBirth, gender, address, bloodGroup (all optional) |
| `doctor` | id | user_id → users | specialization, qualification, experienceYears, consultationFee, bio |
| `doctor_availability` | id | doctor_id → doctor | dayOfWeek, startTime, endTime, isAvailable |
| `appointment` | id | patient_id → patient, doctor_id → doctor | dateTime, status (PENDING/CONFIRMED/CANCELLED/COMPLETED) |
| `medical_record` | id | patient_id, doctor_id, appointment_id (unique) | diagnosis, prescription, recordDate |

## API endpoints built so far

| Entity | Endpoints | Access rule |
|---|---|---|
| Auth | POST /api/auth/signup, POST /api/auth/login | Public |
| Patient | GET/PUT /api/patients/profile, PUT /api/patients/deactivate | Own data only (via JWT) |
| Doctor | POST/PUT/DELETE /api/doctors | Admin only |
| Doctor | GET /api/doctors, GET /api/doctors/{id} | Any logged-in user |
| DoctorAvailability | POST/DELETE /api/availability/{id} | Doctor (own) or Admin |
| DoctorAvailability | GET /api/availability/{doctorId} | Public |
| Appointment | POST /api/appointments/book/{doctorId} | Patient, with full validation |
| Appointment | GET /api/appointments/mine | Own bookings (Patient or Doctor) |
| Appointment | GET /api/appointments/all | Admin only |
| Appointment | PUT /api/appointments/{id}/status, DELETE (cancel) | Owner or Admin |
| MedicalRecord | POST /api/medical-records/{appointmentId} | Doctor only, own appointments |
| MedicalRecord | GET /api/medical-records/mine | Patient, own records |
| MedicalRecord | GET /api/medical-records/patient/{id} | Doctor only |

## Part 2 — What's next

1. **Frontend (Phase 6)** — plain HTML/CSS/JS: login, signup, navbar, role-based dashboards, appointment booking UI with the "available slot checkmark" behavior
2. **React (later)** — rebuild frontend in React once comfortable with fundamentals
3. **Docker (later)** — containerize the backend + MySQL using Docker (already installed on your PC), so the whole app runs with one command
4. **Microservices (later)** — split this monolith into separate services, as a deliberate learning phase
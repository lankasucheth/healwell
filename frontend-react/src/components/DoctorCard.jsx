function DoctorCard({ name, specialization, qualification, experienceYears, consultationFee }) {
    return (
        <div>
            <h3>{name}</h3>
            <p>{specialization}</p>
            <p>{qualification}</p>
            <p>{experienceYears} years experience</p>
            <p>Consultation fee: ₹{consultationFee}</p>
        </div>
    )
}

export default DoctorCard
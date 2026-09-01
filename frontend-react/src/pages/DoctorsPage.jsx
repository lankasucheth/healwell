import { useState, useEffect } from 'react'
import DoctorCard from '../components/DoctorCard'

function DoctorsPage() {
    const [doctors, setDoctors] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        async function loadDoctors() {
            try {
                const response = await fetch('http://localhost:8080/api/doctors')
                const data = await response.json()
                setDoctors(data)
            } catch (error) {
                console.error(error)
                setError('Could not load doctors. Please try again.')

            } finally {
                setLoading(false)
            }
        }
        loadDoctors()
    }, [])

    if (loading) {
        return <p>Loading doctors...</p>
    }

    if (error) {
        return <p>{error}</p>
    }

    return (
        <div>
            <h1>Our Doctors</h1>
            {doctors.length === 0 ? (
                <p>No doctors available.</p>
            ) : (
                doctors.map((doctor) => (
                    <DoctorCard
                        key={doctor.id}
                        name={doctor.name}
                        specialization={doctor.specialization}
                        qualification={doctor.qualification}
                        experienceYears={doctor.experienceYears}
                        consultationFee={doctor.consultationFee}
                    />
                ))
            )}
        </div>
    )
}

export default DoctorsPage
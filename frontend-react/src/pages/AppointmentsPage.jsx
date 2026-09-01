import { useState, useEffect } from 'react'
import AppointmentCard from '../components/AppointmentCard'

function AppointmentsPage({ token }) {
    const [appointments, setAppointments] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)

    useEffect(() => {
        async function loadAppointments() {
            try {
                const response = await fetch('http://localhost:8080/api/appointments/mine', {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                })
                const data = await response.json()
                setAppointments(data)
            } catch (error) {
                setError('Could not load appointments. Please try again.')
            } finally {
                setLoading(false)
            }
        }
        loadAppointments()
    }, [token])

    async function handleCancel(appointmentId) {
        try {
            await fetch(`http://localhost:8080/api/appointments/${appointmentId}`, {
                method: 'DELETE',
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
            setAppointments((prevAppointments) =>
                prevAppointments.map((appointment) =>
                    appointment.id === appointmentId
                        ? { ...appointment, status: 'CANCELLED' }
                        : appointment
                )
            )
        } catch (error) {
            console.log(error)
        }
    }

    if (loading) {
        return <p>Loading your appointments...</p>
    }

    if (error) {
        return <p>{error}</p>
    }

    return (
        <div>
            <h1>HealWell</h1>
            {appointments.length === 0 ? (
                <p>No appointments yet.</p>
            ) : (
                appointments.map((appointment) => (
                    <AppointmentCard
                        key={appointment.id}
                        doctorName={appointment.doctor.name}
                        dateTime={appointment.dateTime}
                        status={appointment.status}
                        onCancel={() => handleCancel(appointment.id)}
                    />
                ))
            )}
        </div>
    )
}

export default AppointmentsPage
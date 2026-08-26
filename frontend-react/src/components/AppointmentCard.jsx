function AppointmentCard({ doctorName, dateTime, status }) {
  const formattedDate = new Date(dateTime).toLocaleString('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
  })

  return (
    <div className="appointment-card">
      <h3>{doctorName}</h3>
      <p>{formattedDate}</p>
      <p>{status}</p>
    </div>
  )
}

export default AppointmentCard
import { useState } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import './App.css'
import Login from './components/Login'
import AppointmentsPage from './pages/AppointmentsPage'
import DoctorsPage from './pages/DoctorsPage'

function App() {
    const [token, setToken] = useState(() => localStorage.getItem('token'))

    function handleLoginSuccess(newToken) {
        localStorage.setItem('token', newToken)
        setToken(newToken)
    }

    return (
        <Routes>
            <Route
                path="/login"
                element={
                    token ? (
                        <Navigate to="/appointments" />
                    ) : (
                        <Login onLoginSuccess={handleLoginSuccess} />
                    )
                }
            />
            <Route
                path="/appointments"
                element={
                    token ? (
                        <AppointmentsPage token={token} />
                    ) : (
                        <Navigate to="/login" />
                    )
                }
            />
            <Route path="/doctors" element={<DoctorsPage />} />
            <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
    )
}

export default App
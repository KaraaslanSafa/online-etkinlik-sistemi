import React, { useState, useEffect } from 'react';

const AdminApprovalPanel = () => {
    const [pendingEvents, setPendingEvents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    useEffect(() => {
        fetchPendingEvents();
    }, []);

    const fetchPendingEvents = async () => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/events/approval/pending');
            const data = await response.json();
            setPendingEvents(data);
        } catch (error) {
            console.error('Hata:', error);
            setMessage('Etkinlikleri yüklemede hata oluştu');
        } finally {
            setLoading(false);
        }
    };

    const handleApprove = async (eventId) => {
        try {
            const response = await fetch(
                `http://localhost:8080/api/events/${eventId}/approve?adminId=1`,
                { method: 'POST' }
            );
            if (response.ok) {
                setMessage('Etkinlik onaylandı!');
                setPendingEvents(pendingEvents.filter(e => e.id !== eventId));
                setTimeout(() => setMessage(''), 3000);
            }
        } catch (error) {
            console.error('Hata:', error);
            setMessage('Onaylama işleminde hata oluştu');
        }
    };

    const handleReject = async (eventId) => {
        const reason = prompt('Reddetme nedenini girin:');
        if (!reason) return;

        try {
            const response = await fetch(
                `http://localhost:8080/api/events/${eventId}/reject?rejectionReason=${encodeURIComponent(reason)}`,
                { method: 'POST' }
            );
            if (response.ok) {
                setMessage('Etkinlik reddedildi!');
                setPendingEvents(pendingEvents.filter(e => e.id !== eventId));
                setTimeout(() => setMessage(''), 3000);
            }
        } catch (error) {
            console.error('Hata:', error);
            setMessage('Reddetme işleminde hata oluştu');
        }
    };

    return (
        <div style={{ padding: '20px', border: '1px solid #ccc', borderRadius: '5px', marginBottom: '20px' }}>
            <h2>📋 Admin - Onay Bekleyen Etkinlikler</h2>

            {message && <div style={{
                padding: '10px',
                marginBottom: '10px',
                backgroundColor: '#d4edda',
                color: '#155724',
                borderRadius: '5px'
            }}>{message}</div>}

            {loading && <p>Yükleniyor...</p>}

            {pendingEvents.length === 0 ? (
                <p>Onay bekleyen etkinlik yok.</p>
            ) : (
                <div>
                    {pendingEvents.map(event => (
                        <div key={event.id} style={{
                            padding: '15px',
                            border: '1px solid #ffc107',
                            borderRadius: '5px',
                            marginBottom: '10px',
                            backgroundColor: '#fffbea'
                        }}>
                            <h4>{event.title}</h4>
                            <p><strong>Açıklama:</strong> {event.description}</p>
                            <p><strong>Tarih:</strong> {new Date(event.startDate).toLocaleString('tr-TR')}</p>
                            <p><strong>Konum:</strong> {event.location}</p>
                            <p><strong>Kategori:</strong> {event.categoryId}</p>
                            <p><strong>Kapasite:</strong> {event.capacity}</p>

                            <div style={{ marginTop: '10px' }}>
                                <button
                                    onClick={() => handleApprove(event.id)}
                                    style={{
                                        padding: '8px 15px',
                                        marginRight: '10px',
                                        backgroundColor: '#28a745',
                                        color: 'white',
                                        border: 'none',
                                        borderRadius: '5px',
                                        cursor: 'pointer'
                                    }}
                                >
                                    ✓ Onayla
                                </button>
                                <button
                                    onClick={() => handleReject(event.id)}
                                    style={{
                                        padding: '8px 15px',
                                        backgroundColor: '#dc3545',
                                        color: 'white',
                                        border: 'none',
                                        borderRadius: '5px',
                                        cursor: 'pointer'
                                    }}
                                >
                                    ✗ Reddet
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default AdminApprovalPanel;

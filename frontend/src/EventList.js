
import React, { useState, useEffect } from "react";

function EventList({ onSelect, onDelete }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    fetchApprovedEvents();
  }, []);

  const fetchApprovedEvents = async () => {
    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/events');
      const data = await response.json();
      // Sadece onaylı etkinlikleri göster
      const approvedEvents = data.filter(e => e.approvalStatus === 'APPROVED');
      setEvents(approvedEvents);
    } catch (error) {
      console.error('Hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (eventId) => {
    if (window.confirm('Etkinliği silmek istediğinize emin misiniz?')) {
      try {
        const response = await fetch(`http://localhost:8080/api/events/${eventId}`, {
          method: 'DELETE'
        });
        if (response.ok) {
          setEvents(events.filter(e => e.id !== eventId));
          alert('Etkinlik başarıyla silindi!');
        }
      } catch (error) {
        console.error('Hata:', error);
        alert('Etkinlik silinirken hata oluştu!');
      }
    }
  };

  if (loading) {
    return <div><h2>Etkinlikler</h2><p>Yükleniyor...</p></div>;
  }

  return (
    <div style={{ marginTop: '20px' }}>
      <h2>📅 Onaylanan Etkinlikler ({events.length})</h2>

      {events.length === 0 ? (
        <p style={{ color: '#666' }}>Henüz onaylı etkinlik yok.</p>
      ) : (
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
          gap: '15px'
        }}>
          {events.map(event => (
            <div key={event.id} style={{
              padding: '15px',
              border: '1px solid #ddd',
              borderRadius: '5px',
              backgroundColor: '#f8f9fa',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}>
              <h4 style={{ marginTop: 0 }}>{event.title}</h4>
              <p><strong>📍 Konum:</strong> {event.location}</p>
              <p><strong>📅 Tarih:</strong> {new Date(event.startDate).toLocaleString('tr-TR')}</p>
              <p><strong>👥 Kapasite:</strong> {event.capacity} kişi</p>
              {event.city && <p><strong>🏙️ Şehir:</strong> {event.city}</p>}
              {event.isFree ? (
                <p style={{ color: 'green' }}><strong>💰 Ücretsiz</strong></p>
              ) : (
                <p><strong>💰 Fiyat:</strong> {event.price} TL</p>
              )}

              <div style={{ marginTop: '10px', display: 'flex', gap: '10px' }}>
                <button
                  onClick={() => onSelect(event)}
                  style={{
                    flex: 1,
                    padding: '8px',
                    backgroundColor: '#007bff',
                    color: 'white',
                    border: 'none',
                    borderRadius: '5px',
                    cursor: 'pointer'
                  }}
                >
                  Detay
                </button>
                <button
                  onClick={() => handleDelete(event.id)}
                  style={{
                    padding: '8px 15px',
                    backgroundColor: '#dc3545',
                    color: 'white',
                    border: 'none',
                    borderRadius: '5px',
                    cursor: 'pointer'
                  }}
                >
                  Sil
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default EventList;

import React, { useState, useEffect, useCallback } from 'react';

function ParticipantList({ eventId, onUpdate }) {
  const [participants, setParticipants] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchEventParticipants = useCallback(async () => {
    if (!eventId) return;

    setLoading(true);
    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/event-participants/event/${eventId}`);
      const data = await response.json();
      setParticipants(data);
    } catch (error) {
      console.error('Hata:', error);
    } finally {
      setLoading(false);
    }
  }, [eventId]);

  useEffect(() => {
    if (eventId) {
      fetchEventParticipants();
    }
  }, [eventId, fetchEventParticipants]);

  const handleRemoveParticipant = async (participantId) => {
    if (!window.confirm('Katılımcıyı çıkarmak istediğinize emin misiniz?')) return;

    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/api/event-participants/unregister?eventId=${eventId}&participantId=${participantId}`,
        { method: 'DELETE' }
      );

      if (response.ok) {
        setParticipants(participants.filter((p) => p.participantId !== participantId));
        alert('Katılımcı çıkarıldı!');
        if (onUpdate) onUpdate();
      } else {
        alert('Katılımcı çıkarılırken hata oluştu!');
      }
    } catch (error) {
      console.error('Hata:', error);
      alert('Katılımcı çıkarılırken hata oluştu!');
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '40px' }}>
        <div className="spinner" style={{ margin: '0 auto 15px auto' }}></div>
        <p style={{ color: '#666', fontWeight: 'bold' }}>Katılımcılar Yükleniyor...</p>
      </div>
    );
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', paddingBottom: '15px', borderBottom: '1px solid #eee' }}>
        <h2 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span>👥</span> Katılımcı Listesi
        </h2>
        <span style={{ backgroundColor: '#eefcf2', color: '#137333', padding: '6px 12px', borderRadius: '20px', fontWeight: 'bold', fontSize: '14px' }}>
          Toplam: {participants.length} Kişi
        </span>
      </div>

      {participants.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px 20px', backgroundColor: '#f8f9fa', borderRadius: '12px' }}>
          <span style={{ fontSize: '40px' }}>🎫</span>
          <h4 style={{ margin: '15px 0 5px 0', color: '#333' }}>Henüz bilet alan yok</h4>
          <p style={{ color: '#888', margin: 0 }}>Bu etkinlik için henüz kimse kayıt oluşturmamış.</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', maxHeight: '60vh', overflowY: 'auto', paddingRight: '5px' }}>
          {participants.map((p) => (
            <div
              key={p.id}
              style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '16px',
                backgroundColor: '#f8f9fa',
                border: '1px solid #e9ecef',
                borderRadius: '10px',
                transition: 'all 0.2s ease',
              }}
              onMouseEnter={(e) => { e.currentTarget.style.backgroundColor = '#fff'; e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.05)'; e.currentTarget.style.borderColor = '#ddd'; }}
              onMouseLeave={(e) => { e.currentTarget.style.backgroundColor = '#f8f9fa'; e.currentTarget.style.boxShadow = 'none'; e.currentTarget.style.borderColor = '#e9ecef'; }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                <div style={{ width: '45px', height: '45px', borderRadius: '50%', backgroundColor: '#007aff', color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '18px', fontWeight: 'bold' }}>
                  {p.participantName ? p.participantName.charAt(0).toUpperCase() : '?'}
                </div>
                <div>
                  <h4 style={{ margin: '0 0 4px 0', fontSize: '16px', color: '#333' }}>{p.participantName}</h4>
                  <p style={{ margin: 0, fontSize: '13px', color: '#666', display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <span>📧</span> {p.participantEmail}
                  </p>
                  {p.status && (
                    <span style={{ 
                      display: 'inline-block', 
                      marginTop: '6px', 
                      fontSize: '11px', 
                      fontWeight: 'bold', 
                      padding: '3px 8px', 
                      borderRadius: '4px',
                      backgroundColor: p.status === 'CONFIRMED' ? '#e6f4ea' : p.status === 'CANCELLED' ? '#fce8e6' : '#f0f0f0',
                      color: p.status === 'CONFIRMED' ? '#137333' : p.status === 'CANCELLED' ? '#c5221f' : '#666'
                    }}>
                      {p.status === 'CONFIRMED' ? 'ONAYLI' : p.status === 'CANCELLED' ? 'İPTAL EDİLDİ' : p.status}
                    </span>
                  )}
                </div>
              </div>
              <button
                onClick={() => handleRemoveParticipant(p.participantId)}
                style={{
                  padding: '8px 16px',
                  backgroundColor: '#fff',
                  color: '#ff3b30',
                  border: '1px solid #ff3b30',
                  borderRadius: '6px',
                  cursor: 'pointer',
                  fontSize: '13px',
                  fontWeight: 'bold',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                  transition: 'all 0.2s ease'
                }}
                onMouseEnter={(e) => { e.currentTarget.style.backgroundColor = '#ff3b30'; e.currentTarget.style.color = '#fff'; }}
                onMouseLeave={(e) => { e.currentTarget.style.backgroundColor = '#fff'; e.currentTarget.style.color = '#ff3b30'; }}
              >
                <span>✕</span> Çıkar
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ParticipantList;

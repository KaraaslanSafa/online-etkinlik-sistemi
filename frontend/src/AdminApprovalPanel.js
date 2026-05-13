import React, { useState, useEffect } from 'react';

const AdminApprovalPanel = ({ onApprovalChanged }) => {
  const [pendingEvents, setPendingEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState({ show: false, message: '', type: 'success' });

  useEffect(() => {
    fetchPendingEvents();
  }, []);

  const showToast = (message, type = 'success') => {
    setNotification({ show: true, message, type });
    setTimeout(() => {
      setNotification({ show: false, message: '', type: 'success' });
    }, 4000);
  };

  const fetchPendingEvents = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/events/approval/pending`);
      if (!response.ok) {
        throw new Error('Bekleyen etkinlikler yüklenemedi');
      }
      const data = await response.json();
      setPendingEvents(data);
    } catch (error) {
      console.error('Hata:', error);
      showToast('Etkinlikleri yüklemede hata oluştu', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (eventId) => {
    try {
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/events/${eventId}/approve?adminId=1`, {
        method: 'POST',
      });

      if (response.ok) {
        showToast('✓ Etkinlik başarıyla onaylandı ve yayına alındı!', 'success');
        // Listeden çıkar
        setPendingEvents(prev => prev.filter((e) => e.id !== eventId));
        if (onApprovalChanged) onApprovalChanged();
      } else {
        const errText = await response.text();
        showToast(`Onaylanamadı: ${errText || 'Sunucu hatası (' + response.status + ')'}`, 'error');
      }
    } catch (error) {
      console.error('Hata:', error);
      showToast('Bağlantı hatası: Onaylama işlemi başarısız oldu', 'error');
    }
  };

  const handleReject = async (eventId) => {
    const reason = prompt('Lütfen reddetme nedenini giriniz (Organizatöre e-posta olarak gönderilecektir):');
    if (reason === null) return; // Cancelled prompt
    if (!reason.trim()) {
      showToast('Reddetme nedeni boş bırakılamaz!', 'error');
      return;
    }

    try {
      const response = await fetch(
        `${process.env.REACT_APP_API_BASE_URL}/api/events/${eventId}/reject?rejectionReason=${encodeURIComponent(reason.trim())}`,
        { method: 'POST' }
      );

      if (response.ok) {
        showToast('✗ Etkinlik reddedildi ve organizatöre bilgi verildi.', 'info');
        setPendingEvents(prev => prev.filter((e) => e.id !== eventId));
        if (onApprovalChanged) onApprovalChanged();
      } else {
        const errText = await response.text();
        showToast(`Reddedilemedi: ${errText || 'Sunucu hatası (' + response.status + ')'}`, 'error');
      }
    } catch (error) {
      console.error('Hata:', error);
      showToast('Bağlantı hatası: Reddetme işlemi başarısız oldu', 'error');
    }
  };

  return (
    <div style={{
      padding: '24px',
      backgroundColor: '#ffffff',
      borderRadius: '16px',
      boxShadow: '0 8px 30px rgba(0,0,0,0.08)',
      marginBottom: '24px',
      position: 'relative',
      border: '1px solid #f2f2f7'
    }}>
      {/* Premium Toast Notification */}
      {notification.show && (
        <div style={{
          position: 'fixed',
          top: '20px',
          right: '20px',
          padding: '16px 24px',
          borderRadius: '12px',
          backgroundColor: notification.type === 'success' ? '#34c759' : notification.type === 'error' ? '#ff3b30' : '#007aff',
          color: '#ffffff',
          fontWeight: 'bold',
          boxShadow: '0 10px 25px rgba(0,0,0,0.15)',
          zIndex: 9999,
          display: 'flex',
          alignItems: 'center',
          gap: '10px',
          animation: 'slideIn 0.3s ease-out',
          fontSize: '15px'
        }}>
          {notification.type === 'success' && '🌟'}
          {notification.type === 'error' && '⚠️'}
          {notification.type === 'info' && 'ℹ️'}
          {notification.message}
        </div>
      )}

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2 style={{ margin: 0, fontSize: '20px', color: '#1c1c1e', fontWeight: '800', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span>📋</span> Onay Bekleyen Etkinlik Talepleri
        </h2>
        <span style={{
          fontSize: '12px',
          backgroundColor: '#f2f2f7',
          padding: '4px 10px',
          borderRadius: '20px',
          color: '#8e8e93',
          fontWeight: 'bold'
        }}>
          {pendingEvents.length} Bekleyen Talep
        </span>
      </div>

      {loading && (
        <div style={{ textAlign: 'center', padding: '30px', color: '#007aff', fontWeight: 'bold' }}>
          🔄 Talepler yükleniyor...
        </div>
      )}

      {!loading && pendingEvents.length === 0 ? (
        <div style={{
          textAlign: 'center',
          padding: '40px 20px',
          backgroundColor: '#f8f9fa',
          borderRadius: '12px',
          color: '#8e8e93',
          border: '1px dashed #e5e5ea'
        }}>
          <span style={{ fontSize: '32px', display: 'block', marginBottom: '10px' }}>🎉</span>
          <p style={{ margin: 0, fontWeight: 'bold', fontSize: '15px', color: '#1c1c1e' }}>Harika! Onay bekleyen etkinlik bulunmuyor.</p>
          <p style={{ margin: '5px 0 0 0', fontSize: '13px' }}>Tüm organizatör talepleri değerlendirilmiş.</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {pendingEvents.map((event) => (
            <div
              key={event.id}
              className="pending-event-card"
              style={{
                padding: '20px',
                border: '1px solid #e5e5ea',
                borderRadius: '12px',
                backgroundColor: '#fafafc',
                transition: 'all 0.3s ease',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'flex-start',
                gap: '20px'
              }}
            >
              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                  <span style={{
                    fontSize: '11px',
                    backgroundColor: '#ff9500',
                    color: '#ffffff',
                    padding: '2px 8px',
                    borderRadius: '10px',
                    fontWeight: 'bold'
                  }}>
                    PENDING
                  </span>
                  <span style={{ fontSize: '12px', color: '#8e8e93' }}>ID: #{event.id}</span>
                </div>
                <h4 style={{ margin: '0 0 10px 0', fontSize: '17px', color: '#1c1c1e', fontWeight: '700' }}>{event.title}</h4>
                <p style={{ margin: '0 0 12px 0', color: '#3a3a3c', fontSize: '14px', lineHeight: '1.5' }}>
                  <strong>Açıklama:</strong> {event.description}
                </p>

                <div style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))',
                  gap: '10px',
                  fontSize: '13px',
                  color: '#48484a',
                  backgroundColor: '#ffffff',
                  padding: '12px',
                  borderRadius: '8px',
                  border: '1px solid #f2f2f7'
                }}>
                  <div>📅 <strong>Tarih:</strong> {new Date(event.startDate).toLocaleString('tr-TR')}</div>
                  <div>📍 <strong>Konum:</strong> {event.location}</div>
                  <div>🏷️ <strong>Kategori ID:</strong> {event.categoryId}</div>
                  <div>👥 <strong>Kapasite:</strong> {event.capacity} Kişi</div>
                  <div>💰 <strong>Fiyat:</strong> {event.price === 0 ? 'Ücretsiz' : `${event.price} TL`}</div>
                </div>
              </div>

              <div style={{
                display: 'flex',
                flexDirection: 'column',
                gap: '10px',
                minWidth: '120px'
              }}>
                <button
                  onClick={() => handleApprove(event.id)}
                  style={{
                    padding: '10px 16px',
                    backgroundColor: '#34c759',
                    color: 'white',
                    border: 'none',
                    borderRadius: '8px',
                    cursor: 'pointer',
                    fontWeight: 'bold',
                    fontSize: '13px',
                    transition: 'background-color 0.2s',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '6px',
                    boxShadow: '0 4px 10px rgba(52, 199, 89, 0.2)'
                  }}
                  onMouseOver={(e) => e.target.style.backgroundColor = '#28a745'}
                  onMouseOut={(e) => e.target.style.backgroundColor = '#34c759'}
                >
                  ✓ Onayla
                </button>
                <button
                  onClick={() => handleReject(event.id)}
                  style={{
                    padding: '10px 16px',
                    backgroundColor: '#ff3b30',
                    color: 'white',
                    border: 'none',
                    borderRadius: '8px',
                    cursor: 'pointer',
                    fontWeight: 'bold',
                    fontSize: '13px',
                    transition: 'background-color 0.2s',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '6px',
                    boxShadow: '0 4px 10px rgba(255, 59, 48, 0.2)'
                  }}
                  onMouseOver={(e) => e.target.style.backgroundColor = '#d32f2f'}
                  onMouseOut={(e) => e.target.style.backgroundColor = '#ff3b30'}
                >
                  ✗ Reddet
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Dynamic Keyframes injected for Toast slideIn animation */}
      <style>{`
        @keyframes slideIn {
          from { transform: translateY(-20px); opacity: 0; }
          to { transform: translateY(0); opacity: 1; }
        }
      `}</style>
    </div>
  );
};

export default AdminApprovalPanel;

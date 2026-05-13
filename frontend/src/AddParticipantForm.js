import API_BASE_URL from './config';
import React, { useState } from 'react';

function AddParticipantForm({ eventId, onRegistered }) {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!eventId) {
      setMessage('Önce bir etkinlik seçin.');
      return;
    }

    if (!formData.firstName.trim() || !formData.lastName.trim() || !formData.email.trim()) {
      setMessage('Ad, soyad ve email zorunludur.');
      return;
    }

    setLoading(true);
    setMessage('');

    try {
      let participant = null;
      const email = encodeURIComponent(formData.email.trim());
      const searchResponse = await fetch(`${API_BASE_URL}/api/participants/email/${email}`);

      if (searchResponse.ok) {
        participant = await searchResponse.json();
      } else if (searchResponse.status === 404) {
        const createResponse = await fetch(`${API_BASE_URL}/api/participants`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            firstName: formData.firstName.trim(),
            lastName: formData.lastName.trim(),
            email: formData.email.trim(),
            phoneNumber: formData.phoneNumber.trim(),
          }),
        });

        if (!createResponse.ok) {
          throw new Error('Katılımcı oluşturulamadı.');
        }
        participant = await createResponse.json();
      } else {
        throw new Error('Katılımcı sorgulanırken hata oluştu.');
      }

      const registerResponse = await fetch(
        `${API_BASE_URL}/api/event-participants/register?eventId=${eventId}&participantId=${participant.id}`,
        {
          method: 'POST',
        }
      );

      if (registerResponse.ok) {
        setMessage('Katılımcı başarıyla etkinliğe kaydedildi.');
        setFormData({ firstName: '', lastName: '', email: '', phoneNumber: '' });
        if (onRegistered) onRegistered();
      } else {
        const errorText = await registerResponse.text();
        setMessage(errorText || 'Katılımcı kaydı başarısız oldu.');
      }
    } catch (error) {
      console.error('Hata:', error);
      setMessage('Katılımcı eklenirken hata oluştu.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginTop: '20px' }}>
      <h3>➕ Katılımcı Kaydet</h3>
      {message && (
        <div
          style={{
            backgroundColor: message.includes('başarıyla') ? '#d4edda' : '#f8d7da',
            color: message.includes('başarıyla') ? '#155724' : '#721c24',
            borderRadius: '5px',
            padding: '10px',
            marginBottom: '12px',
          }}
        >
          {message}
        </div>
      )}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
        <input
          type="text"
          name="firstName"
          placeholder="Ad"
          value={formData.firstName}
          onChange={handleChange}
          required
          style={{ padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
        />
        <input
          type="text"
          name="lastName"
          placeholder="Soyad"
          value={formData.lastName}
          onChange={handleChange}
          required
          style={{ padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
        />
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginTop: '10px' }}>
        <input
          type="email"
          name="email"
          placeholder="Email"
          value={formData.email}
          onChange={handleChange}
          required
          style={{ padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
        />
        <input
          type="text"
          name="phoneNumber"
          placeholder="Telefon (opsiyonel)"
          value={formData.phoneNumber}
          onChange={handleChange}
          style={{ padding: '10px', borderRadius: '5px', border: '1px solid #ccc' }}
        />
      </div>
      <button
        type="submit"
        disabled={loading}
        style={{
          marginTop: '12px',
          padding: '10px 18px',
          backgroundColor: loading ? '#bbb' : '#007bff',
          color: 'white',
          border: 'none',
          borderRadius: '5px',
          cursor: loading ? 'not-allowed' : 'pointer',
        }}
      >
        {loading ? 'Kaydediliyor...' : 'Katılımcı Kaydet'}
      </button>
    </form>
  );
}

export default AddParticipantForm;

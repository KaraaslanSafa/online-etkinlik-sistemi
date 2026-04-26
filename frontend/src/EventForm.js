import React, { useState } from "react";

function EventForm({ onAdd }) {
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    startDate: "",
    endDate: "",
    location: "",
    city: "",
    price: 0,
    isFree: true,
    capacity: 100,
    categoryId: 1
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({
      ...formData,
      [name]: type === 'checkbox' ? checked : (type === 'number' ? Number(value) : value)
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.title || !formData.startDate || !formData.location) {
      setMessage('Lütfen zorunlu alanları doldurun!');
      return;
    }

    setLoading(true);
    try {
      const response = await fetch('http://localhost:8080/api/events', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        const newEvent = await response.json();
        setMessage('✓ Etkinlik başarıyla oluşturuldu! Admin onayını beklemektedir.');

        if (onAdd) {
          onAdd(newEvent);
        }

        // Formu sıfırla
        setFormData({
          title: "",
          description: "",
          startDate: "",
          endDate: "",
          location: "",
          city: "",
          price: 0,
          isFree: true,
          capacity: 100,
          categoryId: 1
        });

        setTimeout(() => setMessage(''), 3000);
      } else {
        setMessage('Etkinlik oluştururken hata oluştu!');
      }
    } catch (error) {
      console.error('Hata:', error);
      setMessage('Bağlantı hatası!');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{
      marginBottom: 24,
      padding: '20px',
      border: '1px solid #ddd',
      borderRadius: '5px',
      backgroundColor: '#f9f9f9'
    }}>
      <h2>📝 Yeni Etkinlik Oluştur (Organizatör)</h2>

      {message && <div style={{
        padding: '10px',
        marginBottom: '10px',
        backgroundColor: message.includes('✓') ? '#d4edda' : '#f8d7da',
        color: message.includes('✓') ? '#155724' : '#721c24',
        borderRadius: '5px'
      }}>{message}</div>}

      <div style={{ marginBottom: '15px' }}>
        <label>Etkinlik Adı *</label>
        <input
          type="text"
          name="title"
          placeholder="Etkinlik Adı"
          value={formData.title}
          onChange={handleChange}
          required
          style={{ width: '100%', padding: '8px', marginTop: '5px' }}
        />
      </div>

      <div style={{ marginBottom: '15px' }}>
        <label>Açıklama</label>
        <textarea
          name="description"
          placeholder="Etkinlik Açıklaması"
          value={formData.description}
          onChange={handleChange}
          style={{ width: '100%', padding: '8px', marginTop: '5px', minHeight: '80px' }}
        />
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '15px' }}>
        <div>
          <label>Başlangıç Tarihi *</label>
          <input
            type="datetime-local"
            name="startDate"
            value={formData.startDate}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
        <div>
          <label>Bitiş Tarihi *</label>
          <input
            type="datetime-local"
            name="endDate"
            value={formData.endDate}
            onChange={handleChange}
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '15px' }}>
        <div>
          <label>Konum *</label>
          <input
            type="text"
            name="location"
            placeholder="Etkinlik Yeri"
            value={formData.location}
            onChange={handleChange}
            required
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
        <div>
          <label>Şehir</label>
          <input
            type="text"
            name="city"
            placeholder="Şehir"
            value={formData.city}
            onChange={handleChange}
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '15px' }}>
        <div>
          <label>Kapasite</label>
          <input
            type="number"
            name="capacity"
            placeholder="Katılımcı Sayısı"
            value={formData.capacity}
            onChange={handleChange}
            min="1"
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
        <div>
          <label>Kategori ID</label>
          <input
            type="number"
            name="categoryId"
            value={formData.categoryId}
            onChange={handleChange}
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
      </div>

      <div style={{ marginBottom: '15px', display: 'flex', alignItems: 'center', gap: '10px' }}>
        <input
          type="checkbox"
          name="isFree"
          checked={formData.isFree}
          onChange={handleChange}
        />
        <label>Ücretsiz mi?</label>
      </div>

      {!formData.isFree && (
        <div style={{ marginBottom: '15px' }}>
          <label>Fiyat</label>
          <input
            type="number"
            name="price"
            placeholder="Fiyat"
            value={formData.price}
            onChange={handleChange}
            min="0"
            step="0.01"
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>
      )}

      <button
        type="submit"
        disabled={loading}
        style={{
          padding: '10px 20px',
          backgroundColor: loading ? '#ccc' : '#007bff',
          color: 'white',
          border: 'none',
          borderRadius: '5px',
          cursor: loading ? 'not-allowed' : 'pointer',
          fontSize: '16px'
        }}
      >
        {loading ? 'Oluşturuluyor...' : '✓ Etkinlik Oluştur'}
      </button>

      <p style={{ marginTop: '10px', fontSize: '14px', color: '#666' }}>
        ℹ️ Etkinlik oluşturulduktan sonra admin onayını bekleyecektir.
      </p>
    </form>
  );
}

export default EventForm;

import React, { useState } from "react";

function EventForm({ onAdd }) {
  const [name, setName] = useState("");
  const [date, setDate] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!name || !date) return;
    onAdd({ name, date });
    setName("");
    setDate("");
  };

  return (
    <form onSubmit={handleSubmit} style={{ marginBottom: 24 }}>
      <h2>Etkinlik Ekle</h2>
      <input
        type="text"
        placeholder="Etkinlik Adı"
        value={name}
        onChange={e => setName(e.target.value)}
        required
      />
      <input
        type="date"
        value={date}
        onChange={e => setDate(e.target.value)}
        required
      />
      <button type="submit">Ekle</button>
    </form>
  );
}

export default EventForm;

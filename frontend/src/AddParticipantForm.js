import React, { useState } from "react";

function AddParticipantForm({ onAdd }) {
  const [name, setName] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!name.trim()) return;
    onAdd(name.trim());
    setName("");
  };

  return (
    <form onSubmit={handleSubmit} className="add-participant-form">
      <input
        type="text"
        placeholder="Katılımcı Adı"
        value={name}
        onChange={e => setName(e.target.value)}
        required
      />
      <button type="submit">Katılımcı Ekle</button>
    </form>
  );
}

export default AddParticipantForm;

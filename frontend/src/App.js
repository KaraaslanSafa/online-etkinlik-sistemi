

import './App.css';

import EventList from './EventList';
import EventForm from './EventForm';
import ParticipantList from './ParticipantList';
import AddParticipantForm from './AddParticipantForm';
import React, { useState } from 'react';


const initialEvents = [
  { id: 1, name: "Online Java Workshop", date: "2026-05-01", participants: ["Ali", "Ayşe"] },
  { id: 2, name: "React Başlangıç Eğitimi", date: "2026-05-10", participants: ["Mehmet"] },
];

function App() {
  const [events, setEvents] = useState(initialEvents);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [alert, setAlert] = useState("");

  const handleAddEvent = (event) => {
    setEvents([
      ...events,
      { ...event, id: events.length + 1, participants: [] },
    ]);
    setAlert("Etkinlik başarıyla eklendi!");
    setTimeout(() => setAlert(""), 2000);
  };

  const handleSelectEvent = (event) => {
    setSelectedEvent(event);
  };

  const handleBack = () => setSelectedEvent(null);

  const handleDeleteEvent = (id) => {
    setEvents(events.filter(e => e.id !== id));
    setSelectedEvent(null);
    setAlert("Etkinlik silindi!");
    setTimeout(() => setAlert(""), 2000);
  };

  const handleAddParticipant = (name) => {
    setEvents(events.map(ev =>
      ev.id === selectedEvent.id
        ? { ...ev, participants: [...ev.participants, name] }
        : ev
    ));
    setSelectedEvent({
      ...selectedEvent,
      participants: [...selectedEvent.participants, name],
    });
    setAlert("Katılımcı eklendi!");
    setTimeout(() => setAlert(""), 2000);
  };

  return (
    <div className="App">
      <h1>Online Etkinlik Sistemi <span role="img" aria-label="calendar">📅</span></h1>
      {alert && <div className="alert">{alert}</div>}
      {!selectedEvent ? (
        <div className="card">
          <EventForm onAdd={handleAddEvent} />
          <EventList
            events={events}
            onSelect={handleSelectEvent}
            onDelete={handleDeleteEvent}
          />
        </div>
      ) : (
        <div className="card">
          <button onClick={handleBack} style={{ marginBottom: 16 }}>← Geri</button>
          <h2>{selectedEvent.name} <span role="img" aria-label="event">🎉</span></h2>
          <p><b>Tarih:</b> {selectedEvent.date}</p>
          <p><b>Katılımcı Sayısı:</b> {selectedEvent.participants.length}</p>
          <AddParticipantForm onAdd={handleAddParticipant} />
          <ParticipantList participants={selectedEvent.participants} />
          <button className="delete-btn" onClick={() => handleDeleteEvent(selectedEvent.id)} style={{marginTop:16, background:'#ef4444'}}>Etkinliği Sil</button>
        </div>
      )}
    </div>
  );
}

export default App;

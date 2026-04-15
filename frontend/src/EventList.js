
import React from "react";

function EventList({ events, onSelect, onDelete }) {
  return (
    <div>
      <h2>Etkinlikler</h2>
      <ul>
        {events.map(event => (
          <li key={event.id} style={{display:'flex',alignItems:'center',justifyContent:'space-between',gap:8}}>
            <span>
              <button onClick={() => onSelect(event)} style={{ marginRight: 8 }}>
                Detay
              </button>
              <span style={{fontWeight:'bold'}}>{event.name}</span> - {event.date}
              <span style={{marginLeft:8, color:'#6366f1'}}>👥 {event.participants.length}</span>
            </span>
            <button className="delete-btn" onClick={() => onDelete(event.id)} style={{background:'#ef4444'}}>Sil</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default EventList;

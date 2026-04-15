import React from "react";

function ParticipantList({ participants }) {
  return (
    <div className="participant-list">
      <h3>Katılımcılar</h3>
      <ul>
        {participants && participants.length > 0 ? (
          participants.map((p, i) => <li key={i}>{p}</li>)
        ) : (
          <li>Henüz katılımcı yok.</li>
        )}
      </ul>
    </div>
  );
}

export default ParticipantList;

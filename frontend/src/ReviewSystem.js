import API_BASE_URL from './config';
import React, { useState, useEffect } from 'react';

const ReviewSystem = ({ eventId }) => {
    const [reviews, setReviews] = useState([]);
    const [rating, setRating] = useState(5);
    const [comment, setComment] = useState('');
    const [title, setTitle] = useState('');
    const [alert, setAlert] = useState('');

    useEffect(() => {
        if (eventId) {
            fetchReviews();
        }
    }, [eventId]);

    const fetchReviews = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/api/event-reviews/event/${eventId}`);
            if (response.ok) {
                const data = await response.json();
                setReviews(data);
            }
        } catch (error) {
            console.error('Yorumlar yüklenemedi:', error);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch(`${API_BASE_URL}/api/event-reviews`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify({
                    eventId: eventId,
                    participantId: 1, // Mock participant ID
                    rating: rating,
                    title: title,
                    comment: comment
                })
            });

            if (response.ok) {
                setAlert('Yorumunuz eklendi!');
                setComment('');
                setTitle('');
                setRating(5);
                fetchReviews();
                setTimeout(() => setAlert(''), 3000);
            } else {
                setAlert('Yorum eklenirken hata oluştu.');
            }
        } catch (error) {
            console.error('Yorum ekleme hatası:', error);
        }
    };

    const handleHelpful = async (reviewId) => {
        try {
            await fetch(`${API_BASE_URL}/api/event-reviews/${reviewId}/mark-helpful`, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
            });
            fetchReviews();
        } catch (error) {
            console.error('Faydalı işaretleme hatası:', error);
        }
    };

    return (
        <div style={{ marginTop: '30px', padding: '20px', borderTop: '2px solid #eee' }}>
            <h3>⭐ Değerlendirmeler ve Yorumlar</h3>
            {alert && <div style={{ padding: '10px', backgroundColor: '#e6ffe6', color: 'green', marginBottom: '10px' }}>{alert}</div>}

            <form onSubmit={handleSubmit} style={{ marginBottom: '20px', display: 'flex', flexDirection: 'column', gap: '10px' }}>
                <select value={rating} onChange={(e) => setRating(parseInt(e.target.value))} style={{ padding: '8px' }}>
                    <option value={5}>⭐⭐⭐⭐⭐ (5/5 Mükemmel)</option>
                    <option value={4}>⭐⭐⭐⭐ (4/5 İyi)</option>
                    <option value={3}>⭐⭐⭐ (3/5 Orta)</option>
                    <option value={2}>⭐⭐ (2/5 Kötü)</option>
                    <option value={1}>⭐ (1/5 Çok Kötü)</option>
                </select>
                <input
                    type="text"
                    placeholder="Başlık (Örn: Harika bir deneyimdi!)"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    style={{ padding: '8px' }}
                    required
                />
                <textarea
                    placeholder="Yorumunuz..."
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    style={{ padding: '8px', minHeight: '80px' }}
                    required
                />
                <button type="submit" style={{ padding: '10px', backgroundColor: '#4caf50', color: 'white', border: 'none', cursor: 'pointer' }}>
                    Yorum Yap
                </button>
            </form>

            <div>
                {reviews.length === 0 ? <p>Henüz yorum yapılmamış.</p> : reviews.map(review => (
                    <div key={review.id} style={{ padding: '15px', backgroundColor: '#f9f9f9', marginBottom: '10px', borderRadius: '5px' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <strong>{review.title}</strong>
                            <span>{'⭐'.repeat(review.rating)}</span>
                        </div>
                        <p style={{ margin: '10px 0' }}>{review.comment}</p>
                        <button onClick={() => handleHelpful(review.id)} style={{ fontSize: '12px', padding: '5px 10px', cursor: 'pointer' }}>
                            👍 Faydalı ({review.helpfulCount || 0})
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default ReviewSystem;

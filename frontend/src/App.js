import API_BASE_URL from './config';
import './App.css';
import React, { useState, useEffect } from 'react';
import EventForm from './EventForm';
import AdminApprovalPanel from './AdminApprovalPanel';
import Login from './Login';
import ParticipantList from './ParticipantList';

function App() {
  const [activePortal, setActivePortal] = useState('customer'); // 'customer', 'organizer', 'admin'
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);

  // Authentication State
  const [token, setToken] = useState(localStorage.getItem('token') || '');
  const [currentUser, setCurrentUser] = useState(() => {
    try {
      const stored = localStorage.getItem('user');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });

  // Müşteri Kayıt Sorgulama State'leri
  const [searchEmail, setSearchEmail] = useState('');
  const [customerRegistrations, setCustomerRegistrations] = useState([]);
  const [searchMessage, setSearchMessage] = useState('');
  const [searchingRegistrations, setSearchingRegistrations] = useState(false);

  // Müşteri Kayıt Formu State'leri
  const [regForm, setRegForm] = useState({ firstName: '', lastName: '', email: '', phoneNumber: '' });
  const [regMessage, setRegMessage] = useState('');
  const [regLoading, setRegLoading] = useState(false);

  // Etkinlik Değerlendirme (Review) State'leri
  const [reviews, setReviews] = useState([]);
  const [newReview, setNewReview] = useState({ reviewerName: '', rating: 5, comment: '' });
  const [reviewMessage, setReviewMessage] = useState('');

  const [alert, setAlert] = useState('');
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  // İstatistikler State'leri (Admin Paneli için)
  const [stats, setStats] = useState({
    totalEvents: 0,
    approvedEvents: 0,
    pendingEvents: 0,
    rejectedEvents: 0,
    totalCapacity: 0,
  });

  // Organizatör Sekmeleri ve Promosyon State'leri
  const [organizerTab, setOrganizerTab] = useState('events'); // 'events', 'promotions'
  const [showEventModal, setShowEventModal] = useState(false);
  const [showParticipantsModalFor, setShowParticipantsModalFor] = useState(null);
  const [promotions, setPromotions] = useState([]);
  const [promoForm, setPromoForm] = useState({
    title: '',
    description: '',
    campaignCode: '',
    discountPercentage: 10,
    eventId: '',
    maxParticipants: 50
  });
  const [promoMessage, setPromoMessage] = useState('');

  // Admin Sekmeleri ve Kullanıcı Rolü State'leri
  const [adminTab, setAdminTab] = useState('approvals'); // 'approvals', 'users'
  const [usersList, setUsersList] = useState([]);
  const [userRoleMessage, setUserRoleMessage] = useState('');
  const [categories, setCategories] = useState([]);
  const [newCategoryName, setNewCategoryName] = useState('');

  // Müşteri Ödeme/Kayıt esnasında kupon kullanım State'leri
  const [couponCodeInput, setCouponCodeInput] = useState('');
  const [appliedPromo, setAppliedPromo] = useState(null);
  const [couponMsg, setCouponMsg] = useState('');

  // Dynamic user session effect (such as pre-filling forms)
  useEffect(() => {
    if (currentUser) {
      setRegForm({
        firstName: currentUser.firstName || '',
        lastName: currentUser.lastName || '',
        email: currentUser.email || '',
        phoneNumber: currentUser.phoneNumber || ''
      });
      setNewReview(prev => ({
        ...prev,
        reviewerName: `${currentUser.firstName || ''} ${currentUser.lastName || ''}`.trim() || currentUser.username
      }));
      setSearchEmail(currentUser.email || '');
    } else {
      setRegForm({ firstName: '', lastName: '', email: '', phoneNumber: '' });
      setNewReview({ reviewerName: '', rating: 5, comment: '' });
      setSearchEmail('');
    }
  }, [currentUser]);

  useEffect(() => {
    fetchAllEvents();
  }, [refreshTrigger]);

  const fetchAllEvents = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/api/events`);
      if (response.ok) {
        const data = await response.json();
        setEvents(data);

        // İstatistikleri Hesapla
        const approved = data.filter(e => e.approvalStatus === 'APPROVED' || !e.approvalStatus).length;
        const pending = data.filter(e => e.approvalStatus === 'PENDING').length;
        const rejected = data.filter(e => e.approvalStatus === 'REJECTED').length;
        const capacity = data.reduce((sum, e) => sum + (e.capacity || 0), 0);

        setStats({
          totalEvents: data.length,
          approvedEvents: approved,
          pendingEvents: pending,
          rejectedEvents: rejected,
          totalCapacity: capacity,
        });
      }
    } catch (error) {
      console.error('Veri çekme hatası:', error);
    } finally {
      setLoading(false);
    }
  };

  const triggerRefresh = () => setRefreshTrigger(prev => prev + 1);

  // JWT Yetkilendirme Başlık Yardımcısı
  const getAuthHeaders = (extraHeaders = {}) => {
    const storedToken = token || localStorage.getItem('token');
    const headers = { ...extraHeaders };
    if (storedToken) {
      headers['Authorization'] = `Bearer ${storedToken}`;
    }
    return headers;
  };

  // Promosyon Çekme ve Ekleme Fonksiyonları
  const fetchPromotions = async () => {
    if (!currentUser) return;
    try {
      const res = await fetch(`${API_BASE_URL}/api/campaigns/organizer/${currentUser.id}`, {
        headers: getAuthHeaders()
      });
      if (res.ok) {
        const data = await res.json();
        setPromotions(data);
      }
    } catch (err) {
      console.error("Promosyonlar yüklenemedi:", err);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/categories`);
      if (res.ok) {
        const data = await res.json();
        setCategories(data);
      }
    } catch (err) {
      console.error("Kategoriler yüklenemedi:", err);
    }
  };

  const handleAddCategory = async (e) => {
    if (e) e.preventDefault();
    if (!newCategoryName.trim()) return;
    try {
      const res = await fetch(`${API_BASE_URL}/api/categories`, {
        method: 'POST',
        headers: getAuthHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify({ name: newCategoryName.trim() })
      });
      if (res.ok) {
        setNewCategoryName('');
        setUserRoleMessage("✓ Kategori başarıyla eklendi.");
        fetchCategories();
        setTimeout(() => setUserRoleMessage(''), 3000);
      } else {
        const err = await res.text();
        setUserRoleMessage(`❌ Hata: ${err || 'Kategori eklenemedi.'}`);
      }
    } catch (err) {
      console.error(err);
      setUserRoleMessage("❌ Bağlantı hatası!");
    }
  };

  const handleDeleteCategory = async (categoryId) => {
    if (!window.confirm("Bu kategoriyi silmek istediğinize emin misiniz?")) return;
    try {
      const res = await fetch(`${API_BASE_URL}/api/categories/${categoryId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
      });
      if (res.status === 200 || res.status === 204) {
        setUserRoleMessage("✓ Kategori başarıyla silindi.");
        fetchCategories();
        setTimeout(() => setUserRoleMessage(''), 3000);
      } else {
        const err = await res.text();
        setUserRoleMessage(`❌ Hata: ${err || 'Kategori silinemedi.'}`);
      }
    } catch (err) {
      console.error(err);
      setUserRoleMessage("❌ Bağlantı hatası!");
    }
  };

  const handleDeactivatePromo = async (promoId) => {
    if (!window.confirm("Bu promosyon kodunu kapatmak/iptal etmek istediğinize emin misiniz?")) return;
    try {
      const res = await fetch(`${API_BASE_URL}/api/campaigns/${promoId}/deactivate`, {
        method: 'POST',
        headers: getAuthHeaders()
      });
      if (res.ok) {
        setPromoMessage("✓ Promosyon başarıyla kapatıldı.");
        fetchPromotions();
        setTimeout(() => setPromoMessage(''), 3000);
      } else {
        const err = await res.text();
        setPromoMessage(`❌ Hata: ${err || 'Deaktif edilemedi.'}`);
      }
    } catch (err) {
      console.error(err);
      setPromoMessage("❌ Bağlantı hatası!");
    }
  };

  const handleMarkReviewHelpful = async (reviewId) => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/event-reviews/${reviewId}/mark-helpful`, {
        method: 'POST'
      });
      if (res.ok) {
        fetchReviews(selectedEvent.id);
      }
    } catch (err) {
      console.error(err);
    }
  };

  const handleDeleteReview = async (reviewId) => {
    if (!window.confirm("Bu yorumu silmek istediğinize emin misiniz?")) return;
    try {
      const res = await fetch(`${API_BASE_URL}/api/event-reviews/${reviewId}?participantId=1`, {
        method: 'DELETE'
      });
      if (res.status === 200 || res.status === 204) {
        setReviewMessage("✓ Yorum başarıyla silindi.");
        fetchReviews(selectedEvent.id);
        setTimeout(() => setReviewMessage(''), 3000);
      } else {
        const errText = await res.text();
        setReviewMessage(`❌ Hata: ${errText || 'Yorum silinemedi.'}`);
      }
    } catch (err) {
      console.error(err);
      setReviewMessage("❌ Bağlantı hatası!");
    }
  };

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    if (currentUser && activePortal === 'organizer' && organizerTab === 'promotions') {
      fetchPromotions();
    }
  }, [currentUser, activePortal, organizerTab]);

  const handleCreatePromo = async (e) => {
    e.preventDefault();
    if (!currentUser) return;
    if (!promoForm.title || !promoForm.campaignCode || !promoForm.discountPercentage) {
      setPromoMessage("⚠️ Lütfen zorunlu alanları doldurun.");
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/api/campaigns`, {
        method: 'POST',
        headers: getAuthHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify({
          organizerId: currentUser.id,
          eventId: promoForm.eventId ? Number(promoForm.eventId) : null,
          title: promoForm.title,
          description: promoForm.description,
          campaignCode: promoForm.campaignCode.trim().toUpperCase(),
          discountPercentage: Number(promoForm.discountPercentage),
          maxParticipants: Number(promoForm.maxParticipants),
          campaignType: 'DISCOUNT'
        })
      });

      if (response.ok) {
        setPromoMessage("✓ Promosyon kodu başarıyla oluşturuldu!");
        setPromoForm({
          title: '',
          description: '',
          campaignCode: '',
          discountPercentage: 10,
          eventId: '',
          maxParticipants: 50
        });
        fetchPromotions();
        setTimeout(() => setPromoMessage(''), 3000);
      } else {
        const txt = await response.text();
        setPromoMessage(`❌ Hata: ${txt || 'Promosyon oluşturulurken hata oluştu.'}`);
      }
    } catch (err) {
      console.error(err);
      setPromoMessage("❌ Bağlantı hatası oluştu.");
    }
  };

  // Admin Kullanıcı Yönetimi Fonksiyonları
  const fetchUsersList = async () => {
    const storedToken = token || localStorage.getItem('token');
    if (storedToken && storedToken.startsWith('mock.')) {
      setUserRoleMessage("⚠️ Sistem şu anda geçici oturum (Sandbox Fallback) modunda çalışıyor. Lütfen yukarıdaki 'Güvenli Çıkış Yap' butonuna tıklayarak çıkış yapın, ardından 'admin' / 'admin123' bilgileriyle tekrar giriş yaparak gerçek veritabanı bağlantısını etkinleştirin.");
      setUsersList([]);
      return;
    }

    try {
      const headers = getAuthHeaders();
      const res = await fetch(`${API_BASE_URL}/api/users?size=100`, {
        headers: headers
      });
      if (res.ok) {
        const data = await res.json();
        setUsersList(data.content || data || []);
        setUserRoleMessage(''); // Clear any old messages on success
      } else {
        const errText = await res.text();
        console.error("Fetch users error:", res.status, errText);
        if (res.status === 401 || res.status === 403) {
          setUserRoleMessage("⚠️ Yetkilendirme hatası (Oturum süresi dolmuş veya geçersiz olabilir). Lütfen yukarıdaki 'Güvenli Çıkış Yap' butonuna tıklayarak çıkış yapın, ardından 'admin' / 'admin123' bilgileriyle yeniden giriş yapın.");
        } else {
          setUserRoleMessage(`❌ Kullanıcı listesi alınamadı (${res.status}): ${errText || 'Sunucu hatası veya Yetkisiz erişim'}`);
        }
        setUsersList([]);
      }
    } catch (err) {
      console.error("Kullanıcılar yüklenemedi:", err);
      setUserRoleMessage(`❌ Bağlantı hatası: ${err.message}`);
    }
  };

  useEffect(() => {
    if (currentUser && currentUser.userRole === 'ADMIN' && activePortal === 'admin' && adminTab === 'users') {
      fetchUsersList();
    }
  }, [currentUser, activePortal, adminTab]);

  const handleChangeUserRole = async (userId, newRole) => {
    try {
      const targetUser = usersList.find(u => u.id === userId);
      if (!targetUser) return;

      const updatedUserPayload = {
        ...targetUser,
        userRole: newRole
      };

      const res = await fetch(`${API_BASE_URL}/api/users/${userId}`, {
        method: 'PUT',
        headers: getAuthHeaders({ 'Content-Type': 'application/json' }),
        body: JSON.stringify(updatedUserPayload)
      });

      if (res.ok) {
        setUserRoleMessage("✓ Kullanıcı rolü başarıyla güncellendi!");
        fetchUsersList();
        setTimeout(() => setUserRoleMessage(''), 3000);
      } else {
        const err = await res.text();
        setUserRoleMessage(`❌ Hata: ${err || 'Rol güncellenemedi.'}`);
      }
    } catch (err) {
      console.error(err);
      setUserRoleMessage("❌ Bağlantı hatası!");
    }
  };

  const handleDeleteUser = async (userId) => {
    if (userId === currentUser.id) {
      setUserRoleMessage("❌ Hata: Kendi admin hesabınızı silemezsiniz!");
      setTimeout(() => setUserRoleMessage(''), 3000);
      return;
    }
    if (!window.confirm("Bu kullanıcıyı kalıcı olarak silmek istediğinize emin misiniz?")) return;
    try {
      const res = await fetch(`${API_BASE_URL}/api/users/${userId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
      });
      if (res.ok) {
        setUserRoleMessage("✓ Kullanıcı sistemden başarıyla silindi.");
        fetchUsersList();
        setTimeout(() => setUserRoleMessage(''), 3000);
      } else {
        const err = await res.text();
        setUserRoleMessage(`❌ Hata: ${err || 'Kullanıcı silinemedi.'}`);
        setTimeout(() => setUserRoleMessage(''), 4000);
      }
    } catch (err) {
      console.error(err);
      setUserRoleMessage("❌ Bağlantı hatası!");
      setTimeout(() => setUserRoleMessage(''), 3000);
    }
  };

  const handleDeleteEvent = async (eventId) => {
    if (!window.confirm("Bu etkinliği sistemden kalıcı olarak silmek istediğinize emin misiniz?")) return;
    try {
      const res = await fetch(`${API_BASE_URL}/api/events/${eventId}`, {
        method: 'DELETE',
        headers: getAuthHeaders()
      });
      if (res.status === 200 || res.status === 204) {
        setUserRoleMessage("✓ Etkinlik sistemden başarıyla silindi.");
        triggerRefresh();
        setTimeout(() => setUserRoleMessage(''), 3000);
      } else {
        const err = await res.text();
        setUserRoleMessage(`❌ Hata: ${err || 'Etkinlik silinemedi.'}`);
        setTimeout(() => setUserRoleMessage(''), 4000);
      }
    } catch (err) {
      console.error(err);
      setUserRoleMessage("❌ Bağlantı hatası!");
      setTimeout(() => setUserRoleMessage(''), 3000);
    }
  };

  // Müşteri Kupon Sorgulama ve Uygulama Fonksiyonu
  const handleApplyCoupon = async () => {
    if (!couponCodeInput.trim()) {
      setCouponMsg("⚠️ Lütfen bir kupon kodu yazın.");
      return;
    }
    setCouponMsg("Kupon sorgulanıyor...");
    try {
      const res = await fetch(`${API_BASE_URL}/api/campaigns/code/${couponCodeInput.trim().toUpperCase()}`);
      if (res.ok) {
        const promo = await res.json();
        if (!promo.isActive || !promo.isCurrentlyActive) {
          setCouponMsg("❌ Bu kupon kodunun süresi geçmiş veya deaktif edilmiş.");
          setAppliedPromo(null);
          return;
        }
        if (promo.eventId && promo.eventId !== selectedEvent.id) {
          setCouponMsg("❌ Bu kupon kodu yalnızca belirli bir etkinlikte geçerlidir.");
          setAppliedPromo(null);
          return;
        }
        setAppliedPromo(promo);
        setCouponMsg(`✓ Kupon uygulandı! %${promo.discountPercentage} indirim kazandınız.`);
      } else {
        setCouponMsg("❌ Geçersiz kupon kodu!");
        setAppliedPromo(null);
      }
    } catch (err) {
      console.error(err);
      setCouponMsg("❌ Kupon sorgulanamadı!");
    }
  };

  useEffect(() => {
    setAppliedPromo(null);
    setCouponCodeInput('');
    setCouponMsg('');
  }, [selectedEvent]);

  // Müşteri Kayıt Sorgulama Fonksiyonu
  const handleQueryRegistrations = async (e) => {
    if (e) e.preventDefault();
    const targetEmail = searchEmail.trim();
    if (!targetEmail) return;

    setSearchingRegistrations(true);
    setSearchMessage('');
    setCustomerRegistrations([]);

    try {
      // 1. Katılımcıyı email ile getir
      const emailEncoded = encodeURIComponent(targetEmail);
      const pRes = await fetch(`${API_BASE_URL}/api/participants/email/${emailEncoded}`);

      if (pRes.status === 404) {
        setSearchMessage('Bu e-posta adresine ait bilet kaydı bulunamadı.');
        setSearchingRegistrations(false);
        return;
      }

      if (pRes.ok) {
        const participant = await pRes.json();

        // 2. Katılımcının etkinliklerini getir
        const regRes = await fetch(`${API_BASE_URL}/api/event-participants/participant/${participant.id}`);
        if (regRes.ok) {
          const regs = await regRes.json();
          setCustomerRegistrations(regs);
          if (regs.length === 0) {
            setSearchMessage('Kayıtlı olduğunuz herhangi bir aktif etkinlik bulunmamaktadır.');
          }
        } else {
          setSearchMessage('Kayıtlar çekilirken bir sorun oluştu.');
        }
      }
    } catch (error) {
      console.error('Sorgulama hatası:', error);
      setSearchMessage('Sorgulama sırasında bağlantı hatası oluştu.');
    } finally {
      setSearchingRegistrations(false);
    }
  };

  const handleCancelRegistration = async (regId, eventTitle, startDate) => {
    const now = new Date();
    const eventDate = new Date(startDate);
    if (eventDate <= now) {
      alert("⚠️ Bu etkinlik başladı veya geçmişte kaldı! Etkinlik başlama tarihinden sonra bilet iptal edilemez.");
      return;
    }

    if (!window.confirm(`"${eventTitle}" biletinizi iptal etmek istediğinize emin misiniz?`)) {
      return;
    }

    try {
      const res = await fetch(`${API_BASE_URL}/api/event-participants/${regId}/status?status=CANCELLED`, {
        method: 'PATCH'
      });
      if (res.ok) {
        alert("✓ Biletiniz başarıyla iptal edildi.");
        handleQueryRegistrations();
        triggerRefresh();
      } else {
        const errText = await res.text();
        alert(`❌ Hata: ${errText || 'İptal işlemi başarısız.'}`);
      }
    } catch (err) {
      console.error(err);
      alert("❌ Bağlantı hatası!");
    }
  };

  // Auto trigger bilet lookup on user login
  useEffect(() => {
    if (currentUser && currentUser.userRole === 'USER' && activePortal === 'customer') {
      handleQueryRegistrations();
    }
  }, [currentUser, activePortal]);

  // Müşteri Etkinliğe Kaydolma Fonksiyonu
  const handleRegister = async (e) => {
    e.preventDefault();
    if (!selectedEvent) return;

    if (!regForm.firstName.trim() || !regForm.lastName.trim() || !regForm.email.trim()) {
      setRegMessage('⚠️ Lütfen tüm zorunlu alanları doldurun!');
      return;
    }

    setRegLoading(true);
    setRegMessage('');

    try {
      let participant = null;
      const emailEncoded = encodeURIComponent(regForm.email.trim());
      const searchRes = await fetch(`${API_BASE_URL}/api/participants/email/${emailEncoded}`);

      if (searchRes.ok) {
        participant = await searchRes.json();
      } else if (searchRes.status === 404) {
        // Yeni katılımcı oluştur
        const createRes = await fetch(`${API_BASE_URL}/api/participants`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            firstName: regForm.firstName.trim(),
            lastName: regForm.lastName.trim(),
            email: regForm.email.trim(),
            phoneNumber: regForm.phoneNumber.trim(),
          }),
        });

        if (createRes.ok) {
          participant = await createRes.json();
        } else {
          throw new Error('Katılımcı kaydı oluşturulamadı.');
        }
      }

      // Etkinliğe kaydet
      const registerRes = await fetch(
        `${API_BASE_URL}/api/event-participants/register?eventId=${selectedEvent.id}&participantId=${participant.id}`,
        { method: 'POST' }
      );

      if (registerRes.ok) {
        setRegMessage('✅ Etkinliğe kaydınız başarıyla tamamlandı! Bilet numaranız üretildi.');
        if (!currentUser) {
          setRegForm({ firstName: '', lastName: '', email: '', phoneNumber: '' });
        }
        triggerRefresh();

        // Seçili etkinliğin katılımcı sayısını lokalde arttır
        setSelectedEvent(prev => ({
          ...prev,
          participantCount: (prev.participantCount || 0) + 1
        }));
      } else {
        const errText = await registerRes.text();
        setRegMessage(`❌ Hata: ${errText || 'Kayıt işlemi başarısız oldu.'}`);
      }
    } catch (error) {
      console.error(error);
      setRegMessage('❌ Bağlantı hatası oluştu!');
    } finally {
      setRegLoading(false);
    }
  };

  // Yorum Çekme ve Ekleme Fonksiyonları
  useEffect(() => {
    if (selectedEvent) {
      fetchReviews(selectedEvent.id);
    }
  }, [selectedEvent]);

  const fetchReviews = async (eventId) => {
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

  const handleAddReview = async (e) => {
    e.preventDefault();
    if (!selectedEvent) return;
    if (!newReview.reviewerName.trim() || !newReview.comment.trim()) {
      setReviewMessage('⚠️ Lütfen adınızı ve yorumunuzu yazın.');
      return;
    }

    setReviewMessage('Yorum gönderiliyor...');

    try {
      // 1. Find or create a Participant for this review to satisfy Database constraint
      let participant = null;
      const email = currentUser ? currentUser.email : `${newReview.reviewerName.trim().toLowerCase().replace(/[^a-zA-Z0-9]/g, '')}@example.com`;
      const emailEncoded = encodeURIComponent(email);

      // Search if participant exists
      const searchRes = await fetch(`${API_BASE_URL}/api/participants/email/${emailEncoded}`);
      if (searchRes.ok) {
        participant = await searchRes.json();
      } else {
        // Create a new participant for the review
        const names = newReview.reviewerName.trim().split(' ');
        const firstName = names[0];
        const lastName = names.slice(1).join(' ') || 'Değerlendiren';

        const createRes = await fetch(`${API_BASE_URL}/api/participants`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            firstName: firstName,
            lastName: lastName,
            email: email,
            phoneNumber: '05555555555'
          })
        });

        if (createRes.ok) {
          participant = await createRes.json();
        } else {
          throw new Error('Yorumcu profili oluşturulamadı.');
        }
      }

      // 2. Submit the review to the backend using URLSearchParams query parameters (@RequestParam)
      const params = new URLSearchParams({
        eventId: selectedEvent.id,
        participantId: participant.id,
        rating: Number(newReview.rating),
        title: 'Etkinlik Değerlendirmesi',
        comment: newReview.comment.trim()
      });

      const response = await fetch(`${API_BASE_URL}/api/event-reviews?${params.toString()}`, {
        method: 'POST'
      });

      if (response.ok) {
        setReviewMessage('✓ Değerlendirmeniz başarıyla eklendi!');
        if (!currentUser) {
          setNewReview({ reviewerName: '', rating: 5, comment: '' });
        } else {
          setNewReview(prev => ({ ...prev, comment: '' }));
        }
        fetchReviews(selectedEvent.id);
        setTimeout(() => setReviewMessage(''), 4000);
      } else {
        const errText = await response.text();
        let parsedErr = 'Değerlendirme eklenirken bir hata oluştu.';
        try {
          const parsed = JSON.parse(errText);
          parsedErr = parsed.message || parsedErr;
        } catch { }
        setReviewMessage(`❌ Hata: ${parsedErr}`);
      }
    } catch (error) {
      console.error(error);
      setReviewMessage('❌ Bağlantı hatası oluştu!');
    }
  };

  const handleEventCreated = () => {
    setAlert('🎉 Yeni etkinlik başarıyla taslak olarak oluşturuldu ve Admin onayına gönderildi!');
    triggerRefresh();
    setTimeout(() => setAlert(''), 5000);
  };

  const handleBackToEvents = () => {
    setSelectedEvent(null);
    setRegMessage('');
    setReviews([]);
  };

  const handleLoginSuccess = (userToken, userObj) => {
    setToken(userToken);
    setCurrentUser(userObj);
    setAlert(`👋 Başarıyla giriş yapıldı: Hoş geldiniz, ${userObj.firstName || userObj.username}!`);

    // Rolüne göre otomatik portala yönlendir
    if (userObj.userRole === 'ADMIN') {
      setActivePortal('admin');
    } else if (userObj.userRole === 'ORGANIZER') {
      setActivePortal('organizer');
    } else {
      setActivePortal('customer');
    }

    handleBackToEvents();
    setTimeout(() => setAlert(''), 4000);
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken('');
    setCurrentUser(null);
    setCustomerRegistrations([]);
    setAlert('👋 Oturumunuz sonlandırıldı.');
    setActivePortal('customer'); // Çıkış yapınca varsayılan müşteri portalına yönlendir
    handleBackToEvents();
    setTimeout(() => setAlert(''), 3000);
  };

  const getCategoryName = (id) => {
    if (categories && categories.length > 0) {
      const found = categories.find(c => c.id === Number(id));
      if (found) return found.name;
    }
    switch (id) {
      case 1: return 'Müzik & Konser';
      case 2: return 'Spor & Egzersiz';
      case 3: return 'Tiyatro & Sanat';
      case 4: return 'Eğitim & Seminer';
      case 5: return 'Teknoloji & Yazılım';
      default: return 'Diğer';
    }
  };

  const getRoleBadge = (role) => {
    switch (role) {
      case 'ADMIN': return '🛡️ Yönetici';
      case 'ORGANIZER': return '💼 Organizatör';
      default: return '👤 Müşteri';
    }
  };

  // Onaylanmış etkinlikleri filtrele
  const approvedEvents = events.filter(e => e.approvalStatus === 'APPROVED' || !e.approvalStatus);

  // Frontend filtre state'leri
  const [filterCategory, setFilterCategory] = React.useState('');
  const [filterCity, setFilterCity] = React.useState('');
  const [filterFree, setFilterFree] = React.useState(false);
  const [filterMaxPrice, setFilterMaxPrice] = React.useState('');
  const [searchKeyword, setSearchKeyword] = React.useState('');

  const filteredEvents = approvedEvents.filter(ev => {
    if (filterCategory && String(ev.categoryId) !== String(filterCategory)) return false;
    if (filterCity && !(ev.city || '').toLowerCase().includes(filterCity.toLowerCase())) return false;
    if (filterFree && !ev.isFree) return false;
    if (filterMaxPrice && ev.price > Number(filterMaxPrice)) return false;
    if (searchKeyword && !(ev.title || '').toLowerCase().includes(searchKeyword.toLowerCase()) &&
      !(ev.description || '').toLowerCase().includes(searchKeyword.toLowerCase())) return false;
    return true;
  });

  return (
    <div className="App">
      {/* Premium Header */}
      <header className="app-header">
        <div className="header-top-bar">
          <div className="logo-section">
            <span className="logo-emoji">⚡</span>
            <div>
              <h1>Etkinlik Dünyası</h1>
              <p className="subtitle">Premium Online Etkinlik & Bilet Yönetim Sistemi</p>
            </div>
          </div>

          {/* Authenticated user banner */}
          <div className="header-auth-badge">
            {currentUser ? (
              <div className="auth-user-info animate-fade">
                <span className="user-icon-avatar">👤</span>
                <div className="user-text-meta">
                  <span className="username-label">{currentUser.firstName} {currentUser.lastName}</span>
                  <span className="user-role-tag">{getRoleBadge(currentUser.userRole)}</span>
                </div>
                <button onClick={handleLogout} className="header-btn-logout" title="Oturumu Kapat">
                  🚪 Çıkış
                </button>
              </div>
            ) : (
              <button
                className="header-btn-login"
                onClick={() => { setActivePortal('customer'); setSelectedEvent(null); }}
              >
                🔑 Giriş Yap / Kayıt Ol
              </button>
            )}
          </div>
        </div>

        {/* Portal Switcher Tabs */}
        <div className="portal-tabs">
          <button
            className={`portal-tab-btn ${activePortal === 'customer' ? 'active' : ''}`}
            onClick={() => { setActivePortal('customer'); handleBackToEvents(); }}
          >
            👤 Müşteri Portalı
          </button>
          
          <button
            className={`portal-tab-btn ${activePortal === 'organizer' ? 'active' : ''}`}
            onClick={() => { setActivePortal('organizer'); handleBackToEvents(); }}
          >
            💼 Organizatör Portalı
          </button>

          {(currentUser && currentUser.userRole === 'ADMIN') && (
            <button
              className={`portal-tab-btn ${activePortal === 'admin' ? 'active' : ''}`}
              onClick={() => { setActivePortal('admin'); handleBackToEvents(); }}
            >
              🛡️ Yönetici Portalı
            </button>
          )}
        </div>
      </header>

      <main className="app-main-content">
        {alert && <div className="app-banner-alert">{alert}</div>}

        {/* ========================================================
            1. MÜŞTERİ PORTALI
           ======================================================== */}
        {activePortal === 'customer' && (
          <div className="portal-container animate-fade">
            {!selectedEvent ? (
              <div className="customer-dashboard">

                {/* Giriş yapmamış müşterilere sunulan hoşgeldin paneli */}
                {!currentUser && (
                  <div className="customer-landing-auth-card card animate-slide">
                    <div className="landing-text">
                      <h3>🎟️ Biletlerinizi Kolayca Yönetin!</h3>
                      <p>Hemen kayıt olup giriş yaparak, bilet geçmişinize anında erişebilir, etkinlik formlarını otomatik doldurarak hızlı bilet üretebilirsiniz.</p>
                    </div>
                    <div className="landing-login-module">
                      <Login defaultRole="USER" onLoginSuccess={handleLoginSuccess} />
                    </div>
                  </div>
                )}

                {/* Bilet Sorgulama Bölümü */}
                <section className="dashboard-section query-section card">
                  <div className="section-header">
                    <h2>🔍 Biletlerim & Kayıtlarım</h2>
                    <p className="section-subtitle">Daha önce kaydolduğunuz etkinlikleri sorgulayın ve biletinizi görüntüleyin.</p>
                  </div>
                  <form onSubmit={handleQueryRegistrations} className="query-form">
                    <input
                      type="email"
                      placeholder="Kayıt olduğunuz e-posta adresini girin..."
                      value={searchEmail}
                      onChange={(e) => setSearchEmail(e.target.value)}
                      required
                    />
                    <button type="submit" disabled={searchingRegistrations}>
                      {searchingRegistrations ? 'Sorgulanıyor...' : 'Kayıtları Sorgula'}
                    </button>
                  </form>

                  {searchMessage && <p className="query-info-msg">{searchMessage}</p>}

                  {customerRegistrations.length > 0 && (
                    <div className="registrations-results">
                      <h3>Biletleriniz ({customerRegistrations.length})</h3>
                      <div className="tickets-grid">
                        {customerRegistrations.map((reg) => {
                          const associatedEvent = events.find(e => e.id === reg.eventId);
                          const eventStartDate = associatedEvent ? new Date(associatedEvent.startDate) : null;
                          const isPast = eventStartDate ? (eventStartDate <= new Date()) : false;

                          return (
                            <div key={reg.id} className="ticket-card animate-slide" style={{ borderLeft: reg.status === 'CANCELLED' ? '4px solid #ff3b30' : '4px solid #34c759' }}>
                              <div className="ticket-stub">
                                <span className="ticket-badge" style={{ backgroundColor: reg.status === 'CANCELLED' ? '#ff3b30' : '#34c759' }}>
                                  {reg.status === 'CANCELLED' ? 'İPTAL' : 'BİLET'}
                                </span>
                                <h4>{associatedEvent ? associatedEvent.title : `Etkinlik #${reg.eventId}`}</h4>
                              </div>
                              <div className="ticket-details">
                                <p><strong>👤 Katılımcı:</strong> {reg.participantName}</p>
                                <p><strong>🎟️ Kayıt Tarihi:</strong> {new Date(reg.registeredAt).toLocaleDateString('tr-TR')}</p>
                                {associatedEvent && (
                                  <>
                                    <p><strong>📍 Konum:</strong> {associatedEvent.location}</p>
                                    <p><strong>📅 Tarih:</strong> {new Date(associatedEvent.startDate).toLocaleString('tr-TR')}</p>
                                  </>
                                )}
                                {reg.status === 'CANCELLED' ? (
                                  <div style={{
                                    marginTop: '12px',
                                    padding: '6px 12px',
                                    backgroundColor: '#fce8e6',
                                    color: '#c5221f',
                                    borderRadius: '6px',
                                    fontSize: '12px',
                                    fontWeight: 'bold',
                                    textAlign: 'center'
                                  }}>
                                    ❌ İptal Edildi
                                  </div>
                                ) : isPast ? (
                                  <p style={{ color: '#8e8e93', fontSize: '11px', marginTop: '10px', fontStyle: 'italic', fontWeight: 'bold' }}>
                                    ⚠️ Etkinlik başladığı için iptal edilemez.
                                  </p>
                                ) : (
                                  <button
                                    onClick={() => handleCancelRegistration(reg.id, associatedEvent ? associatedEvent.title : `Etkinlik #${reg.eventId}`, associatedEvent?.startDate)}
                                    style={{
                                      marginTop: '12px',
                                      backgroundColor: '#ff3b30',
                                      color: 'white',
                                      border: 'none',
                                      padding: '8px 12px',
                                      borderRadius: '6px',
                                      cursor: 'pointer',
                                      fontSize: '12px',
                                      fontWeight: 'bold',
                                      width: '100%',
                                      transition: 'all 0.2s ease-in-out'
                                    }}
                                  >
                                    🚫 Bileti İptal Et
                                  </button>
                                )}
                              </div>
                            </div>
                          );
                        })}
                      </div>
                    </div>
                  )}
                </section>

                {/* Etkinlik Listesi Bölümü */}
                <section className="dashboard-section events-section">
                  <div className="section-title-wrapper">
                    <h2>📅 Güncel Etkinlikler</h2>
                    <p className="section-subtitle">Keşfetmeye hazır olun! Katılmak istediğiniz etkinliği seçip anında kaydolun.</p>
                  </div>

                  {/* Filtre Çubuğu */}
                  <div style={{
                    display: 'flex', flexWrap: 'wrap', gap: '10px', marginBottom: '20px',
                    padding: '15px', backgroundColor: '#f5f5f7', borderRadius: '10px'
                  }}>
                    <input
                      type="text"
                      placeholder="🔍 Etkinlik ara..."
                      value={searchKeyword}
                      onChange={e => setSearchKeyword(e.target.value)}
                      style={{ flex: '1 1 200px', padding: '8px 12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' }}
                    />
                    <select
                      value={filterCategory}
                      onChange={e => setFilterCategory(e.target.value)}
                      style={{ flex: '1 1 150px', padding: '8px 12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' }}
                    >
                      <option value="">📁 Tüm Kategoriler</option>
                      {categories.map(cat => (
                        <option key={cat.id} value={cat.id}>{cat.name}</option>
                      ))}
                    </select>
                    <input
                      type="text"
                      placeholder="📍 Şehir filtrele..."
                      value={filterCity}
                      onChange={e => setFilterCity(e.target.value)}
                      style={{ flex: '1 1 130px', padding: '8px 12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' }}
                    />
                    <input
                      type="number"
                      placeholder="💰 Maks. Fiyat"
                      value={filterMaxPrice}
                      onChange={e => setFilterMaxPrice(e.target.value)}
                      min="0"
                      style={{ flex: '0 1 130px', padding: '8px 12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' }}
                    />
                    <label style={{ display: 'flex', alignItems: 'center', gap: '6px', padding: '8px 12px', backgroundColor: filterFree ? '#e6f4ea' : 'white', borderRadius: '6px', border: '1px solid #ddd', cursor: 'pointer', fontSize: '14px', fontWeight: 'bold' }}>
                      <input type="checkbox" checked={filterFree} onChange={e => setFilterFree(e.target.checked)} />
                      🆓 Sadece Ücretsiz
                    </label>
                    {(searchKeyword || filterCategory || filterCity || filterFree || filterMaxPrice) && (
                      <button
                        onClick={() => { setSearchKeyword(''); setFilterCategory(''); setFilterCity(''); setFilterFree(false); setFilterMaxPrice(''); }}
                        style={{ padding: '8px 14px', backgroundColor: '#ff3b30', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', fontSize: '13px' }}
                      >
                        ✕ Filtreleri Temizle
                      </button>
                    )}
                  </div>

                  {loading ? (
                    <div className="loader-container"><div className="spinner"></div><p>Etkinlikler yükleniyor...</p></div>
                  ) : approvedEvents.length === 0 ? (
                    <div className="empty-state">
                      <span className="empty-icon">🏖️</span>
                      <h3>Henüz Onaylanmış Etkinlik Yok</h3>
                      <p>Lütfen daha sonra tekrar kontrol edin veya Organizatör panelinden yeni bir etkinlik oluşturup onaylatın.</p>
                    </div>
                  ) : filteredEvents.length === 0 ? (
                    <div className="empty-state">
                      <span className="empty-icon">🔍</span>
                      <h3>Arama Kriterlerine Uygun Etkinlik Bulunamadı</h3>
                      <p>Farklı filtreler deneyebilir veya filtreleri temizleyebilirsiniz.</p>
                    </div>
                  ) : (
                    <div className="events-grid">
                      {filteredEvents.map((event) => (
                        <div key={event.id} className="event-premium-card" onClick={() => setSelectedEvent(event)}>
                          <div className="card-tag">{getCategoryName(event.categoryId)}</div>
                          <div className="card-body">
                            <h3>{event.title}</h3>
                            <p className="desc-text">{event.description || 'Açıklama belirtilmemiş.'}</p>

                            <div className="card-meta">
                              <div className="meta-item">
                                <span className="meta-icon">📍</span>
                                <span>{event.location} {event.city ? `, ${event.city}` : ''}</span>
                              </div>
                              <div className="meta-item">
                                <span className="meta-icon">📅</span>
                                <span>{new Date(event.startDate).toLocaleString('tr-TR')}</span>
                              </div>
                              <div className="meta-item">
                                <span className="meta-icon">👥</span>
                                <span>Kapasite: {event.capacity} kişi (Dolu: {event.participantCount || 0})</span>
                              </div>
                            </div>
                          </div>
                          <div className="card-footer">
                            <span className="price-tag">{event.isFree ? 'ÜCRETSİZ' : `${event.price} TL`}</span>
                            <button className="card-action-btn">İncele & Kaydol →</button>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </section>
              </div>
            ) : (
              /* Detay Sayfası */
              <div className="event-detail-view card animate-slide">
                <button onClick={handleBackToEvents} className="btn-back">← Etkinliklere Geri Dön</button>

                <div className="detail-header">
                  <span className="detail-category">{getCategoryName(selectedEvent.categoryId)}</span>
                  <h2>{selectedEvent.title}</h2>
                  <p className="detail-price-badge">{selectedEvent.isFree ? 'ÜCRETSİZ ETKİNLİK' : `Giriş Ücreti: ${selectedEvent.price} TL`}</p>
                </div>

                <div className="detail-layout">
                  <div className="detail-info-block">
                    <h3>Etkinlik Detayları</h3>
                    <p className="detail-desc">{selectedEvent.description || 'Bu etkinlik için henüz bir detaylı açıklama girilmedi.'}</p>

                    <div className="detail-meta-list">
                      <div className="detail-meta-item">
                        <span className="meta-icon">📍</span>
                        <div>
                          <strong>Yer & Konum</strong>
                          <p>{selectedEvent.location} {selectedEvent.city ? `, ${selectedEvent.city}` : ''}</p>
                        </div>
                      </div>
                      <div className="detail-meta-item">
                        <span className="meta-icon">📅</span>
                        <div>
                          <strong>Başlangıç Tarihi</strong>
                          <p>{new Date(selectedEvent.startDate).toLocaleString('tr-TR')}</p>
                        </div>
                      </div>
                      {selectedEvent.endDate && (
                        <div className="detail-meta-item">
                          <span className="meta-icon">⏱️</span>
                          <div>
                            <strong>Bitiş Tarihi</strong>
                            <p>{new Date(selectedEvent.endDate).toLocaleString('tr-TR')}</p>
                          </div>
                        </div>
                      )}
                      <div className="detail-meta-item">
                        <span className="meta-icon">👥</span>
                        <div>
                          <strong>Kontenjan & Katılım</strong>
                          <p>{selectedEvent.capacity} Maksimum Katılımcı ({selectedEvent.participantCount || 0} kişi kayıtlı)</p>
                        </div>
                      </div>
                      {selectedEvent.organizerName && (
                        <div className="detail-meta-item">
                          <span className="meta-icon">💼</span>
                          <div>
                            <strong>Düzenleyen Organizatör</strong>
                            <p>{selectedEvent.organizerName}</p>
                          </div>
                        </div>
                      )}
                    </div>

                    {/* Değerlendirme Sistemi */}
                    <div className="detail-reviews-section">
                      <h3>💬 Katılımcı Değerlendirmeleri ({reviews.length})</h3>
                      {reviews.length === 0 ? (
                        <p className="no-reviews-msg">Bu etkinlik için henüz yorum yapılmamış. İlk yorumu siz yapın!</p>
                      ) : (
                        <div className="reviews-list">
                          {reviews.map((r) => (
                            <div key={r.id} className="review-item" style={{ padding: '15px', borderBottom: '1px solid #f0f0f0' }}>
                              <div className="review-meta" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <strong>{r.reviewerName}</strong>
                                <span className="review-stars">{'★'.repeat(r.rating)}{'☆'.repeat(5 - r.rating)}</span>
                              </div>
                              <p className="review-comment" style={{ margin: '8px 0' }}>{r.comment}</p>

                              <div style={{ display: 'flex', gap: '15px', alignItems: 'center', marginTop: '10px' }}>
                                <button
                                  onClick={() => handleMarkReviewHelpful(r.id)}
                                  style={{
                                    border: 'none', background: '#e1f5fe', color: '#0288d1',
                                    fontSize: '12px', padding: '4px 8px', borderRadius: '4px', cursor: 'pointer',
                                    fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '4px'
                                  }}
                                >
                                  👍 Faydalı ({r.helpfulCount || 0})
                                </button>

                                {currentUser && (currentUser.userRole === 'ADMIN' || `${currentUser.firstName || ''} ${currentUser.lastName || ''}`.trim() === r.reviewerName || currentUser.username === r.reviewerName) && (
                                  <button
                                    onClick={() => handleDeleteReview(r.id)}
                                    style={{
                                      border: 'none', background: '#ffebee', color: '#c62828',
                                      fontSize: '12px', padding: '4px 8px', borderRadius: '4px', cursor: 'pointer',
                                      fontWeight: 'bold'
                                    }}
                                  >
                                    🗑️ Sil
                                  </button>
                                )}
                              </div>
                            </div>
                          ))}
                        </div>
                      )}

                      <form onSubmit={handleAddReview} className="add-review-form">
                        <h4>Etkinliği Değerlendir</h4>
                        {reviewMessage && <p className="review-info">{reviewMessage}</p>}
                        <div className="form-group-row">
                          <input
                            type="text"
                            placeholder="Adınız Soyadınız"
                            value={newReview.reviewerName}
                            onChange={(e) => setNewReview({ ...newReview, reviewerName: e.target.value })}
                            required
                          />
                          <select
                            value={newReview.rating}
                            onChange={(e) => setNewReview({ ...newReview, rating: Number(e.target.value) })}
                          >
                            <option value="5">⭐⭐⭐⭐⭐ (5/5)</option>
                            <option value="4">⭐⭐⭐⭐ (4/5)</option>
                            <option value="3">⭐⭐⭐ (3/5)</option>
                            <option value="2">⭐⭐ (2/5)</option>
                            <option value="1">⭐ (1/5)</option>
                          </select>
                        </div>
                        <textarea
                          placeholder="Etkinlik hakkındaki düşünceleriniz..."
                          value={newReview.comment}
                          onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                          required
                        />
                        <button type="submit">Yorumu Gönder</button>
                      </form>
                    </div>
                  </div>

                  {/* Kaydolma Formu */}
                  <div className="detail-register-block">
                    <div className="register-sticky-card">
                      <h3>🎟️ Etkinliğe Kaydol</h3>
                      <p className="register-intro">Aşağıdaki formu doldurarak etkinlik katılımınızı onaylayın.</p>

                      {regMessage && <div className="register-status-msg">{regMessage}</div>}

                      <form onSubmit={handleRegister} className="register-form">
                        <div className="form-field">
                          <label>Adınız *</label>
                          <input
                            type="text"
                            placeholder="Örn. Ahmet"
                            value={regForm.firstName}
                            onChange={(e) => setRegForm({ ...regForm, firstName: e.target.value })}
                            required
                          />
                        </div>
                        <div className="form-field">
                          <label>Soyadınız *</label>
                          <input
                            type="text"
                            placeholder="Örn. Yılmaz"
                            value={regForm.lastName}
                            onChange={(e) => setRegForm({ ...regForm, lastName: e.target.value })}
                            required
                          />
                        </div>
                        <div className="form-field">
                          <label>E-Posta Adresiniz *</label>
                          <input
                            type="email"
                            placeholder="ahmet@example.com"
                            value={regForm.email}
                            onChange={(e) => setRegForm({ ...regForm, email: e.target.value })}
                            required
                          />
                        </div>
                        <div className="form-field">
                          <label>Telefon Numarası</label>
                          <input
                            type="text"
                            placeholder="0555 555 5555"
                            value={regForm.phoneNumber}
                            onChange={(e) => setRegForm({ ...regForm, phoneNumber: e.target.value })}
                          />
                        </div>

                        {/* Kupon Kodu Bölümü (Sadece ücretli etkinlikler için) */}
                        {!selectedEvent.isFree && selectedEvent.price > 0 && (
                          <div className="promo-coupon-section" style={{ borderTop: '1px dashed #ddd', paddingTop: '15px', marginTop: '15px', marginBottom: '15px' }}>
                            <label style={{ fontSize: '13px', fontWeight: 'bold', display: 'block', marginBottom: '5px' }}>🎟️ Promosyon / Kupon Kodu</label>
                            <div style={{ display: 'flex', gap: '8px' }}>
                              <input
                                type="text"
                                placeholder="Örn: BAHAR20"
                                value={couponCodeInput}
                                onChange={(e) => setCouponCodeInput(e.target.value)}
                                style={{ flex: 1, padding: '6px', fontSize: '13px', textTransform: 'uppercase' }}
                              />
                              <button
                                type="button"
                                onClick={handleApplyCoupon}
                                style={{ padding: '6px 12px', fontSize: '13px', backgroundColor: '#5856d6', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
                              >
                                Uygula
                              </button>
                            </div>
                            {couponMsg && (
                              <p style={{ fontSize: '12px', marginTop: '5px', color: appliedPromo ? '#28a745' : '#dc3545', fontWeight: 'bold' }}>
                                {couponMsg}
                              </p>
                            )}
                            {appliedPromo && (
                              <div style={{ backgroundColor: '#eefcf2', border: '1px solid #28a745', padding: '10px', borderRadius: '4px', marginTop: '8px' }}>
                                <p style={{ fontSize: '13px', margin: 0, color: '#155724' }}>
                                  <strong>Kupon uygulandı!</strong> %{appliedPromo.discountPercentage} İndirim.
                                </p>
                                <p style={{ fontSize: '14px', margin: '5px 0 0 0', fontWeight: 'bold', color: '#155724' }}>
                                  Ödenecek Tutar: <span style={{ textDecoration: 'line-through', color: '#888', fontSize: '13px' }}>{selectedEvent.price} TL</span> {(selectedEvent.price * (1 - appliedPromo.discountPercentage / 100)).toFixed(2)} TL
                                </p>
                              </div>
                            )}
                          </div>
                        )}

                        <button type="submit" className="btn-register-submit" disabled={regLoading}>
                          {regLoading ? 'Kayıt Yapılıyor...' : '🎟️ Katılımımı Onayla'}
                        </button>
                      </form>
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* ========================================================
            2. ORGANİZATÖR PORTALI
           ======================================================== */}
        {activePortal === 'organizer' && (
          <div className="portal-container animate-fade">
            {(!currentUser || (currentUser.userRole !== 'ORGANIZER' && currentUser.userRole !== 'ADMIN')) ? (
              /* Organizatör Giriş Paneli */
              <div className="organizer-login-overlay card">
                <div className="portal-alert-info">
                  <h3>💼 Organizatör Hesabı Gerekli</h3>
                  <p>Etkinlik eklemek ve onay durumlarını yönetmek için organizatör hesabınızla oturum açın ya da yeni bir kayıt oluşturun.</p>
                </div>
                <Login defaultRole="ORGANIZER" onLoginSuccess={handleLoginSuccess} />
              </div>
            ) : (
              /* Organizatör Gerçek İçeriği */
              <div className="organizer-portal-view" style={{ width: '100%' }}>
                {/* Sub tabs for Organizer */}
                <div className="portal-sub-tabs" style={{ display: 'flex', gap: '15px', marginBottom: '20px', borderBottom: '2px solid #e5e5ea', paddingBottom: '10px' }}>
                  <button
                    onClick={() => setOrganizerTab('events')}
                    className={`sub-tab-btn ${organizerTab === 'events' ? 'active' : ''}`}
                    style={{
                      background: 'none', border: 'none', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer',
                      padding: '5px 10px', color: organizerTab === 'events' ? '#007aff' : '#8e8e93',
                      borderBottom: organizerTab === 'events' ? '3px solid #007aff' : 'none'
                    }}
                  >
                    📅 Etkinlik Ekle & Yönet
                  </button>
                  <button
                    onClick={() => setOrganizerTab('promotions')}
                    className={`sub-tab-btn ${organizerTab === 'promotions' ? 'active' : ''}`}
                    style={{
                      background: 'none', border: 'none', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer',
                      padding: '5px 10px', color: organizerTab === 'promotions' ? '#007aff' : '#8e8e93',
                      borderBottom: organizerTab === 'promotions' ? '3px solid #007aff' : 'none'
                    }}
                  >
                    🎟️ Promosyon & Kupon Kodları
                  </button>
                </div>

                {organizerTab === 'events' ? (
                  <div className="organizer-layout animate-slide" style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    {(() => {
                      const myEvents = events.filter(e => e.organizerId === currentUser.id);
                      const totalCapacity = myEvents.reduce((sum, e) => sum + (e.capacity || 0), 0);
                      const totalParticipants = myEvents.reduce((sum, e) => sum + (e.participantCount || 0), 0);
                      const totalRevenue = myEvents.reduce((sum, e) => sum + ((e.participantCount || 0) * (e.price || 0)), 0);

                      return (
                        <>
                          {/* Dashboard İstatistikleri */}
                          <div className="dashboard-stats" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px' }}>
                            <div className="stat-card" style={{ backgroundColor: 'white', padding: '20px', borderRadius: '12px', boxShadow: '0 4px 12px rgba(0,0,0,0.05)', borderLeft: '4px solid #007aff' }}>
                              <h3 style={{ margin: '0 0 10px 0', fontSize: '14px', color: '#666' }}>📌 Toplam Etkinlik</h3>
                              <p style={{ margin: 0, fontSize: '28px', fontWeight: 'bold', color: '#333' }}>{myEvents.length}</p>
                            </div>
                            <div className="stat-card" style={{ backgroundColor: 'white', padding: '20px', borderRadius: '12px', boxShadow: '0 4px 12px rgba(0,0,0,0.05)', borderLeft: '4px solid #34c759' }}>
                              <h3 style={{ margin: '0 0 10px 0', fontSize: '14px', color: '#666' }}>👥 Toplam Katılımcı</h3>
                              <p style={{ margin: 0, fontSize: '28px', fontWeight: 'bold', color: '#333' }}>{totalParticipants} <span style={{ fontSize: '14px', color: '#888' }}>/ {totalCapacity}</span></p>
                            </div>
                            <div className="stat-card" style={{ backgroundColor: 'white', padding: '20px', borderRadius: '12px', boxShadow: '0 4px 12px rgba(0,0,0,0.05)', borderLeft: '4px solid #ff9500' }}>
                              <h3 style={{ margin: '0 0 10px 0', fontSize: '14px', color: '#666' }}>💰 Tahmini Gelir</h3>
                              <p style={{ margin: 0, fontSize: '28px', fontWeight: 'bold', color: '#333' }}>{totalRevenue.toLocaleString('tr-TR')} ₺</p>
                            </div>
                          </div>

                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px' }}>
                            <h2 style={{ margin: 0 }}>📋 Etkinliklerim</h2>
                            <button
                              onClick={() => setShowEventModal(true)}
                              style={{ padding: '10px 20px', backgroundColor: '#007aff', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '8px', boxShadow: '0 4px 10px rgba(0,122,255,0.3)' }}
                            >
                              <span>➕</span> Yeni Etkinlik Oluştur
                            </button>
                          </div>

                          {myEvents.length === 0 ? (
                            <div className="empty-state" style={{ backgroundColor: 'white', padding: '40px', borderRadius: '12px', textAlign: 'center', boxShadow: '0 4px 12px rgba(0,0,0,0.05)' }}>
                              <span style={{ fontSize: '40px' }}>🏖️</span>
                              <h3 style={{ marginTop: '15px' }}>Henüz etkinlik oluşturmadınız</h3>
                              <p style={{ color: '#666' }}>Sağ üstteki butonu kullanarak ilk etkinliğinizi planlayın.</p>
                            </div>
                          ) : (
                            <div className="organizer-events-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '20px' }}>
                              {myEvents.map((e) => {
                                const fillPercentage = e.capacity > 0 ? Math.min(100, ((e.participantCount || 0) / e.capacity) * 100) : 0;
                                const revenue = (e.participantCount || 0) * (e.price || 0);

                                return (
                                  <div key={e.id} className="org-event-card" style={{ backgroundColor: 'white', borderRadius: '12px', overflow: 'hidden', boxShadow: '0 4px 15px rgba(0,0,0,0.05)', display: 'flex', flexDirection: 'column' }}>
                                    <div style={{ padding: '15px', borderBottom: '1px solid #f0f0f0' }}>
                                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '10px' }}>
                                        <h4 style={{ margin: 0, fontSize: '18px', color: '#333', flex: 1, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{e.title}</h4>
                                        <div style={{ marginLeft: '10px' }}>
                                          {e.approvalStatus === 'APPROVED' ? (
                                            <span style={{ padding: '4px 8px', backgroundColor: '#e6f4ea', color: '#137333', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold' }}>✓ ONAYLI</span>
                                          ) : e.approvalStatus === 'REJECTED' ? (
                                            <span style={{ padding: '4px 8px', backgroundColor: '#fce8e6', color: '#c5221f', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold' }}>✗ RED</span>
                                          ) : (
                                            <span style={{ padding: '4px 8px', backgroundColor: '#fef7e0', color: '#b06000', borderRadius: '4px', fontSize: '11px', fontWeight: 'bold' }}>⏱️ BEKLİYOR</span>
                                          )}
                                        </div>
                                      </div>
                                      <div style={{ fontSize: '13px', color: '#666', display: 'flex', alignItems: 'center', gap: '5px', marginBottom: '5px' }}>
                                        <span>📅</span> {new Date(e.startDate).toLocaleString('tr-TR', { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })}
                                      </div>
                                      <div style={{ fontSize: '13px', color: '#666', display: 'flex', alignItems: 'center', gap: '5px' }}>
                                        <span>📍</span> {e.location} {e.city ? `(${e.city})` : ''}
                                      </div>
                                    </div>

                                    <div style={{ padding: '15px', backgroundColor: '#fafbfc', flex: 1 }}>
                                      <div style={{ marginBottom: '12px' }}>
                                        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', marginBottom: '4px', fontWeight: 'bold', color: '#555' }}>
                                          <span>Doluluk: {e.participantCount || 0} / {e.capacity}</span>
                                          <span>{fillPercentage.toFixed(0)}%</span>
                                        </div>
                                        <div style={{ height: '8px', backgroundColor: '#e9ecef', borderRadius: '4px', overflow: 'hidden' }}>
                                          <div style={{ height: '100%', width: `${fillPercentage}%`, backgroundColor: fillPercentage >= 100 ? '#ff3b30' : '#34c759', transition: 'width 0.3s ease' }}></div>
                                        </div>
                                      </div>

                                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '13px', marginTop: '10px', paddingTop: '10px', borderTop: '1px dashed #ddd' }}>
                                        <span style={{ color: '#666' }}>Bilet: <strong>{e.isFree ? 'Ücretsiz' : `${e.price} ₺`}</strong></span>
                                        <span style={{ color: '#666' }}>Gelir: <strong style={{ color: '#007aff' }}>{revenue.toLocaleString('tr-TR')} ₺</strong></span>
                                      </div>
                                      {e.approvalStatus === 'REJECTED' && e.rejectionReason && (
                                        <div style={{ marginTop: '10px', padding: '8px', backgroundColor: '#fce8e6', borderRadius: '4px', fontSize: '12px', color: '#c5221f' }}>
                                          <strong>Red Nedeni:</strong> {e.rejectionReason}
                                        </div>
                                      )}
                                    </div>

                                    <div style={{ padding: '10px', borderTop: '1px solid #eee', display: 'flex', gap: '10px' }}>
                                      <button
                                        onClick={() => setShowParticipantsModalFor(e.id)}
                                        style={{ flex: 1, padding: '8px', backgroundColor: '#f0f0f5', color: '#333', border: '1px solid #ddd', borderRadius: '6px', cursor: 'pointer', fontSize: '13px', fontWeight: 'bold' }}
                                      >
                                        👥 Katılımcılar
                                      </button>
                                      <button
                                        style={{ flex: 1, padding: '8px', backgroundColor: '#f0f0f5', color: '#333', border: '1px solid #ddd', borderRadius: '6px', cursor: 'pointer', fontSize: '13px', fontWeight: 'bold' }}
                                      >
                                        ✏️ Düzenle
                                      </button>
                                    </div>
                                  </div>
                                );
                              })}
                            </div>
                          )}

                          {/* Etkinlik Oluşturma Modalı */}
                          {showEventModal && (
                            <div className="modal-overlay" style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.6)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
                              <div className="modal-content animate-slide" style={{ backgroundColor: 'white', borderRadius: '12px', width: '90%', maxWidth: '800px', maxHeight: '90vh', overflowY: 'auto', padding: '20px', position: 'relative' }}>
                                <button
                                  onClick={() => setShowEventModal(false)}
                                  style={{ position: 'absolute', top: '15px', right: '15px', background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer', color: '#888' }}
                                >
                                  ✖
                                </button>
                                <h2 style={{ marginTop: 0, marginBottom: '20px', borderBottom: '1px solid #eee', paddingBottom: '10px' }}>🎉 Yeni Etkinlik Oluştur</h2>
                                <EventForm
                                  organizerId={currentUser.id}
                                  onCreated={(newEvent) => {
                                    handleEventCreated(newEvent);
                                    setShowEventModal(false);
                                  }}
                                />
                              </div>
                            </div>
                          )}

                          {/* Katılımcı Yönetim Modalı */}
                          {showParticipantsModalFor && (
                            <div className="modal-overlay" style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.6)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
                              <div className="modal-content animate-slide" style={{ backgroundColor: 'white', borderRadius: '12px', width: '90%', maxWidth: '600px', maxHeight: '80vh', overflowY: 'auto', padding: '20px', position: 'relative' }}>
                                <button
                                  onClick={() => setShowParticipantsModalFor(null)}
                                  style={{ position: 'absolute', top: '15px', right: '15px', background: 'none', border: 'none', fontSize: '20px', cursor: 'pointer', color: '#888', zIndex: 10 }}
                                >
                                  ✖
                                </button>
                                <ParticipantList
                                  eventId={showParticipantsModalFor}
                                  onUpdate={() => setRefreshTrigger(prev => prev + 1)}
                                />
                              </div>
                            </div>
                          )}
                        </>
                      );
                    })()}
                  </div>
                ) : (
                  /* Promosyon & Kupon Kodları Yönetimi */
                  <div className="organizer-layout animate-slide" style={{ display: 'grid', gridTemplateColumns: '1fr 1.5fr', gap: '20px' }}>
                    <div className="organizer-form-card card" style={{ padding: '20px' }}>
                      <h3>🎟️ Yeni İndirim Kuponu Tanımla</h3>
                      <p style={{ color: '#888', fontSize: '13px', marginBottom: '15px' }}>Etkinlikleriniz için indirim kodları (kampanyalar) oluşturarak satışlarınızı artırın.</p>
                      {promoMessage && (
                        <div style={{ padding: '10px', backgroundColor: promoMessage.startsWith('✓') ? '#e6f4ea' : '#fce8e6', color: promoMessage.startsWith('✓') ? '#137333' : '#c5221f', borderRadius: '4px', marginBottom: '15px', fontWeight: 'bold' }}>
                          {promoMessage}
                        </div>
                      )}
                      <form onSubmit={handleCreatePromo}>
                        <div style={{ marginBottom: '12px' }}>
                          <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '4px', fontSize: '13px' }}>Kupon Başlığı *</label>
                          <input
                            type="text"
                            placeholder="Örn: Bahar Kampanyası"
                            value={promoForm.title}
                            onChange={(e) => setPromoForm({ ...promoForm, title: e.target.value })}
                            required
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                          />
                        </div>
                        <div style={{ marginBottom: '12px' }}>
                          <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '4px', fontSize: '13px' }}>Açıklama</label>
                          <input
                            type="text"
                            placeholder="Örn: Tüm müzik etkinliklerinde %20 indirim"
                            value={promoForm.description}
                            onChange={(e) => setPromoForm({ ...promoForm, description: e.target.value })}
                            style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                          />
                        </div>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '12px' }}>
                          <div>
                            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '4px', fontSize: '13px' }}>Kupon Kodu *</label>
                            <input
                              type="text"
                              placeholder="Örn: BAHAR20"
                              value={promoForm.campaignCode}
                              onChange={(e) => setPromoForm({ ...promoForm, campaignCode: e.target.value.toUpperCase() })}
                              required
                              style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc', textTransform: 'uppercase' }}
                            />
                          </div>
                          <div>
                            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '4px', fontSize: '13px' }}>İndirim Yüzdesi (%) *</label>
                            <input
                              type="number"
                              min="1"
                              max="100"
                              value={promoForm.discountPercentage}
                              onChange={(e) => setPromoForm({ ...promoForm, discountPercentage: e.target.value })}
                              required
                              style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                            />
                          </div>
                        </div>
                        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '15px' }}>
                          <div>
                            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '4px', fontSize: '13px' }}>Geçerli Etkinlik</label>
                            <select
                              value={promoForm.eventId}
                              onChange={(e) => setPromoForm({ ...promoForm, eventId: e.target.value })}
                              style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                            >
                              <option value="">Tüm Etkinliklerimde Geçerli</option>
                              {events.filter(e => e.organizerId === currentUser.id).map(e => (
                                <option key={e.id} value={e.id}>{e.title}</option>
                              ))}
                            </select>
                          </div>
                          <div>
                            <label style={{ display: 'block', fontWeight: 'bold', marginBottom: '4px', fontSize: '13px' }}>Kullanım Limiti</label>
                            <input
                              type="number"
                              min="1"
                              value={promoForm.maxParticipants}
                              onChange={(e) => setPromoForm({ ...promoForm, maxParticipants: e.target.value })}
                              style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                            />
                          </div>
                        </div>
                        <button type="submit" style={{ width: '100%', padding: '10px', backgroundColor: '#34c759', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
                          ✓ Kuponu Oluştur
                        </button>
                      </form>
                    </div>

                    <div className="organizer-events-card card" style={{ padding: '20px' }}>
                      <h3>📋 Oluşturduğum Promosyonlar & Kuponlar ({promotions.length})</h3>
                      <p style={{ color: '#888', fontSize: '13px', marginBottom: '15px' }}>Oluşturduğunuz aktif kupon kodlarının kullanım oranını izleyin.</p>
                      {promotions.length === 0 ? (
                        <p style={{ color: '#666', fontStyle: 'italic' }}>Henüz tanımlanmış bir promosyon kodunuz bulunmamaktadır.</p>
                      ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                          {promotions.map(promo => (
                            <div key={promo.id} style={{ border: '1px solid #e5e5ea', padding: '15px', borderRadius: '6px', backgroundColor: '#fafafa' }}>
                              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                                <h4 style={{ margin: 0 }}>{promo.title}</h4>
                                <span style={{ padding: '3px 8px', backgroundColor: '#5856d6', color: 'white', fontWeight: 'bold', borderRadius: '4px', fontSize: '12px' }}>
                                  {promo.campaignCode}
                                </span>
                              </div>
                              <p style={{ margin: '0 0 8px 0', fontSize: '13px', color: '#555' }}>{promo.description || 'Açıklama girilmemiş.'}</p>
                              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '15px', fontSize: '12px', color: '#888', alignItems: 'center' }}>
                                <span><strong>🎯 İndirim Oranı:</strong> %{promo.discountPercentage}</span>
                                <span><strong>📊 Kullanım:</strong> {promo.usedCount || 0} / {promo.maxParticipants || 'Sınırsız'}</span>
                                <span><strong>📅 Geçerli Etkinlik:</strong> {promo.eventTitle || 'Tüm Etkinlikler'}</span>
                                <span style={{ color: promo.isActive ? '#34c759' : '#ff3b30', fontWeight: 'bold' }}>
                                  {promo.isActive ? '● Aktif' : '○ Pasif'}
                                </span>
                                {promo.isActive && (
                                  <button
                                    onClick={() => handleDeactivatePromo(promo.id)}
                                    style={{
                                      border: 'none',
                                      background: '#ffebee',
                                      color: '#c62828',
                                      fontSize: '11px',
                                      padding: '4px 8px',
                                      borderRadius: '4px',
                                      cursor: 'pointer',
                                      fontWeight: 'bold',
                                      marginLeft: 'auto'
                                    }}
                                  >
                                    🛑 Kapat (Pasif Et)
                                  </button>
                                )}
                              </div>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        )}

        {/* ========================================================
            3. YÖNETİCİ PORTALI
           ======================================================== */}
        {activePortal === 'admin' && (
          <div className="portal-container animate-fade">
            {(!currentUser || currentUser.userRole !== 'ADMIN') ? (
              /* Yönetici Girişi Ekranı */
              <div className="admin-login-overlay card">
                <div className="portal-alert-info">
                  <h3>🛡️ Yönetici Yetkilendirmesi Gerekli</h3>
                  <p>Bu alana yalnızca sistem yöneticileri erişebilir.</p>
                </div>
                <Login defaultRole="ADMIN" onLoginSuccess={handleLoginSuccess} />
              </div>
            ) : (
              /* Yönetici İçerikleri */
              <>
                <div className="admin-header-actions card">
                  <div className="admin-welcome-info">
                    <h2>🛡️ Yönetici Kontrol Paneli</h2>
                    <p>Sisteme başarıyla giriş yaptınız. Bekleyen talepleri onaylayabilir/reddedebilirsiniz.</p>
                  </div>
                  <button onClick={handleLogout} className="btn-logout">
                    🚪 Güvenli Çıkış Yap
                  </button>
                </div>

                {/* İstatistik Göstergeleri */}
                <section className="stats-dashboard-grid">
                  <div className="stat-card">
                    <span className="stat-icon">📊</span>
                    <div className="stat-info">
                      <h3>{stats.totalEvents}</h3>
                      <p>Toplam Etkinlik</p>
                    </div>
                  </div>
                  <div className="stat-card">
                    <span className="stat-icon text-approved">✓</span>
                    <div className="stat-info">
                      <h3>{stats.approvedEvents}</h3>
                      <p>Onaylananlar</p>
                    </div>
                  </div>
                  <div className="stat-card">
                    <span className="stat-icon text-pending">⏱️</span>
                    <div className="stat-info">
                      <h3>{stats.pendingEvents}</h3>
                      <p>Onay Bekleyenler</p>
                    </div>
                  </div>
                  <div className="stat-card">
                    <span className="stat-icon text-rejected">✗</span>
                    <div className="stat-info">
                      <h3>{stats.rejectedEvents}</h3>
                      <p>Reddedilenler</p>
                    </div>
                  </div>
                </section>

                {/* Admin Sub tabs */}
                <div className="portal-sub-tabs" style={{ display: 'flex', gap: '15px', marginBottom: '20px', borderBottom: '2px solid #e5e5ea', paddingBottom: '10px', marginTop: '20px' }}>
                  <button
                    onClick={() => setAdminTab('approvals')}
                    className={`sub-tab-btn ${adminTab === 'approvals' ? 'active' : ''}`}
                    style={{
                      background: 'none', border: 'none', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer',
                      padding: '5px 10px', color: adminTab === 'approvals' ? '#007aff' : '#8e8e93',
                      borderBottom: adminTab === 'approvals' ? '3px solid #007aff' : 'none'
                    }}
                  >
                    📋 Bekleyen Onaylar
                  </button>
                  <button
                    onClick={() => setAdminTab('users')}
                    className={`sub-tab-btn ${adminTab === 'users' ? 'active' : ''}`}
                    style={{
                      background: 'none', border: 'none', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer',
                      padding: '5px 10px', color: adminTab === 'users' ? '#007aff' : '#8e8e93',
                      borderBottom: adminTab === 'users' ? '3px solid #007aff' : 'none'
                    }}
                  >
                    👤 Kullanıcı Rolleri & Yönetim
                  </button>
                  <button
                    onClick={() => setAdminTab('events')}
                    className={`sub-tab-btn ${adminTab === 'events' ? 'active' : ''}`}
                    style={{
                      background: 'none', border: 'none', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer',
                      padding: '5px 10px', color: adminTab === 'events' ? '#007aff' : '#8e8e93',
                      borderBottom: adminTab === 'events' ? '3px solid #007aff' : 'none'
                    }}
                  >
                    📅 Tüm Etkinlikler & Yönetim
                  </button>
                  <button
                    onClick={() => { setAdminTab('categories'); fetchCategories(); }}
                    className={`sub-tab-btn ${adminTab === 'categories' ? 'active' : ''}`}
                    style={{
                      background: 'none', border: 'none', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer',
                      padding: '5px 10px', color: adminTab === 'categories' ? '#007aff' : '#8e8e93',
                      borderBottom: adminTab === 'categories' ? '3px solid #007aff' : 'none'
                    }}
                  >
                    📁 Kategori Yönetimi
                  </button>
                </div>

                {adminTab === 'approvals' && (
                  /* Admin Approval Component */
                  <div className="card animate-slide">
                    <AdminApprovalPanel onApprovalChanged={triggerRefresh} />
                  </div>
                )}

                {adminTab === 'users' && (
                  /* Kullanıcı Rolleri & Yönetim */
                  <div className="card animate-slide" style={{ padding: '20px' }}>
                    <h3>👤 Sistem Kullanıcıları & Rol Atamaları</h3>
                    <p style={{ color: '#888', fontSize: '13px', marginBottom: '15px' }}>Sistemdeki kayıtlı tüm kullanıcıları listeleyin ve yetki seviyelerini (Rollerini) anında güncelleyin.</p>

                    {userRoleMessage && (
                      <div style={{ padding: '10px', backgroundColor: userRoleMessage.startsWith('✓') ? '#e6f4ea' : '#fce8e6', color: userRoleMessage.startsWith('✓') ? '#137333' : '#c5221f', borderRadius: '4px', marginBottom: '15px', fontWeight: 'bold' }}>
                        {userRoleMessage}
                      </div>
                    )}

                    <div style={{ overflowX: 'auto' }}>
                      <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
                        <thead>
                          <tr style={{ borderBottom: '2px solid #e5e5ea', textAlign: 'left', backgroundColor: '#f5f5f7' }}>
                            <th style={{ padding: '12px' }}>ID</th>
                            <th style={{ padding: '12px' }}>Kullanıcı Adı</th>
                            <th style={{ padding: '12px' }}>Ad Soyad</th>
                            <th style={{ padding: '12px' }}>E-Posta</th>
                            <th style={{ padding: '12px' }}>Mevcut Rol</th>
                            <th style={{ padding: '12px' }}>Rolü Güncelle</th>
                            <th style={{ padding: '12px' }}>İşlemler</th>
                          </tr>
                        </thead>
                        <tbody>
                          {usersList.length === 0 ? (
                            <tr>
                              <td colSpan="7" style={{ padding: '20px', textAlign: 'center', color: '#888' }}>Sistemde kayıtlı kullanıcı bulunamadı.</td>
                            </tr>
                          ) : (
                            usersList.map(user => (
                              <tr key={user.id} style={{ borderBottom: '1px solid #e5e5ea' }}>
                                <td style={{ padding: '12px' }}>{user.id}</td>
                                <td style={{ padding: '12px', fontWeight: 'bold' }}>{user.username}</td>
                                <td style={{ padding: '12px' }}>{user.fullName || `${user.firstName || ''} ${user.lastName || ''}`.trim() || 'Belirtilmemiş'}</td>
                                <td style={{ padding: '12px' }}>{user.email}</td>
                                <td style={{ padding: '12px' }}>
                                  <span style={{
                                    padding: '4px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 'bold',
                                    backgroundColor: user.userRole === 'ADMIN' ? '#ffccd5' : user.userRole === 'ORGANIZER' ? '#d1e7dd' : '#e2e3e5',
                                    color: user.userRole === 'ADMIN' ? '#a30000' : user.userRole === 'ORGANIZER' ? '#0f5132' : '#383d41'
                                  }}>
                                    {user.userRole}
                                  </span>
                                </td>
                                <td style={{ padding: '12px' }}>
                                  <select
                                    value={user.userRole}
                                    onChange={(e) => handleChangeUserRole(user.id, e.target.value)}
                                    style={{ padding: '6px', borderRadius: '4px', border: '1px solid #ccc', fontSize: '13px' }}
                                  >
                                    <option value="USER">USER (Katılımcı / Müşteri)</option>
                                    <option value="ORGANIZER">ORGANIZER (Etkinlik Sahibi)</option>
                                    <option value="ADMIN">ADMIN (Sistem Yöneticisi)</option>
                                  </select>
                                </td>
                                <td style={{ padding: '12px' }}>
                                  <button
                                    onClick={() => handleDeleteUser(user.id)}
                                    style={{
                                      backgroundColor: '#ff3b30', color: 'white', border: 'none',
                                      padding: '6px 12px', borderRadius: '4px', cursor: 'pointer',
                                      fontSize: '12px', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '4px'
                                    }}
                                    disabled={user.id === currentUser.id}
                                    title={user.id === currentUser.id ? "Kendi admin hesabınızı silemezsiniz!" : "Kullanıcıyı Sil"}
                                  >
                                    🗑️ Sil
                                  </button>
                                </td>
                              </tr>
                            ))
                          )}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}

                {adminTab === 'events' && (
                  /* Tüm Etkinlikler & Yönetim */
                  <div className="card animate-slide" style={{ padding: '20px' }}>
                    <h3>📅 Tüm Etkinlikler & Yönetim</h3>
                    <p style={{ color: '#888', fontSize: '13px', marginBottom: '15px' }}>Sistemdeki tüm aktif, onaylı, onay bekleyen veya reddedilen etkinlikleri görüntüleyin ve yönetin.</p>

                    {userRoleMessage && (
                      <div style={{ padding: '10px', backgroundColor: userRoleMessage.startsWith('✓') ? '#e6f4ea' : '#fce8e6', color: userRoleMessage.startsWith('✓') ? '#137333' : '#c5221f', borderRadius: '4px', marginBottom: '15px', fontWeight: 'bold' }}>
                        {userRoleMessage}
                      </div>
                    )}

                    <div style={{ overflowX: 'auto' }}>
                      <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '10px' }}>
                        <thead>
                          <tr style={{ borderBottom: '2px solid #e5e5ea', textAlign: 'left', backgroundColor: '#f5f5f7' }}>
                            <th style={{ padding: '12px' }}>ID</th>
                            <th style={{ padding: '12px' }}>Etkinlik Adı</th>
                            <th style={{ padding: '12px' }}>Kategori</th>
                            <th style={{ padding: '12px' }}>Şehir / Konum</th>
                            <th style={{ padding: '12px' }}>Fiyat</th>
                            <th style={{ padding: '12px' }}>Kapasite</th>
                            <th style={{ padding: '12px' }}>Durum</th>
                            <th style={{ padding: '12px' }}>İşlemler</th>
                          </tr>
                        </thead>
                        <tbody>
                          {events.length === 0 ? (
                            <tr>
                              <td colSpan="8" style={{ padding: '20px', textAlign: 'center', color: '#888' }}>Sistemde kayıtlı etkinlik bulunamadı.</td>
                            </tr>
                          ) : (
                            events.map(ev => (
                              <tr key={ev.id} style={{ borderBottom: '1px solid #e5e5ea' }}>
                                <td style={{ padding: '12px' }}>{ev.id}</td>
                                <td style={{ padding: '12px', fontWeight: 'bold' }}>{ev.title}</td>
                                <td style={{ padding: '12px' }}>{getCategoryName(ev.categoryId)}</td>
                                <td style={{ padding: '12px' }}>{ev.location}</td>
                                <td style={{ padding: '12px' }}>{ev.price === 0 ? 'Ücretsiz' : `${ev.price} TL`}</td>
                                <td style={{ padding: '12px' }}>{ev.capacity}</td>
                                <td style={{ padding: '12px' }}>
                                  <span style={{
                                    padding: '4px 8px', borderRadius: '12px', fontSize: '11px', fontWeight: 'bold',
                                    backgroundColor: ev.approvalStatus === 'APPROVED' ? '#d1e7dd' : ev.approvalStatus === 'REJECTED' ? '#fce8e6' : '#fff3cd',
                                    color: ev.approvalStatus === 'APPROVED' ? '#0f5132' : ev.approvalStatus === 'REJECTED' ? '#c5221f' : '#856404'
                                  }}>
                                    {ev.approvalStatus || 'APPROVED'}
                                  </span>
                                </td>
                                <td style={{ padding: '12px' }}>
                                  <button
                                    onClick={() => handleDeleteEvent(ev.id)}
                                    style={{
                                      backgroundColor: '#ff3b30', color: 'white', border: 'none',
                                      padding: '6px 12px', borderRadius: '4px', cursor: 'pointer',
                                      fontSize: '12px', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '4px'
                                    }}
                                  >
                                    🗑️ Sil
                                  </button>
                                </td>
                              </tr>
                            ))
                          )}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}

                {adminTab === 'categories' && (
                  <div className="card animate-slide" style={{ padding: '20px' }}>
                    <h3>📁 Kategori Yönetimi</h3>
                    <p style={{ color: '#888', fontSize: '13px', marginBottom: '15px' }}>
                      Sistemde kayıtlı etkinlik kategorilerini yönetin. Yeni kategoriler ekleyin veya mevcutları silin.
                    </p>

                    {userRoleMessage && (
                      <div style={{ padding: '10px', backgroundColor: userRoleMessage.startsWith('✓') ? '#e6f4ea' : '#fce8e6', color: userRoleMessage.startsWith('✓') ? '#137333' : '#c5221f', borderRadius: '4px', marginBottom: '15px', fontWeight: 'bold' }}>
                        {userRoleMessage}
                      </div>
                    )}

                    <form onSubmit={handleAddCategory} style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
                      <input
                        type="text"
                        placeholder="Yeni kategori adı (Örn: Festival & Eğlence)"
                        value={newCategoryName}
                        onChange={(e) => setNewCategoryName(e.target.value)}
                        required
                        style={{ flex: 1, padding: '10px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '14px' }}
                      />
                      <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#34c759', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', whiteSpace: 'nowrap' }}>
                        ➕ Kategori Ekle
                      </button>
                    </form>

                    {categories.length === 0 ? (
                      <p style={{ color: '#888', fontStyle: 'italic' }}>Henüz kategori bulunmamaktadır.</p>
                    ) : (
                      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                          <tr style={{ borderBottom: '2px solid #e5e5ea', textAlign: 'left', backgroundColor: '#f5f5f7' }}>
                            <th style={{ padding: '12px' }}>ID</th>
                            <th style={{ padding: '12px' }}>Kategori Adı</th>
                            <th style={{ padding: '12px' }}>İşlemler</th>
                          </tr>
                        </thead>
                        <tbody>
                          {categories.map(cat => (
                            <tr key={cat.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
                              <td style={{ padding: '12px', color: '#888', fontSize: '13px' }}>{cat.id}</td>
                              <td style={{ padding: '12px', fontWeight: 'bold' }}>{cat.name}</td>
                              <td style={{ padding: '12px' }}>
                                <button
                                  onClick={() => handleDeleteCategory(cat.id)}
                                  style={{
                                    backgroundColor: '#ff3b30', color: 'white', border: 'none',
                                    padding: '6px 12px', borderRadius: '4px', cursor: 'pointer',
                                    fontSize: '12px', fontWeight: 'bold'
                                  }}
                                >
                                  🗑️ Sil
                                </button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    )}
                  </div>
                )}
              </>
            )}
          </div>
        )}
      </main>
    </div>
  );
}

export default App;

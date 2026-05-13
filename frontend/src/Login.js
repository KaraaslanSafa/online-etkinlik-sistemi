import API_BASE_URL from './config';
import React, { useState } from 'react';
import './App.css';

const Login = ({ defaultRole = 'USER', onLoginSuccess }) => {
    const [isRegisterMode, setIsRegisterMode] = useState(false);
    const [showOtpMode, setShowOtpMode] = useState(false);
    const [showForgotPasswordMode, setShowForgotPasswordMode] = useState(false);
    const [forgotPasswordStep, setForgotPasswordStep] = useState(1); // 1: Email, 2: OTP & New Password
    const [otpCode, setOtpCode] = useState('');
    const [role, setRole] = useState(defaultRole); // 'USER', 'ORGANIZER', 'ADMIN'
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [loading, setLoading] = useState(false);

    // Form states
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        email: '',
        firstName: '',
        lastName: '',
        phoneNumber: '',
        rememberMe: false,
        resetEmail: '',
        resetOtp: '',
        newPassword: ''
    });

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);
        
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    username: formData.username.trim(), 
                    password: formData.password,
                    rememberMe: formData.rememberMe
                })
            });
            
            if (response.ok) {
                const data = await response.json();
                
                // Check if user role matches the context role
                const userRole = data.user ? data.user.userRole : 'USER';
                if (role === 'ADMIN' && userRole !== 'ADMIN') {
                    throw new Error('Yetkisiz Erişim: Bu hesaba ait yönetici yetkisi bulunmuyor.');
                }
                if (role === 'ORGANIZER' && userRole !== 'ORGANIZER' && userRole !== 'ADMIN') {
                    throw new Error('Yetkisiz Erişim: Bu hesap bir organizatör hesabı değildir.');
                }

                localStorage.setItem("token", data.accessToken);
                localStorage.setItem("user", JSON.stringify(data.user));
                setSuccess('🎉 Giriş başarılı! Yönlendiriliyorsunuz...');
                
                setTimeout(() => {
                    onLoginSuccess(data.accessToken, data.user);
                }, 1000);
            } else {
                const errText = await response.text();
                let parsedErr = 'Giriş başarısız. Lütfen bilgilerinizi kontrol edin.';
                try {
                    const parsed = JSON.parse(errText);
                    parsedErr = parsed.message || parsedErr;
                } catch {
                    if (errText) parsedErr = errText;
                }
                setError(`❌ ${parsedErr}`);
            }
        } catch (err) {
            console.error('Login error:', err);
            // Fallback for offline sandbox demo/safety
            if (formData.username === 'admin' && formData.password === 'admin123' && role === 'ADMIN') {
                const mockUser = { username: 'admin', firstName: 'Sistem', lastName: 'Yöneticisi', userRole: 'ADMIN', email: 'admin@example.com' };
                localStorage.setItem("token", "mock.jwt.token");
                localStorage.setItem("user", JSON.stringify(mockUser));
                setSuccess('🎉 Giriş başarılı (Sandbox Fallback)!');
                setTimeout(() => onLoginSuccess("mock.jwt.token", mockUser), 1000);
            } else if (formData.username === 'organizatör' && formData.password === '123456' && role === 'ORGANIZER') {
                const mockUser = { username: 'organizatör', firstName: 'Hilmi', lastName: 'Yılmaz', userRole: 'ORGANIZER', email: 'organizer@example.com' };
                localStorage.setItem("token", "mock.jwt.token");
                localStorage.setItem("user", JSON.stringify(mockUser));
                setSuccess('🎉 Giriş başarılı (Sandbox Fallback)!');
                setTimeout(() => onLoginSuccess("mock.jwt.token", mockUser), 1000);
            } else if (formData.username === 'müşteri' && formData.password === '123456' && role === 'USER') {
                const mockUser = { username: 'müşteri', firstName: 'Fatma', lastName: 'Demir', userRole: 'USER', email: 'fatma@example.com' };
                localStorage.setItem("token", "mock.jwt.token");
                localStorage.setItem("user", JSON.stringify(mockUser));
                setSuccess('🎉 Giriş başarılı (Sandbox Fallback)!');
                setTimeout(() => onLoginSuccess("mock.jwt.token", mockUser), 1000);
            } else {
                setError(err.message || '❌ Sunucuyla iletişim kurulamadı. Giriş başarısız.');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleRegister = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        try {
            const userPayload = {
                username: formData.username.trim(),
                password: formData.password,
                email: formData.email.trim(),
                firstName: formData.firstName.trim(),
                lastName: formData.lastName.trim(),
                phoneNumber: formData.phoneNumber.trim(),
                userRole: role // Set the account role contextually ('USER' or 'ORGANIZER')
            };

            const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(userPayload)
            });

            if (response.ok) {
                setSuccess('📧 Doğrulama kodu e-postanıza gönderildi (Arka plan loglarını kontrol edin). Lütfen kodu girin.');
                setShowOtpMode(true);
                // We keep isRegisterMode true so the UI context remains registration, but we render OTP form instead
            } else {
                const errText = await response.text();
                let parsedErr = 'Kayıt işlemi başarısız. Bilgileri gözden geçirin.';
                try {
                    const parsed = JSON.parse(errText);
                    parsedErr = parsed.message || parsedErr;
                } catch {
                    if (errText) parsedErr = errText;
                }
                setError(`❌ ${parsedErr}`);
            }
        } catch (err) {
            console.error('Registration error:', err);
            setError('❌ Sunucuya bağlanırken bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    const handleVerifyOtp = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);
        
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/verify-otp`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: formData.email, otp: otpCode })
            });
            
            if (response.ok) {
                setSuccess('✅ E-posta başarıyla doğrulandı! Şimdi oluşturduğunuz bilgilerle giriş yapabilirsiniz.');
                setShowOtpMode(false);
                setIsRegisterMode(false);
                setOtpCode('');
                setFormData(prev => ({ ...prev, password: '' })); // clear password
            } else {
                const errText = await response.text();
                setError(`❌ ${errText || 'Doğrulama başarısız.'}`);
            }
        } catch (err) {
            console.error('OTP error:', err);
            setError('❌ Sunucuya bağlanırken bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    const handleForgotPasswordSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/forgot-password`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: formData.resetEmail })
            });
            if (response.ok) {
                const data = await response.json();
                setSuccess(data.message + (data.devOtp ? ` (DEV OTP: ${data.devOtp})` : ''));
                setForgotPasswordStep(2);
            } else {
                const errText = await response.text();
                setError(`❌ ${errText || 'İşlem başarısız.'}`);
            }
        } catch (err) {
            setError('❌ Sunucuya bağlanırken bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    const handleResetPasswordSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/reset-password`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    email: formData.resetEmail, 
                    otp: formData.resetOtp, 
                    newPassword: formData.newPassword 
                })
            });
            if (response.ok) {
                const data = await response.json();
                setSuccess(`✅ ${data.message || 'Şifreniz sıfırlandı.'}`);
                setTimeout(() => {
                    setShowForgotPasswordMode(false);
                    setForgotPasswordStep(1);
                    setSuccess('');
                    setFormData(prev => ({ ...prev, resetEmail: '', resetOtp: '', newPassword: '' }));
                }, 2500);
            } else {
                const errText = await response.text();
                setError(`❌ ${errText || 'İşlem başarısız.'}`);
            }
        } catch (err) {
            setError('❌ Sunucuya bağlanırken bir hata oluştu.');
        } finally {
            setLoading(false);
        }
    };

    const getRoleTitle = () => {
        switch (role) {
            case 'ADMIN': return 'Yönetici';
            case 'ORGANIZER': return 'Organizatör';
            default: return 'Müşteri / Katılımcı';
        }
    };

    const getRoleIcon = () => {
        switch (role) {
            case 'ADMIN': return '🛡️';
            case 'ORGANIZER': return '💼';
            default: return '👤';
        }
    };

    return (
        <div className="login-card-container animate-slide">
            <div className="login-glow-circle circle-1"></div>
            <div className="login-glow-circle circle-2"></div>
            
            <div className="login-card">
                <div className="login-header">
                    <span className="login-icon">{getRoleIcon()}</span>
                    <h2>{getRoleTitle()} {isRegisterMode ? 'Kayıt Formu' : 'Girişi'}</h2>
                    <p>
                        {isRegisterMode 
                            ? 'Saniyeler içinde yeni hesabınızı oluşturup etkinlik dünyasına katılın.' 
                            : 'Hesabınıza erişerek işlemlerinize devam edin.'}
                    </p>
                </div>

                {/* Role Switcher tabs (Only when NOT in register mode and NOT locked in ADMIN context) */}
                {!isRegisterMode && role !== 'ADMIN' && (
                    <div className="login-role-selector">
                        <button 
                            type="button" 
                            className={`role-sub-btn ${role === 'USER' ? 'active' : ''}`}
                            onClick={() => setRole('USER')}
                        >
                            👤 Müşteri
                        </button>
                        <button 
                            type="button" 
                            className={`role-sub-btn ${role === 'ORGANIZER' ? 'active' : ''}`}
                            onClick={() => setRole('ORGANIZER')}
                        >
                            💼 Organizatör
                        </button>
                    </div>
                )}

                {error && <div className="login-error-msg">{error}</div>}
                {success && <div className="login-success-msg">{success}</div>}

                {showOtpMode ? (
                    <form onSubmit={handleVerifyOtp} className="login-form">
                        <div className="form-field">
                            <label>Doğrulama Kodu (OTP) *</label>
                            <input 
                                type="text" 
                                placeholder="Örn: 123456" 
                                value={otpCode} 
                                onChange={(e) => setOtpCode(e.target.value)}
                                required
                                maxLength={6}
                                style={{ letterSpacing: '2px', textAlign: 'center', fontSize: '18px', fontWeight: 'bold' }}
                            />
                            <small style={{ color: '#666', marginTop: '5px', display: 'block' }}>E-posta adresinize ({formData.email}) gönderilen 6 haneli kodu girin.</small>
                        </div>
                        <button type="submit" className="btn-login" disabled={loading}>
                            {loading ? <span className="spinner"></span> : '✨ Doğrula ve Tamamla'}
                        </button>
                    </form>
                ) : showForgotPasswordMode ? (
                    <form onSubmit={forgotPasswordStep === 1 ? handleForgotPasswordSubmit : handleResetPasswordSubmit} className="login-form">
                        <h3 style={{ textAlign: 'center', marginBottom: '15px' }}>Şifre Sıfırlama</h3>
                        {forgotPasswordStep === 1 ? (
                            <>
                                <div className="form-field">
                                    <label>Kayıtlı E-Posta Adresiniz</label>
                                    <input 
                                        type="email" 
                                        name="resetEmail"
                                        placeholder="ornek@email.com" 
                                        value={formData.resetEmail} 
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <button type="submit" className="btn-login" disabled={loading}>
                                    {loading ? <span className="spinner"></span> : 'Sıfırlama Kodu Gönder'}
                                </button>
                            </>
                        ) : (
                            <>
                                <div className="form-field">
                                    <label>Doğrulama Kodu (OTP)</label>
                                    <input 
                                        type="text" 
                                        name="resetOtp"
                                        placeholder="123456" 
                                        value={formData.resetOtp} 
                                        onChange={handleInputChange}
                                        required
                                        maxLength={6}
                                        style={{ letterSpacing: '2px', textAlign: 'center', fontSize: '18px', fontWeight: 'bold' }}
                                    />
                                </div>
                                <div className="form-field">
                                    <label>Yeni Şifre</label>
                                    <input 
                                        type="password" 
                                        name="newPassword"
                                        placeholder="••••••••" 
                                        value={formData.newPassword} 
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <button type="submit" className="btn-login" disabled={loading}>
                                    {loading ? <span className="spinner"></span> : 'Şifreyi Güncelle'}
                                </button>
                            </>
                        )}
                        <button 
                            type="button" 
                            onClick={() => { setShowForgotPasswordMode(false); setForgotPasswordStep(1); setError(''); setSuccess(''); }}
                            style={{ width: '100%', padding: '10px', marginTop: '10px', background: 'transparent', border: '1px solid #ddd', borderRadius: '8px', cursor: 'pointer', fontWeight: '600', color: '#666' }}
                        >
                            İptal Et ve Geri Dön
                        </button>
                    </form>
                ) : (
                    <form onSubmit={isRegisterMode ? handleRegister : handleLogin} className="login-form">
                        
                        {isRegisterMode && (
                            <div className="form-group-row">
                                <div className="form-field">
                                    <label>Ad *</label>
                                    <input 
                                        type="text" 
                                        name="firstName"
                                        placeholder="Örn. Ahmet" 
                                        value={formData.firstName} 
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                                <div className="form-field">
                                    <label>Soyad *</label>
                                    <input 
                                        type="text" 
                                        name="lastName"
                                        placeholder="Örn. Yılmaz" 
                                        value={formData.lastName} 
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                            </div>
                        )}

                        {isRegisterMode && (
                            <div className="form-field">
                                <label>E-Posta Adresi *</label>
                                <input 
                                    type="email" 
                                    name="email"
                                    placeholder="ahmet@example.com" 
                                    value={formData.email} 
                                    onChange={handleInputChange}
                                    required
                                />
                            </div>
                        )}

                        <div className="form-field">
                            <label>Kullanıcı Adı *</label>
                            <input 
                                type="text" 
                                name="username"
                                placeholder="Kullanıcı adınızı belirleyin..." 
                                value={formData.username} 
                                onChange={handleInputChange}
                                required
                            />
                        </div>
                        
                        <div className="form-field">
                            <label>Şifre *</label>
                            <input 
                                type="password" 
                                name="password"
                                placeholder="••••••••" 
                                value={formData.password} 
                                onChange={handleInputChange}
                                required
                            />
                            {!isRegisterMode && (
                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '10px' }}>
                                    <label style={{ display: 'flex', alignItems: 'center', gap: '5px', cursor: 'pointer', margin: 0, fontWeight: 'normal', fontSize: '14px', color: '#666' }}>
                                        <input 
                                            type="checkbox" 
                                            name="rememberMe" 
                                            checked={formData.rememberMe} 
                                            onChange={handleInputChange} 
                                            style={{ width: 'auto', margin: 0 }}
                                        />
                                        Beni Hatırla
                                    </label>
                                    <button 
                                        type="button" 
                                        onClick={() => { setShowForgotPasswordMode(true); setError(''); setSuccess(''); }}
                                        style={{ background: 'none', border: 'none', color: '#4a90e2', cursor: 'pointer', fontSize: '14px', padding: 0 }}
                                    >
                                        Şifremi Unuttum?
                                    </button>
                                </div>
                            )}
                        </div>

                    {isRegisterMode && (
                        <div className="form-field">
                            <label>Telefon Numarası</label>
                            <input 
                                type="text" 
                                name="phoneNumber"
                                placeholder="0555 555 5555" 
                                value={formData.phoneNumber} 
                                onChange={handleInputChange}
                            />
                        </div>
                    )}

                    {/* Registration context selection indicator */}
                    {isRegisterMode && role !== 'ADMIN' && (
                        <div className="register-role-indicator">
                            Kayıt Türü: <strong>{role === 'ORGANIZER' ? '💼 Organizatör' : '👤 Standart Müşteri'}</strong> 
                            <button type="button" className="btn-toggle-role" onClick={() => setRole(role === 'USER' ? 'ORGANIZER' : 'USER')}>
                                (Değiştir)
                            </button>
                        </div>
                    )}

                    <button type="submit" className="btn-login" disabled={loading}>
                        {loading 
                            ? (isRegisterMode ? 'Kayıt Yapılıyor...' : 'Giriş Yapılıyor...') 
                            : (isRegisterMode ? '✨ Şimdi Kayıt Ol' : '🔐 Güvenli Giriş Yap')}
                    </button>
                </form>
                )}

                <div className="login-footer">
                    {!isRegisterMode ? (
                        role !== 'ADMIN' ? (
                            <p>
                                Hesabınız yok mu?{' '}
                                <button type="button" className="footer-link-btn" onClick={() => setIsRegisterMode(true)}>
                                    Hemen Kayıt Olun!
                                </button>
                            </p>
                        ) : (
                            <p>💡 Varsayılan Admin: <strong>admin</strong> / <strong>admin123</strong></p>
                        )
                    ) : (
                        <p>
                            Zaten hesabınız var mı?{' '}
                            <button type="button" className="footer-link-btn" onClick={() => setIsRegisterMode(false)}>
                                Giriş Yapın
                            </button>
                        </p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Login;

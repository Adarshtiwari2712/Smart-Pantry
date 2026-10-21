document.addEventListener('DOMContentLoaded', () => {
    const loginEmailStep = document.getElementById('login-email-step');
    const loginOtpStep = document.getElementById('login-otp-step');
    const loginForm = document.getElementById('login-form');
    const loginOtpForm = document.getElementById('login-otp-form');
    const loginEmail = document.getElementById('login-email');
    const loginDisplayEmail = document.getElementById('login-display-email');
    const backLink = document.getElementById('login-back-link');
    const otpInputs = loginOtpStep ? loginOtpStep.querySelectorAll('.otp-box') : [];

    const API_BASE_URL = 'http://localhost:8084/api/auth';

    // Step 1: Send OTP
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = loginEmail.value.trim();

            if (!email || !email.includes('@')) {
                alert('Valid email address enter karein!');
                return;
            }

            const submitBtn = loginForm.querySelector('button[type="submit"]');

            try {
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerText = 'Sending OTP...';
                }

                const response = await fetch(`${API_BASE_URL}/send-otp?email=${encodeURIComponent(email)}`, {
                    method: 'POST'
                });

                const responseText = await response.text();

                if (response.ok) {
                    localStorage.setItem('temp_login_email', email);
                    if (loginDisplayEmail) loginDisplayEmail.textContent = email;

                    loginEmailStep.classList.add('hidden');
                    loginOtpStep.classList.remove('hidden');
                    if (otpInputs.length > 0) otpInputs[0].focus();
                } else {
                    alert(responseText || 'OTP send karne mein issue aaya!');
                }
            } catch (err) {
                console.error('Login Error:', err);
                alert('Server error! Check karein backend running hai.');
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerText = 'Send Login OTP';
                }
            }
        });
    }

    // Step 2: Back Link
    if (backLink) {
        backLink.addEventListener('click', (e) => {
            e.preventDefault();
            loginOtpStep.classList.add('hidden');
            loginEmailStep.classList.remove('hidden');
        });
    }

    // Step 3: OTP Auto-focus (Fixed Syntax)
    otpInputs.forEach((input, index) => {
        input.addEventListener('input', () => {
            if (input.value.length === 1 && index < otpInputs.length - 1) {
                otpInputs[index + 1].focus();
            }
        });
        input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && input.value === '' && index > 0) {
                otpInputs[index - 1].focus();
            }
        });
    });

    // Step 4: Verify OTP & Sign In
    if (loginOtpForm) {
        loginOtpForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            let otpVal = '';
            otpInputs.forEach(i => otpVal += i.value.trim());

            if (otpVal.length !== 6) {
                alert('6-digit OTP daalein!');
                return;
            }

            const email = localStorage.getItem('temp_login_email');
            const submitBtn = loginOtpForm.querySelector('button[type="submit"]');

            try {
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerText = 'Verifying...';
                }

                const response = await fetch(`${API_BASE_URL}/verify-otp?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otpVal)}`, {
                    method: 'POST'
                });

                const responseText = await response.text();

                if (response.ok) {
                    localStorage.setItem('user_email', email);
                    localStorage.removeItem('temp_login_email');

                    alert('Login Successful!');
                    window.location.href = 'dashboard.html';
                } else {
                    alert(responseText || 'Invalid OTP! Dobara check karein.');
                }
            } catch (err) {
                console.error('Verify Error:', err);
                alert('Server connect nahi ho paaya.');
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerText = 'Verify & Sign In';
                }
            }
        });
    }
});
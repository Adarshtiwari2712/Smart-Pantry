document.addEventListener('DOMContentLoaded', () => {
    const loginEmailStep = document.getElementById('login-email-step');
    const loginOtpStep = document.getElementById('login-otp-step');
    const loginForm = document.getElementById('login-form');
    const loginOtpForm = document.getElementById('login-otp-form');
    const loginEmail = document.getElementById('login-email');
    const loginDisplayEmail = document.getElementById('login-display-email');
    const backLink = document.getElementById('login-back-link');
    const otpInputs = loginOtpStep
        ? loginOtpStep.querySelectorAll('.otp-box')
        : [];

    // Correct backend URL
    const API_BASE_URL = 'http://localhost:8084/api/auth';

    // Step 1: Send Login OTP
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const email = loginEmail.value.trim();

            if (!email || !email.includes('@')) {
                alert('Valid email address enter karein!');
                return;
            }

            const submitBtn = loginForm.querySelector(
                'button[type="submit"]'
            );

            try {
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerText = 'Sending OTP...';
                }

                // Send OTP to the entered email
                const response = await fetch(
                    `${API_BASE_URL}/send-otp?email=${encodeURIComponent(email)}`,
                    {
                        method: 'POST'
                    }
                );

                const responseText = await response.text();

                if (response.ok) {
                    localStorage.setItem('temp_login_email', email);
                    localStorage.setItem('user_phone', email);

                    if (loginDisplayEmail) {
                        loginDisplayEmail.textContent = email;
                    }

                    loginEmailStep.classList.add('hidden');
                    loginOtpStep.classList.remove('hidden');

                    if (otpInputs.length > 0) {
                        otpInputs[0].focus();
                    }

                    alert('OTP sent successfully! Apna email check karein.');
                } else {
                    alert(responseText || 'OTP send karne mein issue aaya!');
                }
            } catch (err) {
                console.error('Send Login OTP Error:', err);
                alert(
                    'Server se connect nahi ho paaya. Backend URL aur CORS check karein.'
                );
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerText = 'Send Login OTP';
                }
            }
        });
    }

    // Step 2: Back button
    if (backLink) {
        backLink.addEventListener('click', (e) => {
            e.preventDefault();

            loginOtpStep.classList.add('hidden');
            loginEmailStep.classList.remove('hidden');
        });
    }

    // Step 3: OTP input navigation
    otpInputs.forEach((input, index) => {
        input.addEventListener('input', () => {
            input.value = input.value.replace(/\D/g, '').slice(0, 1);

            if (
                input.value.length === 1 &&
                index < otpInputs.length - 1
            ) {
                otpInputs[index + 1].focus();
            }
        });

        input.addEventListener('keydown', (e) => {
            if (
                e.key === 'Backspace' &&
                input.value === '' &&
                index > 0
            ) {
                otpInputs[index - 1].focus();
            }
        });
    });

    // Step 4: Verify OTP and sign in
    if (loginOtpForm) {
        loginOtpForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            let otpVal = '';

            otpInputs.forEach((input) => {
                otpVal += input.value.trim();
            });

            if (otpVal.length !== 6) {
                alert('6-digit OTP daalein!');
                return;
            }

            const email = localStorage.getItem('temp_login_email');

            if (!email) {
                alert('Email session nahi mila. Dobara login karein.');
                return;
            }

            const submitBtn = loginOtpForm.querySelector(
                'button[type="submit"]'
            );

            try {
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerText = 'Verifying...';
                }

                // Use the separate login verification endpoint
                const response = await fetch(
                    `${API_BASE_URL}/login-verify-otp?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otpVal)}`,
                    {
                        method: 'POST'
                    }
                );

                const responseText = await response.text();

                if (response.ok) {
                    localStorage.setItem('user_email', email);
                    localStorage.removeItem('temp_login_email');

                    alert('Login Successful!');

                    window.location.href = 'dashboard.html';
                } else {
                    alert(
                        responseText || 'Invalid OTP! Dobara check karein.'
                    );
                }
            } catch (err) {
                console.error('Verify Login OTP Error:', err);
                alert('Server connect nahi ho paaya. Backend aur API URL check karein.');
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerText = 'Verify & Sign In';
                }
            }
        });
    }
});
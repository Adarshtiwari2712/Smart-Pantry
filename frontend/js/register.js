document.addEventListener('DOMContentLoaded', () => {
    const regDetailsStep = document.getElementById('reg-details-step');
    const regOtpStep = document.getElementById('reg-otp-step');
    const registerForm = document.getElementById('register-form');
    const regOtpForm = document.getElementById('reg-otp-form');
    const regName = document.getElementById('reg-name');
    const regEmail = document.getElementById('reg-email');
    const regDisplayEmail = document.getElementById('reg-display-email');
    const backLink = document.getElementById('reg-back-link');
    const otpInputs = regOtpStep ? regOtpStep.querySelectorAll('.otp-box') : [];

    const API_BASE_URL = 'http://localhost:8084/api/auth';

    // Step 1: Submit Details -> Call /send-otp
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = regName.value.trim();
            const email = regEmail.value.trim();

            if (!name) {
                alert('Apna naam enter karein!');
                return;
            }

            if (!email || !email.includes('@')) {
                alert('Valid email address enter karein!');
                return;
            }

            const submitBtn = registerForm.querySelector('button[type="submit"]');

            try {
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerText = 'Sending Email OTP...';
                }

                // Spring Boot Controller @RequestParam email expect karta hai
                const response = await fetch(`${API_BASE_URL}/send-otp?email=${encodeURIComponent(email)}`, {
                    method: 'POST'
                });

                const responseText = await response.text();

                if (response.ok) {
                    localStorage.setItem('temp_user_name', name);
                    localStorage.setItem('temp_user_email', email);

                    if (regDisplayEmail) {
                        regDisplayEmail.textContent = email;
                    }

                    regDetailsStep.classList.add('hidden');
                    regOtpStep.classList.remove('hidden');
                    if (otpInputs.length > 0) otpInputs[0].focus();
                } else {
                    alert(responseText || 'OTP send karne mein dikkat aayi!');
                }
            } catch (err) {
                console.error('Send OTP Error:', err);
                alert('Server error! Check karein backend port 8084 running hai.');
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerText = 'Get Registration OTP';
                }
            }
        });
    }

    // Step 2: Back button
    if (backLink) {
        backLink.addEventListener('click', (e) => {
            e.preventDefault();
            regOtpStep.classList.add('hidden');
            regDetailsStep.classList.remove('hidden');
        });
    }

    // Step 3: OTP inputs auto-navigation
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

    // Step 4: Submit OTP -> Call /verify-otp
    if (regOtpForm) {
        regOtpForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            let otpVal = '';
            otpInputs.forEach(i => otpVal += i.value.trim());

            if (otpVal.length !== 6) {
                alert('6-digit OTP daalein!');
                return;
            }

            const name = localStorage.getItem('temp_user_name');
            const email = localStorage.getItem('temp_user_email');
            const submitBtn = regOtpForm.querySelector('button[type="submit"]');

            try {
                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerText = 'Verifying...';
                }

                // Spring Boot Controller @RequestParam email aur otp expect karta hai

                const response = await fetch(
                    `${API_BASE_URL}/verify-otp?email=${encodeURIComponent(email)}&otp=${encodeURIComponent(otpVal)}&name=${encodeURIComponent(name)}`,
                    {
                        method: 'POST'
                    }
                );


                const responseText = await response.text();

                if (response.ok) {
                    // Registration / Verification Successful
                    localStorage.setItem('user_name', name);
                    localStorage.setItem('user_email', email);

                    localStorage.removeItem('temp_user_name');
                    localStorage.removeItem('temp_user_email');

                    alert('Verification Successful! Redirecting to dashboard...');
                    window.location.href = 'dashboard.html';
                } else {
                    alert(responseText || 'Invalid OTP. Kripya dubara check karein.');
                }
            } catch (err) {
                console.error('Verification Error:', err);
                alert('Backend communication error!');
            } finally {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerText = 'Verify & Register';
                }
            }
        });
    }
});
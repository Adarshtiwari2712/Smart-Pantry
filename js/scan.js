document.addEventListener('DOMContentLoaded', () => {
    // 1. DOM Elements
    const dropzoneArea = document.getElementById('dropzone-area');
    const fileInput = document.getElementById('file-input');
    const previewWrapper = document.getElementById('preview-wrapper');
    const imagePreview = document.getElementById('image-preview');
    const btnReupload = document.getElementById('btn-reupload');
    const loadingIndicator = document.getElementById('loading-indicator');
    const resultSheet = document.getElementById('result-sheet');
    const btnCancelScan = document.getElementById('btn-cancel-scan');

    // Result Form Inputs
    const productNameInput = document.getElementById('product-name');
    const productCategoryInput = document.getElementById('product-category');
    const productUnitsInput = document.getElementById('product-units');
    const productExpiryInput = document.getElementById('product-expiry');
    const productTipInput = document.getElementById('product-tip');
    const saveProductForm = document.getElementById('save-product-form');

    // 2. Click on Dropzone triggers File Chooser / Camera
    if (dropzoneArea && fileInput) {
        dropzoneArea.addEventListener('click', () => {
            fileInput.click();
        });
    }

    // 3. Re-upload button click
    if (btnReupload && fileInput) {
        btnReupload.addEventListener('click', () => {
            fileInput.value = '';
            fileInput.click();
        });
    }

    // 4. Cancel button action
    if (btnCancelScan) {
        btnCancelScan.addEventListener('click', () => {
            if (resultSheet) resultSheet.classList.add('hidden');
            if (previewWrapper) previewWrapper.classList.add('hidden');
            if (dropzoneArea) dropzoneArea.classList.remove('hidden');
            if (fileInput) fileInput.value = '';
        });
    }

    // 5. File Selection & AI Vision Call
    if (fileInput) {
        fileInput.addEventListener('change', async (e) => {
            const file = e.target.files[0];
            if (!file) return;

            // Preview local image
            if (imagePreview && dropzoneArea && previewWrapper) {
                const reader = new FileReader();
                reader.onload = (event) => {
                    imagePreview.src = event.target.result;
                    dropzoneArea.classList.add('hidden');
                    previewWrapper.classList.remove('hidden');
                };
                reader.readAsDataURL(file);
            }

            // Show Loading Spinner & Hide Previous Results
            if (resultSheet) resultSheet.classList.add('hidden');
            if (loadingIndicator) loadingIndicator.classList.remove('hidden');

            const formData = new FormData();
            formData.append('file', file);

            try {
                const response = await fetch('http://localhost:8084/api/pantry/scan', {
                    method: 'POST',
                    body: formData
                });

                if (!response.ok) {
                    const errText = await response.text();
                    throw new Error(errText || 'Failed to scan image');
                }

                const data = await response.json();

                // Auto-fill values
                if (data.itemName && productNameInput) {
                    productNameInput.value = data.itemName;
                }
                if (data.expiryDate && data.expiryDate !== 'null' && productExpiryInput) {
                    productExpiryInput.value = data.expiryDate;
                }

                if (resultSheet) resultSheet.classList.remove('hidden');

            } catch (error) {
                console.error('Scan Error:', error);
                alert('AI Scan error: ' + error.message);
            } finally {
                if (loadingIndicator) loadingIndicator.classList.add('hidden');
            }
        });
    }

    // 6. Submit Form to Save in Pantry
    if (saveProductForm) {
        saveProductForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            // Retrieve User Phone Number (Dashboard session id)
            let userPhone = localStorage.getItem('user_phone') || localStorage.getItem('userPhone');

            // Fallback agar direct page open kiya ho
            if (!userPhone) {
                userPhone = prompt('Enter your registered phone number:', '9876543210');
                if (!userPhone) return;
                localStorage.setItem('user_phone', userPhone);
            }

            // Exact match with ProductRequest.java
            const payload = {
                userId: userPhone.trim(),
                productName: productNameInput.value.trim(),
                category: productCategoryInput ? productCategoryInput.value : 'General',
                units: parseInt(productUnitsInput.value, 10) || 1,
                expiryDate: productExpiryInput.value,
                usageTip: productTipInput ? productTipInput.value.trim() : ''
            };

            try {
                const response = await fetch('http://localhost:8084/api/pantry/add', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                });

                const result = await response.json();

                if (response.ok && (result.success || result.data || result.id)) {
                    alert('Product successfully added to your Pantry!');
                    window.location.href = 'dashboard.html';
                } else {
                    alert('Backend rejected save: ' + (result.message || JSON.stringify(result)));
                }
            } catch (err) {
                console.error('Save error:', err);
                alert('Error connecting to backend: ' + err.message);
            }
        });
    }
});
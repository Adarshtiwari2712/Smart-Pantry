document.addEventListener('DOMContentLoaded', () => {
    const API_BASE_URL = 'http://localhost:8084/api/pantry';

    // 1. Check Session
    const userPhone = localStorage.getItem('user_phone') || localStorage.getItem('userPhone');
    const userName = localStorage.getItem('user_name') || localStorage.getItem('userName');

    if (!userPhone) {
        alert('Session expired! Pehle login karein.');
        window.location.href = 'login.html';
        return;
    }

    // 2. DOM Elements
    const navPhoneDisplay = document.getElementById('nav-phone-display');
    const logoutBtn = document.getElementById('logout-btn');
    const statActive = document.getElementById('stat-active');
    const statExpiring = document.getElementById('stat-expiring');
    const statRestock = document.getElementById('stat-restock');
    const priorityAlertsList = document.getElementById('priority-alerts-list');
    const inventoryTableBody = document.getElementById('inventory-table-body');
    const shoppingListDisplay = document.getElementById('shopping-list-display');

    // Restock Modal Elements
    const restockModal = document.getElementById('restock-modal');
    const restockModalMsg = document.getElementById('restock-modal-msg');
    const btnAddRestock = document.getElementById('btn-add-restock');
    const btnSkipRestock = document.getElementById('btn-skip-restock');

    let currentFinishedItemName = null;

    // Header Display
    if (navPhoneDisplay) {
        navPhoneDisplay.textContent = userName ? `${userName} (${userPhone})` : userPhone;
    }

    // Logout Action
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            if (confirm('Kya aap logout karna chahte hain?')) {
                localStorage.clear();
                window.location.href = 'login.html';
            }
        });
    }

    // Load Data
    loadPantryData();
    renderShoppingList();

    // 3. Fetch Items from Backend
    async function loadPantryData() {
        try {
            const response = await fetch(`${API_BASE_URL}/user/${encodeURIComponent(userPhone)}`);
            const result = await response.json();

            // ApiResponse<List<ProductItem>> me data field ke andar items array hota hai
            const items = result.data || (Array.isArray(result) ? result : []);

            if (response.ok && items.length >= 0) {
                renderInventory(items);
            } else {
                console.error('Failed to load items:', result.message);
                renderEmptyState();
            }
        } catch (error) {
            console.error('Error connecting to backend:', error);
            renderEmptyState('Backend server offline hai ya connect nahi ho paya.');
        }
    }

    // 4. Render Table, Alerts & Stats
    function renderInventory(items) {
        if (!inventoryTableBody) return;

        let activeCount = 0;
        let expiringCount = 0;
        let alertsHtml = '';
        let tableRowsHtml = '';

        items.forEach(item => {
            // Flexible field support for MongoDB / Java models
            const pName = item.productName || item.name || 'Unnamed Product';
            const pUnits = item.units !== undefined ? item.units : (item.quantity !== undefined ? item.quantity : 1);
            const daysLeft = calculateDaysLeft(item.expiryDate);
            let statusBadge = '';
            let isPriorityAlert = false;

            if (daysLeft < 0) {
                statusBadge = `<span class="badge" style="background: #dc2626; color: #fff; padding: 3px 8px; border-radius: 4px; font-size: 11px;">Expired</span>`;
                isPriorityAlert = true;
                expiringCount++;
            } else if (daysLeft <= 2) {
                statusBadge = `<span class="badge" style="background: #d97706; color: #fff; padding: 3px 8px; border-radius: 4px; font-size: 11px;">Expires in ${daysLeft}d</span>`;
                isPriorityAlert = true;
                expiringCount++;
                activeCount++;
            } else {
                statusBadge = `<span class="badge" style="background: #059669; color: #fff; padding: 3px 8px; border-radius: 4px; font-size: 11px;">Fresh (${daysLeft}d left)</span>`;
                activeCount++;
            }

            // Priority Alert Card
            if (isPriorityAlert) {
                alertsHtml += `
                    <div style="background: rgba(239, 68, 68, 0.1); border-left: 4px solid #ef4444; padding: 10px 14px; margin-bottom: 8px; border-radius: 4px; display: flex; justify-content: space-between; align-items: center;">
                        <div>
                            <strong style="color: #fff;">${pName}</strong>
                            <div style="font-size: 12px; color: #f87171;">
                                ${daysLeft < 0 ? 'Item expire ho chuka hai!' : `Expiry agle ${daysLeft} din mein hai.`}
                                ${item.usageTip ? ` | 💡 ${item.usageTip}` : ''}
                            </div>
                        </div>
                        <button class="btn btn-outline" onclick="consumeUnit('${item.id}', '${pName}', ${pUnits})" style="font-size: 11px; padding: 4px 8px;">Use Now</button>
                    </div>
                `;
            }

            // Inventory Table Row
            tableRowsHtml += `
                <tr>
                    <td><strong>${pName}</strong></td>
                    <td>${item.category || 'General'}</td>
                    <td><strong>${pUnits}</strong></td>
                    <td>${item.expiryDate || 'N/A'}</td>
                    <td>${statusBadge}</td>
                    <td>
                        <button class="btn btn-outline" onclick="consumeUnit('${item.id}', '${pName}', ${pUnits})" style="font-size: 11px; padding: 4px 8px;">Consume -1</button>
                    </td>
                </tr>
            `;
        });

        // Update UI Counts
        if (statActive) statActive.textContent = activeCount;
        if (statExpiring) statExpiring.textContent = expiringCount;

        if (priorityAlertsList) {
            priorityAlertsList.innerHTML = alertsHtml || `<p style="font-size: 13px; color: var(--text-muted);">Koi urgent alerts nahi hain. Sabhi items fresh hain! 🍃</p>`;
        }

        if (tableRowsHtml) {
            inventoryTableBody.innerHTML = tableRowsHtml;
        } else {
            renderEmptyState();
        }
    }

    function renderEmptyState(message = 'Pantry mein abhi koi items nahi hain.') {
        if (inventoryTableBody) {
            inventoryTableBody.innerHTML = `
                <tr>
                    <td colspan="6" style="text-align: center; padding: 24px; color: var(--text-muted);">
                        ${message} <br>
                        <a href="scan.html" style="color: var(--primary-color); text-decoration: underline; font-size: 13px; display: inline-block; margin-top: 6px;">+ Add/Scan Product</a>
                    </td>
                </tr>
            `;
        }
        if (priorityAlertsList) {
            priorityAlertsList.innerHTML = `<p style="font-size: 13px; color: var(--text-muted);">Koi active items nahi hain.</p>`;
        }
        if (statActive) statActive.textContent = '0';
        if (statExpiring) statExpiring.textContent = '0';
    }

    // 5. Consume Unit Handler
    window.consumeUnit = async function(itemId, productName, currentUnits) {
        if (currentUnits <= 1) {
            currentFinishedItemName = productName;
            if (restockModalMsg) {
                restockModalMsg.textContent = `"${productName}" ke saare units khatam ho gaye hain. Kya ise shopping list mein add karein?`;
            }
            if (restockModal) restockModal.classList.remove('hidden');
        }

        try {
            const res = await fetch(`${API_BASE_URL}/consume/${itemId}`, {
                method: 'PUT'
            });
            const data = await res.json();
            if (res.ok && (data.success || data.id)) {
                loadPantryData();
            } else {
                alert(data.message || 'Error updating unit');
            }
        } catch (err) {
            console.error('Error consuming unit:', err);
        }
    };

    // Modal Actions
    if (btnAddRestock) {
        btnAddRestock.addEventListener('click', () => {
            if (currentFinishedItemName) {
                addToShoppingList(currentFinishedItemName);
            }
            if (restockModal) restockModal.classList.add('hidden');
        });
    }

    if (btnSkipRestock) {
        btnSkipRestock.addEventListener('click', () => {
            if (restockModal) restockModal.classList.add('hidden');
        });
    }

    // 6. Shopping List Management (LocalStorage)
    function getShoppingList() {
        return JSON.parse(localStorage.getItem(`shopping_list_${userPhone}`) || '[]');
    }

    function addToShoppingList(name) {
        const list = getShoppingList();
        if (!list.includes(name)) {
            list.push(name);
            localStorage.setItem(`shopping_list_${userPhone}`, JSON.stringify(list));
            renderShoppingList();
        }
    }

    function renderShoppingList() {
        const list = getShoppingList();
        if (statRestock) statRestock.textContent = list.length;

        if (!shoppingListDisplay) return;

        if (list.length === 0) {
            shoppingListDisplay.innerHTML = `<li style="color: var(--text-muted); list-style: none;">Restock list khali hai.</li>`;
            return;
        }

        shoppingListDisplay.innerHTML = list.map((item, idx) => `
            <li style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px;">
                <span>${item}</span>
                <button onclick="removeShoppingItem(${idx})" style="background: none; border: none; color: #ef4444; cursor: pointer; font-size: 12px;">✖</button>
            </li>
        `).join('');
    }

    window.removeShoppingItem = function(index) {
        const list = getShoppingList();
        list.splice(index, 1);
        localStorage.setItem(`shopping_list_${userPhone}`, JSON.stringify(list));
        renderShoppingList();
    };

    // Helper: Days Left Calculation (Clean & Exact Formula)
    function calculateDaysLeft(dateStr) {
        if (!dateStr) return 0;
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const exp = new Date(dateStr);
        exp.setHours(0, 0, 0, 0);
        const diffTime = exp.getTime() - today.getTime();
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    }
});
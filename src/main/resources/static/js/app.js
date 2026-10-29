// NovaShop Client Application

let currentUser = null;
let currentCategory = 'ALL';
let currentSearch = '';

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadProducts();
    refreshCart();
});

// Authentication Status
async function checkAuth() {
    try {
        const res = await fetch('/api/auth/me');
        if (res.ok) {
            currentUser = await res.json();
            renderAuthUI();
        } else {
            currentUser = null;
            renderAuthUI();
        }
    } catch (e) {
        currentUser = null;
        renderAuthUI();
    }
}

function renderAuthUI() {
    const authContainer = document.getElementById('auth-nav-container');
    if (currentUser && currentUser.authenticated) {
        authContainer.innerHTML = `
            <div style="display: flex; align-items: center; gap: 0.75rem;">
                <span style="font-weight: 600; font-size: 0.9rem;">👤 ${currentUser.fullName}</span>
                ${currentUser.role === 'ROLE_ADMIN' ? '<a href="/admin.html" class="btn btn-outline" style="padding: 0.35rem 0.75rem; font-size: 0.8rem;">Admin Portal</a>' : ''}
                <button onclick="handleLogout()" class="btn btn-outline" style="padding: 0.35rem 0.75rem; font-size: 0.8rem;">Logout</button>
            </div>
        `;
    } else {
        authContainer.innerHTML = `
            <button onclick="openModal('login-modal')" class="btn btn-outline">Sign In</button>
            <button onclick="openModal('register-modal')" class="btn btn-primary">Register</button>
        `;
    }
}

async function handleLogout() {
    await fetch('/api/auth/logout', { method: 'POST' });
    currentUser = null;
    checkAuth();
    showToast('Logged out successfully');
}

// Products Catalog
async function loadProducts() {
    const grid = document.getElementById('products-grid');
    grid.innerHTML = '<div style="text-align: center; grid-column: 1/-1; padding: 2rem;">Loading products...</div>';

    let url = '/api/products';
    const params = new URLSearchParams();
    if (currentCategory && currentCategory !== 'ALL') params.append('category', currentCategory);
    if (currentSearch && currentSearch.trim() !== '') params.append('search', currentSearch.trim());
    if ([...params].length > 0) url += '?' + params.toString();

    try {
        const res = await fetch(url);
        const products = await res.json();

        if (products.length === 0) {
            grid.innerHTML = '<div style="text-align: center; grid-column: 1/-1; padding: 3rem; color: var(--text-muted);">No products found matching your search.</div>';
            return;
        }

        grid.innerHTML = products.map(p => `
            <div class="product-card" data-product-id="${p.id}">
                <div class="product-img-wrapper">
                    <img src="${p.imageUrl || 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500'}" alt="${p.name}">
                    <span class="category-tag">${p.category}</span>
                </div>
                <div class="product-info">
                    <h3 class="product-title">${p.name}</h3>
                    <p class="product-desc">${p.description}</p>
                    <div class="product-bottom">
                        <div>
                            <div class="price">$${p.price.toFixed(2)}</div>
                            <span class="stock-tag ${p.stockQuantity <= 5 ? 'stock-low' : ''}">
                                ${p.stockQuantity > 0 ? `In Stock (${p.stockQuantity})` : 'Out of Stock'}
                            </span>
                        </div>
                        <button class="btn btn-primary add-to-cart-btn" 
                                onclick="addToCart(${p.id})" 
                                ${p.stockQuantity === 0 ? 'disabled' : ''}>
                            Add to Cart
                        </button>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (e) {
        grid.innerHTML = '<div style="color: var(--danger); text-align: center; grid-column: 1/-1;">Failed to load products.</div>';
    }
}

function filterCategory(category, btn) {
    currentCategory = category;
    document.querySelectorAll('.cat-chip').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    loadProducts();
}

function handleSearch(e) {
    currentSearch = e.target.value;
    loadProducts();
}

// Cart Drawer Operations
async function refreshCart() {
    try {
        const res = await fetch('/api/cart');
        const cart = await res.json();
        renderCart(cart);
    } catch (e) {
        console.error('Failed to load cart', e);
    }
}

function renderCart(cart) {
    const countEl = document.getElementById('cart-badge-count');
    const itemsEl = document.getElementById('cart-items-container');
    const subtotalEl = document.getElementById('cart-subtotal');
    const discountEl = document.getElementById('cart-discount');
    const shippingEl = document.getElementById('cart-shipping');
    const totalEl = document.getElementById('cart-total');

    const totalQty = cart.items ? cart.items.reduce((sum, item) => sum + item.quantity, 0) : 0;
    countEl.textContent = totalQty;

    if (!cart.items || cart.items.length === 0) {
        itemsEl.innerHTML = '<div style="text-align: center; padding: 3rem 1rem; color: var(--text-muted);">Your cart is currently empty.</div>';
        subtotalEl.textContent = '$0.00';
        discountEl.textContent = '-$0.00';
        shippingEl.textContent = '$0.00';
        totalEl.textContent = '$0.00';
        document.getElementById('checkout-drawer-btn').disabled = true;
        return;
    }

    document.getElementById('checkout-drawer-btn').disabled = false;
    itemsEl.innerHTML = cart.items.map(item => `
        <div class="cart-item">
            <img src="${item.imageUrl}" alt="${item.productName}">
            <div class="cart-item-details">
                <div class="cart-item-title">${item.productName}</div>
                <div class="cart-item-price">$${item.price.toFixed(2)} each</div>
                <div class="qty-controls">
                    <button class="qty-btn" onclick="updateQty(${item.productId}, ${item.quantity - 1})">-</button>
                    <span style="font-weight: 600; min-width: 20px; text-align: center;">${item.quantity}</span>
                    <button class="qty-btn" onclick="updateQty(${item.productId}, ${item.quantity + 1})">+</button>
                    <button onclick="removeFromCart(${item.productId})" style="background: none; border: none; color: var(--danger); cursor: pointer; margin-left: 0.5rem; font-size: 0.8rem;">Remove</button>
                </div>
            </div>
            <div style="font-weight: 700; font-size: 1rem;">$${item.subtotal.toFixed(2)}</div>
        </div>
    `).join('');

    subtotalEl.textContent = `$${cart.subtotal.toFixed(2)}`;
    discountEl.textContent = `-$${cart.discount.toFixed(2)}`;
    shippingEl.textContent = cart.shippingFee === 0 ? 'FREE' : `$${cart.shippingFee.toFixed(2)}`;
    totalEl.textContent = `$${cart.total.toFixed(2)}`;
}

async function addToCart(productId) {
    try {
        const res = await fetch('/api/cart/add', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ productId, quantity: 1 })
        });
        const data = await res.json();
        if (res.ok) {
            renderCart(data);
            openCart();
            showToast('Item added to cart!');
        } else {
            alert(data.error || 'Failed to add item to cart');
        }
    } catch (e) {
        alert('Network error while adding to cart');
    }
}

async function updateQty(productId, quantity) {
    const res = await fetch('/api/cart/update', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ productId, quantity })
    });
    const data = await res.json();
    if (res.ok) renderCart(data);
    else alert(data.error);
}

async function removeFromCart(productId) {
    const res = await fetch(`/api/cart/remove/${productId}`, { method: 'DELETE' });
    const data = await res.json();
    renderCart(data);
}

async function applyPromo() {
    const input = document.getElementById('promo-code-input');
    const code = input.value.trim();
    if (!code) return;

    const res = await fetch('/api/cart/promo', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ promoCode: code })
    });
    const data = await res.json();
    if (res.ok) {
        renderCart(data);
        showToast(`Promo ${code.toUpperCase()} applied!`);
    } else {
        alert(data.error || 'Invalid promo code');
    }
}

function openCart() {
    document.getElementById('cart-drawer').classList.add('open');
    document.getElementById('cart-overlay').classList.add('open');
}

function closeCart() {
    document.getElementById('cart-drawer').classList.remove('open');
    document.getElementById('cart-overlay').classList.remove('open');
}

// Checkout Workflow
function startCheckout() {
    closeCart();
    if (!currentUser || !currentUser.authenticated) {
        showToast('Please sign in or register to complete your order.');
        openModal('login-modal');
        return;
    }
    openModal('checkout-modal');
}

async function submitOrder(e) {
    e.preventDefault();
    const address = document.getElementById('checkout-address').value;
    const phone = document.getElementById('checkout-phone').value;
    const promo = document.getElementById('promo-code-input').value;

    const res = await fetch('/api/orders/checkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            shippingAddress: address,
            contactPhone: phone,
            promoCode: promo
        })
    });

    const data = await res.json();
    if (res.ok) {
        closeModal('checkout-modal');
        refreshCart();
        loadProducts(); // Update stock counts on grid

        document.getElementById('conf-order-id').textContent = data.orderNumber;
        document.getElementById('conf-order-total').textContent = `$${data.totalAmount.toFixed(2)}`;
        document.getElementById('conf-order-address').textContent = data.shippingAddress;
        openModal('confirmation-modal');
    } else {
        alert(data.error || 'Checkout failed');
    }
}

// Auth Forms
async function submitLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const errorBox = document.getElementById('login-error');

    errorBox.style.display = 'none';

    const res = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password })
    });

    const data = await res.json();
    if (res.ok) {
        closeModal('login-modal');
        checkAuth();
        showToast('Welcome back, ' + data.fullName + '!');
    } else {
        errorBox.textContent = data.error || 'Login failed';
        errorBox.style.display = 'block';
    }
}

async function submitRegister(e) {
    e.preventDefault();
    const fullName = document.getElementById('reg-name').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;
    const errorBox = document.getElementById('reg-error');

    errorBox.style.display = 'none';

    const res = await fetch('/api/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fullName, email, password })
    });

    const data = await res.json();
    if (res.ok) {
        closeModal('register-modal');
        showToast('Registration successful! Please sign in.');
        openModal('login-modal');
        document.getElementById('login-email').value = email;
    } else {
        errorBox.textContent = data.error || 'Registration failed';
        errorBox.style.display = 'block';
    }
}

// Helpers
function openModal(id) {
    document.getElementById(id).classList.add('open');
}

function closeModal(id) {
    document.getElementById(id).classList.remove('open');
}

function showToast(msg) {
    const toast = document.createElement('div');
    toast.textContent = msg;
    toast.style.cssText = `
        position: fixed; bottom: 20px; right: 20px;
        background: #0f172a; color: #fff; padding: 0.8rem 1.4rem;
        border-radius: 8px; z-index: 9999; font-weight: 600;
        box-shadow: 0 10px 15px -3px rgba(0,0,0,0.3);
        animation: fadeIn 0.3s ease;
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3200);
}

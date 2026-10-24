// HardTrack - AJAX Application
const API_BASE = '/api';
const AUTH_STORAGE_KEY = 'hardtrack.adminAuth';
const ADMIN_CHECK_DEVICE_ID = -999999;

// Authentication
function getAdminAuth() {
    try {
        const stored = sessionStorage.getItem(AUTH_STORAGE_KEY);
        if (!stored) return null;
        const auth = JSON.parse(stored);
        return auth && auth.username && auth.password ? auth : null;
    } catch (error) {
        return null;
    }
}

function buildAuthHeader(username, password) {
    return 'Basic ' + btoa(`${username}:${password}`);
}

function setAdminAuth(username, password) {
    sessionStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify({ username, password }));
}

function clearAdminAuth() {
    sessionStorage.removeItem(AUTH_STORAGE_KEY);
}

function adminFetch(url, options = {}) {
    const headers = new Headers(options.headers || {});
    const auth = getAdminAuth();
    if (auth) {
        headers.set('Authorization', buildAuthHeader(auth.username, auth.password));
    }
    return fetch(url, { ...options, headers });
}

function updateAdminAuthUi() {
    const auth = getAdminAuth();
    const loginItem = document.getElementById('adminLoginItem');
    const logoutItem = document.getElementById('adminLogoutItem');
    const userLabel = document.getElementById('adminLoggedUser');

    if (loginItem) loginItem.style.display = auth ? 'none' : '';
    if (logoutItem) logoutItem.style.display = auth ? '' : 'none';
    if (userLabel) userLabel.textContent = auth ? auth.username : '';
}

function showAuthError(message) {
    const errorBox = document.getElementById('authError');
    if (!errorBox) return;
    errorBox.textContent = message;
    errorBox.style.display = 'block';
}

function openAuthModal() {
    const modalEl = document.getElementById('authModal');
    if (!modalEl) return;
    const errorBox = document.getElementById('authError');
    if (errorBox) errorBox.style.display = 'none';
    bootstrap.Modal.getOrCreateInstance(modalEl).show();
}

function requireAdmin() {
    if (getAdminAuth()) return true;
    const deviceModal = document.getElementById('deviceModal');
    if (deviceModal) {
        const instance = bootstrap.Modal.getInstance(deviceModal);
        if (instance) instance.hide();
    }
    showAlert('Inicia sesión como administrador para modificar dispositivos', 'warning');
    openAuthModal();
    return false;
}

async function adminLogin(event) {
    if (event) event.preventDefault();

    const usernameEl = document.getElementById('authUsername');
    const passwordEl = document.getElementById('authPassword');
    const loginBtn = document.getElementById('authLoginBtn');
    if (!usernameEl || !passwordEl || !loginBtn) return;

    const username = usernameEl.value.trim();
    const password = passwordEl.value;

    if (!username || !password) {
        showAuthError('Introduce usuario y contraseña');
        return;
    }

    const originalText = loginBtn.innerHTML;
    loginBtn.disabled = true;
    loginBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Comprobando...';
    const errorBox = document.getElementById('authError');
    if (errorBox) errorBox.style.display = 'none';

    try {
        const authHeader = buildAuthHeader(username, password);

        const credentialsCheck = await fetch(`${API_BASE}/devices`, {
            headers: { 'Authorization': authHeader }
        });
        if (credentialsCheck.status === 401) {
            showAuthError('Usuario o contraseña incorrectos');
            return;
        }
        if (!credentialsCheck.ok) {
            throw new Error('No se pudo verificar el acceso');
        }

        const roleCheck = await fetch(`${API_BASE}/devices/${ADMIN_CHECK_DEVICE_ID}`, {
            method: 'DELETE',
            headers: { 'Authorization': authHeader }
        });
        if (roleCheck.status === 403) {
            showAuthError('El usuario no tiene permisos de administrador');
            return;
        }
        if (roleCheck.status !== 404) {
            throw new Error('No se pudo verificar el rol de administrador');
        }

        setAdminAuth(username, password);
        updateAdminAuthUi();

        const form = document.getElementById('authForm');
        if (form) form.reset();

        const modalEl = document.getElementById('authModal');
        const modal = modalEl ? bootstrap.Modal.getInstance(modalEl) : null;
        if (modal) modal.hide();

        showAlert(`Sesión de administrador iniciada como ${username}`, 'success');
    } catch (error) {
        showAuthError(error.message || 'Error al iniciar sesión');
    } finally {
        loginBtn.disabled = false;
        loginBtn.innerHTML = originalText;
    }
}

function adminLogout(event) {
    if (event) event.preventDefault();
    clearAdminAuth();
    updateAdminAuthUi();
    showAlert('Sesión de administrador cerrada', 'info');
}

function handleAdminResponse(response) {
    if (response.status === 401) {
        clearAdminAuth();
        updateAdminAuthUi();
        throw new Error('Debes iniciar sesión como administrador');
    }
    if (response.status === 403) {
        throw new Error('El usuario no tiene permisos de administrador');
    }
}

// Utility Functions
function showAlert(message, type = 'success') {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) return;
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;

    const icon = document.createElement('i');
    icon.className = `fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-circle' : 'info-circle'} me-2`;
    alertDiv.appendChild(icon);

    const text = document.createElement('span');
    text.textContent = message;
    alertDiv.appendChild(text);

    const closeBtn = document.createElement('button');
    closeBtn.type = 'button';
    closeBtn.className = 'btn-close';
    closeBtn.setAttribute('data-bs-dismiss', 'alert');
    alertDiv.appendChild(closeBtn);
    
    alertContainer.innerHTML = '';
    alertContainer.appendChild(alertDiv);
    
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
        const alert = bootstrap.Alert.getOrCreateInstance(alertDiv);
        if (alert) alert.close();
    }, 5000);
    
    // Scroll to top
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function formatDate() {
    return new Date().toLocaleString('es-ES');
}

// Device Management
async function loadDevices() {
    const tableBody = document.getElementById('devicesTableBody');
    const loadingSpinner = document.getElementById('devicesLoading');
    const emptyState = document.getElementById('devicesEmptyState');
    
    if (!tableBody) return;
    
    if (loadingSpinner) loadingSpinner.style.display = 'block';
    if (emptyState) emptyState.style.display = 'none';
    tableBody.innerHTML = '';
    
    try {
        const response = await fetch(`${API_BASE}/devices`);
        if (!response.ok) throw new Error('Error al cargar dispositivos');
        
        const devices = await response.json();
        
        if (loadingSpinner) loadingSpinner.style.display = 'none';
        
        if (devices.length === 0) {
            if (emptyState) emptyState.style.display = 'block';
            return;
        }
        
        devices.forEach(device => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><strong>#${device.id}</strong></td>
                <td>${escapeHtml(device.deviceDescription || '-')}</td>
                <td>${escapeHtml(device.type ? device.type.name : '-')}</td>
                <td>
                    <span class="badge ${device.state ? 'badge-success' : 'badge-danger'}">
                        <i class="fas fa-${device.state ? 'check-circle' : 'times-circle'} me-1"></i>
                        ${device.state ? 'Disponible' : 'No Disponible'}
                    </span>
                </td>
                <td class="text-end">
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="editDevice(${device.id})" title="Editar">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteDevice(${device.id}, '${escapeHtml(device.deviceDescription).replace(/'/g, "\\'")}')" title="Eliminar">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        if (loadingSpinner) loadingSpinner.style.display = 'none';
        showAlert('Error al cargar dispositivos: ' + error.message, 'danger');
        console.error('Error:', error);
    }
}

async function loadDeviceTypes() {
    try {
        const response = await fetch(`${API_BASE}/device-types`);
        if (!response.ok) throw new Error('Error al cargar tipos');
        const types = await response.json();
        
        const typeSelects = document.querySelectorAll('.device-type-select');
        typeSelects.forEach(select => {
            const currentValue = select.value;
            select.innerHTML = '<option value="">Seleccionar tipo...</option>';
            types.forEach(type => {
                const option = document.createElement('option');
                option.value = type.id;
                option.textContent = type.name;
                if (type.id == currentValue) option.selected = true;
                select.appendChild(option);
            });
        });
        
        return types;
    } catch (error) {
        console.error('Error al cargar tipos:', error);
        showAlert('Error al cargar tipos de dispositivo', 'danger');
    }
}

async function saveDevice() {
    if (!requireAdmin()) return;

    const form = document.getElementById('deviceForm');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    
    const deviceId = document.getElementById('deviceId').value;
    const typeId = document.getElementById('deviceType').value;
    const description = document.getElementById('deviceDescription').value;
    const state = document.getElementById('deviceState').value === 'true';
    
    const device = {
        id: deviceId ? parseInt(deviceId) : 0,
        type: { id: parseInt(typeId) },
        deviceDescription: description,
        state: state
    };
    
    const saveBtn = document.getElementById('saveDeviceBtn');
    const originalText = saveBtn.innerHTML;
    saveBtn.disabled = true;
    saveBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Guardando...';
    
    try {
        const response = await adminFetch(`${API_BASE}/devices`, {
            method: deviceId ? 'PUT' : 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(device)
        });
        handleAdminResponse(response);
        
        if (!response.ok) throw new Error('Error al guardar dispositivo');
        
        const modal = bootstrap.Modal.getInstance(document.getElementById('deviceModal'));
        if (modal) modal.hide();
        
        showAlert(deviceId ? 'Dispositivo actualizado correctamente' : 'Dispositivo creado correctamente', 'success');
        loadDevices();
        form.reset();
    } catch (error) {
        showAlert('Error al guardar dispositivo: ' + error.message, 'danger');
        console.error('Error:', error);
    } finally {
        saveBtn.disabled = false;
        saveBtn.innerHTML = originalText;
    }
}

async function editDevice(id) {
    if (!requireAdmin()) return;

    try {
        const response = await fetch(`${API_BASE}/devices/${id}`);
        if (!response.ok) throw new Error('Error al cargar dispositivo');
        
        const device = await response.json();
        
        document.getElementById('deviceModalLabel').innerHTML = '<i class="fas fa-edit me-2"></i>Editar Dispositivo';
        document.getElementById('deviceId').value = device.id;
        document.getElementById('deviceDescription').value = device.deviceDescription || '';
        document.getElementById('deviceState').value = device.state;
        document.getElementById('deviceType').value = device.type ? device.type.id : '';
        
        const modal = new bootstrap.Modal(document.getElementById('deviceModal'));
        modal.show();
    } catch (error) {
        showAlert('Error al cargar dispositivo: ' + error.message, 'danger');
        console.error('Error:', error);
    }
}

async function deleteDevice(id, description) {
    if (!requireAdmin()) return;

    if (!confirm(`¿Está seguro de que desea eliminar el dispositivo "${description}"?\n\nEsta acción no se puede deshacer.`)) {
        return;
    }
    
    try {
        const response = await adminFetch(`${API_BASE}/devices/${id}`, {
            method: 'DELETE'
        });
        handleAdminResponse(response);
        
        if (!response.ok) throw new Error('Error al eliminar dispositivo');
        
        showAlert('Dispositivo eliminado correctamente', 'success');
        loadDevices();
    } catch (error) {
        showAlert('Error al eliminar dispositivo: ' + error.message, 'danger');
        console.error('Error:', error);
    }
}

function openNewDeviceModal() {
    if (!requireAdmin()) return;

    document.getElementById('deviceForm').reset();
    document.getElementById('deviceId').value = '';
    document.getElementById('deviceModalLabel').innerHTML = '<i class="fas fa-plus-circle me-2"></i>Nuevo Dispositivo';
    document.getElementById('deviceState').value = 'true';
    
    const modal = new bootstrap.Modal(document.getElementById('deviceModal'));
    modal.show();
}

// Order Management
async function loadAvailableDevices() {
    const container = document.getElementById('availableDevicesContainer');
    if (!container) return;
    
    container.innerHTML = '<div class="text-center py-3"><span class="spinner-border spinner-border-sm"></span> Cargando...</div>';
    
    try {
        const response = await fetch(`${API_BASE}/devices`);
        if (!response.ok) throw new Error('Error al cargar dispositivos');
        
        const devices = await response.json();
        const available = devices.filter(d => d.state === true);
        
        if (available.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="padding: 2rem 1rem;">
                    <i class="fas fa-inbox"></i>
                    <div class="empty-state-title">Sin dispositivos disponibles</div>
                    <div class="empty-state-text">No hay dispositivos disponibles para crear un pedido</div>
                </div>
            `;
            return;
        }
        
        container.innerHTML = '';
        available.forEach(device => {
            const div = document.createElement('div');
            div.className = 'form-check p-3 mb-2 border rounded';
            div.style.backgroundColor = '#f8fafc';
            div.innerHTML = `
                <input class="form-check-input device-order-checkbox" type="checkbox" value="${device.id}" id="orderDevice${device.id}">
                <label class="form-check-label w-100" for="orderDevice${device.id}" style="cursor: pointer;">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <strong>#${device.id}</strong> - ${escapeHtml(device.deviceDescription)}
                            <br>
                            <small class="text-muted">${escapeHtml(device.type ? device.type.name : '-')}</small>
                        </div>
                        <span class="badge badge-success">Disponible</span>
                    </div>
                </label>
            `;
            container.appendChild(div);
        });
    } catch (error) {
        container.innerHTML = `<div class="alert alert-danger">Error: ${error.message}</div>`;
        console.error('Error:', error);
    }
}

async function createOrder() {
    const checkboxes = document.querySelectorAll('.device-order-checkbox:checked');
    const deviceIds = Array.from(checkboxes).map(cb => parseInt(cb.value));
    
    if (deviceIds.length === 0) {
        showAlert('Seleccione al menos un dispositivo para crear el pedido', 'danger');
        return;
    }
    
    const btn = document.getElementById('createOrderBtn');
    const originalText = btn.innerHTML;
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';
    
    try {
        const response = await fetch(`${API_BASE}/devices/order`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ deviceIds: deviceIds })
        });
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Error al crear pedido');
        }
        
        const receipt = await response.json();
        showReceipt(receipt);
        showAlert('Pedido creado correctamente', 'success');
        loadAvailableDevices();
    } catch (error) {
        showAlert('Error al crear pedido: ' + error.message, 'danger');
        console.error('Error:', error);
    } finally {
        btn.disabled = false;
        btn.innerHTML = originalText;
    }
}

function showReceipt(receipt) {
    const modal = new bootstrap.Modal(document.getElementById('receiptModal'));
    const content = document.getElementById('receiptContent');
    
    let itemsHtml = '';
    if (receipt.items && receipt.items.length > 0) {
        itemsHtml = `
            <div class="table-responsive">
                <table class="table table-sm">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Descripción</th>
                            <th>Tipo</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${receipt.items.map(item => `
                            <tr>
                                <td><strong>#${item.deviceId}</strong></td>
                                <td>${escapeHtml(item.deviceDescription || '-')}</td>
                                <td>${escapeHtml(item.deviceTypeName || '-')}</td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
            <div class="mt-3 p-3 bg-light rounded">
                <strong>Total de items:</strong> <span class="badge bg-primary">${receipt.totalItems || receipt.items.length}</span>
            </div>
        `;
    }
    
    content.innerHTML = `
        <div class="mb-3">
            <h6 class="text-primary-custom mb-2"><i class="fas fa-receipt me-2"></i>Detalles del Recibo</h6>
            <p class="text-muted mb-0">Fecha: ${formatDate()}</p>
        </div>
        ${itemsHtml}
    `;
    
    modal.show();
}

// Utility
function escapeHtml(text) {
    if (text == null) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    updateAdminAuthUi();

    const authForm = document.getElementById('authForm');
    if (authForm) authForm.addEventListener('submit', adminLogin);

    const authLoginBtn = document.getElementById('authLoginBtn');
    if (authLoginBtn) authLoginBtn.addEventListener('click', adminLogin);

    const adminLogoutLink = document.getElementById('adminLogoutLink');
    if (adminLogoutLink) adminLogoutLink.addEventListener('click', adminLogout);

    const authModal = document.getElementById('authModal');
    if (authModal) {
        authModal.addEventListener('show.bs.modal', function() {
            const errorBox = document.getElementById('authError');
            if (errorBox) errorBox.style.display = 'none';
            const passwordEl = document.getElementById('authPassword');
            if (passwordEl) passwordEl.value = '';
        });
    }

    // Set active nav link
    const currentPage = window.location.pathname.split('/').pop() || 'index.html';
    document.querySelectorAll('.nav-link').forEach(link => {
        if (link.getAttribute('href') === currentPage) {
            link.classList.add('active');
        }
    });
    
    // Load devices if on devices page
    if (document.getElementById('devicesTableBody')) {
        loadDevices();
        loadDeviceTypes();
    }
    
    // Load available devices if on order page
    if (document.getElementById('availableDevicesContainer')) {
        loadAvailableDevices();
    }
});

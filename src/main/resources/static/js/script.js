// Custom JavaScript for Category Management System

// Document Ready
document.addEventListener('DOMContentLoaded', function() {
    // Initialize all components
    initializeTooltips();
    initializeAnimations();
    initializeFormValidation();
    initializeSearchFeatures();
    initializeTableFeatures();
    initializePaginationFeatures();
    initializeNotifications();
});

// Initialize Bootstrap Tooltips
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Initialize Animations
function initializeAnimations() {
    // Add fade-in animation to page content
    const pageContent = document.querySelector('main');
    if (pageContent) {
        pageContent.classList.add('fade-in');
    }

    // Add hover effects to cards
    const cards = document.querySelectorAll('.card');
    cards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.transition = 'all 0.3s ease';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });

    // Button hover effects
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
            this.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
        });
        
        button.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '';
        });
    });
}

// Form Validation
function initializeFormValidation() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
                
                // Focus on first invalid field
                const firstInvalid = form.querySelector(':invalid');
                if (firstInvalid) {
                    firstInvalid.focus();
                    firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
            form.classList.add('was-validated');
        });

        // Real-time validation
        const inputs = form.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                validateField(this);
            });
            
            input.addEventListener('input', function() {
                if (this.classList.contains('is-invalid')) {
                    validateField(this);
                }
            });
        });
    });
}

// Validate individual field
function validateField(field) {
    const isValid = field.checkValidity();
    
    if (isValid) {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
    } else {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
    }
    
    // Custom validation messages
    if (field.name === 'name' && field.value.trim().length < 2) {
        setCustomValidity(field, 'Tên category phải có ít nhất 2 ký tự');
    } else if (field.name === 'description' && field.value.length > 500) {
        setCustomValidity(field, 'Mô tả không được vượt quá 500 ký tự');
    } else {
        field.setCustomValidity('');
    }
}

// Set custom validation message
function setCustomValidity(field, message) {
    field.setCustomValidity(message);
    
    let feedback = field.parentNode.querySelector('.invalid-feedback');
    if (!feedback) {
        feedback = document.createElement('div');
        feedback.className = 'invalid-feedback';
        field.parentNode.appendChild(feedback);
    }
    feedback.textContent = message;
}

// Search Features
function initializeSearchFeatures() {
    const searchForm = document.querySelector('form[action*="/categories"]');
    const searchInput = document.querySelector('input[name="keyword"]');
    
    if (searchInput) {
        // Add search icon
        if (!searchInput.parentNode.classList.contains('search-box')) {
            const wrapper = document.createElement('div');
            wrapper.className = 'search-box position-relative';
            searchInput.parentNode.insertBefore(wrapper, searchInput);
            wrapper.appendChild(searchInput);
            
            const icon = document.createElement('i');
            icon.className = 'fas fa-search search-icon';
            wrapper.appendChild(icon);
        }
        
        // Auto-submit after typing (with debounce)
        let searchTimeout;
        searchInput.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            searchTimeout = setTimeout(() => {
                if (this.value.length >= 2 || this.value.length === 0) {
                    if (searchForm) {
                        searchForm.submit();
                    }
                }
            }, 500);
        });
        
        // Clear search
        const clearButton = document.createElement('button');
        clearButton.type = 'button';
        clearButton.className = 'btn btn-outline-secondary btn-sm ms-1';
        clearButton.innerHTML = '<i class="fas fa-times"></i>';
        clearButton.title = 'Xóa tìm kiếm';
        
        clearButton.addEventListener('click', function() {
            searchInput.value = '';
            if (searchForm) {
                searchForm.submit();
            }
        });
        
        if (searchInput.value && !searchInput.parentNode.querySelector('.btn-outline-secondary')) {
            searchInput.parentNode.appendChild(clearButton);
        }
    }
}

// Table Features
function initializeTableFeatures() {
    const table = document.querySelector('.table');
    if (!table) return;
    
    // Row hover effects
    const rows = table.querySelectorAll('tbody tr');
    rows.forEach(row => {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = 'rgba(13, 110, 253, 0.05)';
            this.style.transform = 'scale(1.01)';
            this.style.transition = 'all 0.2s ease';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
            this.style.transform = 'scale(1)';
        });
    });
    
    // Sortable column headers
    const sortHeaders = table.querySelectorAll('th a');
    sortHeaders.forEach(header => {
        header.addEventListener('click', function(e) {
            // Add loading animation
            const icon = this.querySelector('i');
            if (icon) {
                icon.className = 'fas fa-spinner fa-spin';
            }
        });
    });
    
    // Row selection (if needed for bulk operations)
    const checkboxes = table.querySelectorAll('input[type="checkbox"]');
    if (checkboxes.length > 0) {
        const selectAllCheckbox = document.createElement('input');
        selectAllCheckbox.type = 'checkbox';
        selectAllCheckbox.addEventListener('change', function() {
            checkboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
        });
    }
}

// Pagination Features
function initializePaginationFeatures() {
    const pagination = document.querySelector('.pagination');
    if (!pagination) return;
    
    const pageLinks = pagination.querySelectorAll('.page-link');
    pageLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            if (this.parentNode.classList.contains('disabled')) {
                e.preventDefault();
                return;
            }
            
            // Add loading state
            if (!this.parentNode.classList.contains('active')) {
                this.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
            }
        });
    });
    
    // Keyboard navigation for pagination
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey || e.metaKey) {
            switch(e.key) {
                case 'ArrowLeft':
                    e.preventDefault();
                    const prevLink = pagination.querySelector('.page-item:not(.disabled) .page-link[href*="page=' + (currentPage - 1) + '"]');
                    if (prevLink) prevLink.click();
                    break;
                case 'ArrowRight':
                    e.preventDefault();
                    const nextLink = pagination.querySelector('.page-item:not(.disabled) .page-link[href*="page=' + (currentPage + 1) + '"]');
                    if (nextLink) nextLink.click();
                    break;
            }
        }
    });
}

// Notification System
function initializeNotifications() {
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        if (!alert.querySelector('.btn-close')) {
            setTimeout(() => {
                alert.style.opacity = '0';
                setTimeout(() => {
                    alert.remove();
                }, 300);
            }, 5000);
        }
    });
    
    // Enhanced alert animations
    alerts.forEach(alert => {
        alert.style.transform = 'translateX(-100%)';
        alert.style.transition = 'all 0.5s ease';
        
        setTimeout(() => {
            alert.style.transform = 'translateX(0)';
        }, 100);
    });
}

// Utility Functions

// Show loading spinner
function showLoading(element) {
    const spinner = document.createElement('div');
    spinner.className = 'loading-spinner position-absolute top-50 start-50 translate-middle';
    spinner.innerHTML = '<div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div>';
    
    element.style.position = 'relative';
    element.style.opacity = '0.6';
    element.appendChild(spinner);
}

// Hide loading spinner
function hideLoading(element) {
    const spinner = element.querySelector('.loading-spinner');
    if (spinner) {
        spinner.remove();
    }
    element.style.opacity = '1';
}

// Show notification
function showNotification(message, type = 'info', duration = 5000) {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    
    notification.innerHTML = `
        <i class="fas fa-${getIconForType(type)} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.opacity = '0';
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, duration);
}

// Get icon for notification type
function getIconForType(type) {
    switch(type) {
        case 'success': return 'check-circle';
        case 'danger': return 'exclamation-circle';
        case 'warning': return 'exclamation-triangle';
        case 'info': return 'info-circle';
        default: return 'info-circle';
    }
}

// Confirm dialog with custom styling
function confirmAction(message, callback) {
    const modal = document.createElement('div');
    modal.className = 'modal fade';
    modal.innerHTML = `
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-question-circle me-2 text-warning"></i>Xác nhận</h5>
                </div>
                <div class="modal-body">
                    <p class="mb-0">${message}</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <button type="button" class="btn btn-primary confirm-btn">Xác nhận</button>
                </div>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    const bootstrapModal = new bootstrap.Modal(modal);
    bootstrapModal.show();
    
    modal.querySelector('.confirm-btn').addEventListener('click', function() {
        callback();
        bootstrapModal.hide();
    });
    
    modal.addEventListener('hidden.bs.modal', function() {
        modal.remove();
    });
}

// Form data validation
function validateFormData(formData) {
    const errors = [];
    
    if (!formData.name || formData.name.trim().length < 2) {
        errors.push('Tên category phải có ít nhất 2 ký tự');
    }
    
    if (formData.description && formData.description.length > 500) {
        errors.push('Mô tả không được vượt quá 500 ký tự');
    }
    
    return errors;
}

// Format date
function formatDate(date) {
    return new Intl.DateTimeFormat('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    }).format(new Date(date));
}

// Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Scroll to top
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// Add scroll to top button
window.addEventListener('scroll', function() {
    let scrollTopBtn = document.getElementById('scrollTopBtn');
    
    if (!scrollTopBtn) {
        scrollTopBtn = document.createElement('button');
        scrollTopBtn.id = 'scrollTopBtn';
        scrollTopBtn.className = 'btn btn-primary position-fixed';
        scrollTopBtn.style.cssText = 'bottom: 20px; right: 20px; z-index: 1000; border-radius: 50%; width: 50px; height: 50px; display: none;';
        scrollTopBtn.innerHTML = '<i class="fas fa-arrow-up"></i>';
        scrollTopBtn.addEventListener('click', scrollToTop);
        document.body.appendChild(scrollTopBtn);
    }
    
    if (window.pageYOffset > 300) {
        scrollTopBtn.style.display = 'block';
    } else {
        scrollTopBtn.style.display = 'none';
    }
});

// Performance monitoring
window.addEventListener('load', function() {
    const loadTime = performance.timing.loadEventEnd - performance.timing.navigationStart;
    console.log(`Page loaded in ${loadTime}ms`);
    
    if (loadTime > 3000) {
        console.warn('Page load time is slower than expected');
    }
});

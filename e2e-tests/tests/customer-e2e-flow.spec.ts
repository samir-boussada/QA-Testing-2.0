import { test, expect } from '@playwright/test';
import { StorePage } from '../src/pages/StorePage';
import { CartDrawer } from '../src/pages/CartDrawer';
import { CheckoutModal } from '../src/pages/CheckoutModal';
import { AuthModal } from '../src/pages/AuthModal';

test.describe('Customer End-to-End E-Commerce Flow', () => {

  test('E2E: Guest browses catalog, adds product, logs in, and completes checkout', async ({ page }) => {
    const store = new StorePage(page);
    const cart = new CartDrawer(page);
    const checkout = new CheckoutModal(page);
    const auth = new AuthModal(page);

    // 1. Visit Storefront
    await store.goto();

    // 2. Add first product to cart
    await store.addProductToCartByIndex(0);

    // 3. Verify cart drawer opened and contains item
    await expect(cart.drawer).toHaveClass(/open/);
    await expect(cart.cartItems).toHaveCount(1);

    // 4. Apply 10% promo code SAVE10
    await cart.applyPromoCode('SAVE10');
    await expect(cart.discountText).not.toHaveText('-$0.00');

    // 5. Proceed to checkout (requires authentication)
    await cart.proceedToCheckout();

    // 6. Login modal should appear for unauthenticated user
    await expect(auth.loginModal).toHaveClass(/open/);
    await auth.login('customer@novashop.com', 'CustomerPass123!');

    // 7. Proceed to checkout modal
    await store.openCart();
    await cart.proceedToCheckout();
    await expect(checkout.modal).toHaveClass(/open/);

    // 8. Submit shipping details
    await checkout.fillAndSubmit('42 Enterprise Boulevard, Suite 500, Paris', '+33612345678');

    // 9. Verify Order Confirmation with generated reference code
    const orderRef = await checkout.expectOrderConfirmed();
    expect(orderRef).toMatch(/^ORD-[A-Z0-9]{8}$/);
  });
});

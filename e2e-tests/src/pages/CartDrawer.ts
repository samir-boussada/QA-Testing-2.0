import { Page, Locator, expect } from '@playwright/test';

export class CartDrawer {
  readonly page: Page;
  readonly drawer: Locator;
  readonly cartItems: Locator;
  readonly subtotalText: Locator;
  readonly discountText: Locator;
  readonly totalText: Locator;
  readonly promoInput: Locator;
  readonly applyPromoBtn: Locator;
  readonly checkoutBtn: Locator;

  constructor(page: Page) {
    this.page = page;
    this.drawer = page.locator('#cart-drawer');
    this.cartItems = page.locator('.cart-item');
    this.subtotalText = page.locator('#cart-subtotal');
    this.discountText = page.locator('#cart-discount');
    this.totalText = page.locator('#cart-total');
    this.promoInput = page.locator('#promo-code-input');
    this.applyPromoBtn = page.locator('button:has-text("Apply")');
    this.checkoutBtn = page.locator('#checkout-drawer-btn');
  }

  async applyPromoCode(code: string) {
    await this.promoInput.fill(code);
    await this.applyPromoBtn.click();
  }

  async proceedToCheckout() {
    await this.checkoutBtn.click();
  }

  async close() {
    await this.page.locator('#cart-overlay').click({ force: true });
  }
}

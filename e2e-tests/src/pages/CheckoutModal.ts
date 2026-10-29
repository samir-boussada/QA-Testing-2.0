import { Page, Locator, expect } from '@playwright/test';

export class CheckoutModal {
  readonly page: Page;
  readonly modal: Locator;
  readonly addressInput: Locator;
  readonly phoneInput: Locator;
  readonly confirmBtn: Locator;
  readonly confModal: Locator;
  readonly confOrderRef: Locator;

  constructor(page: Page) {
    this.page = page;
    this.modal = page.locator('#checkout-modal');
    this.addressInput = page.locator('#checkout-address');
    this.phoneInput = page.locator('#checkout-phone');
    this.confirmBtn = page.locator('#confirm-order-btn');
    this.confModal = page.locator('#confirmation-modal');
    this.confOrderRef = page.locator('#conf-order-id');
  }

  async fillAndSubmit(address: string, phone: string) {
    await this.addressInput.fill(address);
    await this.phoneInput.fill(phone);
    await this.confirmBtn.click();
  }

  async expectOrderConfirmed() {
    await expect(this.confModal).toBeVisible();
    await expect(this.confOrderRef).not.toBeEmpty();
    return await this.confOrderRef.innerText();
  }
}

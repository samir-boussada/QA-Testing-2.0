import { Page, Locator, expect } from '@playwright/test';

export class AuthModal {
  readonly page: Page;
  readonly loginModal: Locator;
  readonly loginEmailInput: Locator;
  readonly loginPasswordInput: Locator;
  readonly loginSubmitBtn: Locator;
  readonly userNavText: Locator;

  constructor(page: Page) {
    this.page = page;
    this.loginModal = page.locator('#login-modal');
    this.loginEmailInput = page.locator('#login-email');
    this.loginPasswordInput = page.locator('#login-password');
    this.loginSubmitBtn = page.locator('#login-form button[type="submit"]');
    this.userNavText = page.locator('#auth-nav-container');
  }

  async login(email: string, pass: string) {
    await this.loginEmailInput.fill(email);
    await this.loginPasswordInput.fill(pass);
    await this.loginSubmitBtn.click();
    await expect(this.userNavText).toContainText(email.split('@')[0], { ignoreCase: true });
  }
}

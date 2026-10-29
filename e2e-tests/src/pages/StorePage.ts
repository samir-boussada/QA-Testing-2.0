import { Page, Locator, expect } from '@playwright/test';

export class StorePage {
  readonly page: Page;
  readonly searchInput: Locator;
  readonly cartBadgeCount: Locator;
  readonly cartDrawerBtn: Locator;
  readonly productCards: Locator;

  constructor(page: Page) {
    this.page = page;
    this.searchInput = page.locator('#search-input');
    this.cartBadgeCount = page.locator('#cart-badge-count');
    this.cartDrawerBtn = page.locator('#cart-drawer-toggle');
    this.productCards = page.locator('.product-card');
  }

  async goto() {
    await this.page.goto('/');
    await expect(this.page).toHaveTitle(/NovaShop/);
  }

  async searchProduct(keyword: string) {
    await this.searchInput.fill(keyword);
  }

  async filterByCategory(categoryName: string) {
    await this.page.locator(`.cat-chip:has-text("${categoryName}")`).click();
  }

  async addProductToCartByIndex(index: number = 0) {
    const card = this.productCards.nth(index);
    const title = await card.locator('.product-title').innerText();
    const btn = card.locator('.add-to-cart-btn');
    await btn.click();
    return title;
  }

  async openCart() {
    await this.cartDrawerBtn.click();
  }
}

import { test, expect } from '@playwright/test';
import { StorePage } from '../src/pages/StorePage';
import { CartDrawer } from '../src/pages/CartDrawer';

test.describe('Catalog Search and Cart Inventory Validation', () => {

  test('Filter products by category and verify matching results', async ({ page }) => {
    const store = new StorePage(page);
    await store.goto();

    // Filter by Audio category
    await store.filterByCategory('Audio');
    await page.waitForTimeout(300);

    const cards = store.productCards;
    const count = await cards.count();
    expect(count).toBeGreaterThan(0);

    for (let i = 0; i < count; i++) {
      await expect(cards.nth(i).locator('.category-tag')).toHaveText('Audio');
    }
  });

  test('Search for specific product name', async ({ page }) => {
    const store = new StorePage(page);
    await store.goto();

    await store.searchProduct('Keyboard');
    await page.waitForTimeout(300);

    await expect(store.productCards).toHaveCount(1);
    await expect(store.productCards.first().locator('.product-title')).toContainText('Keyboard');
  });

  test('Update quantity and remove item from cart drawer', async ({ page }) => {
    const store = new StorePage(page);
    const cart = new CartDrawer(page);

    await store.goto();
    await store.addProductToCartByIndex(0);
    await expect(cart.cartItems).toHaveCount(1);

    // Increase quantity
    await page.locator('.qty-btn:has-text("+")').click();
    await expect(page.locator('.qty-controls span')).toHaveText('2');

    // Remove from cart
    await page.locator('button:has-text("Remove")').click();
    await expect(page.locator('#cart-items-container')).toContainText('Your cart is currently empty');
    await expect(page.locator('#checkout-drawer-btn')).toBeDisabled();
  });
});

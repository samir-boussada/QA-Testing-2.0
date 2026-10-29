import { test, expect } from '@playwright/test';
import { AuthModal } from '../src/pages/AuthModal';

test.describe('Admin Management and Inventory Control', () => {

  test('Admin logs in and accesses dashboard metrics and products', async ({ page }) => {
    await page.goto('/');

    // 1. Sign in as Admin
    await page.locator('button:has-text("Sign In")').click();
    const auth = new AuthModal(page);
    await auth.login('admin@novashop.com', 'AdminPass123!');

    // 2. Click Admin Portal button in header
    await page.locator('a:has-text("Admin Portal")').click();
    await expect(page).toHaveURL(/\/admin\.html/);

    // 3. Verify statistics cards render with data
    await expect(page.locator('#stat-revenue')).toBeVisible();
    await expect(page.locator('#stat-products')).not.toHaveText('0');

    // 4. Verify products inventory table is populated
    await expect(page.locator('#admin-products-table tr')).not.toHaveCount(0);
  });
});

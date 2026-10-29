package com.novashop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CheckoutRequest {

    @NotBlank(message = "Shipping address is required")
    @Size(min = 5, max = 250, message = "Shipping address must be between 5 and 250 characters")
    private String shippingAddress;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{8,18}$", message = "Phone number must be between 8 and 18 numeric digits")
    private String contactPhone;

    private String promoCode;

    public CheckoutRequest() {}

    public CheckoutRequest(String shippingAddress, String contactPhone, String promoCode) {
        this.shippingAddress = shippingAddress;
        this.contactPhone = contactPhone;
        this.promoCode = promoCode;
    }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }
}

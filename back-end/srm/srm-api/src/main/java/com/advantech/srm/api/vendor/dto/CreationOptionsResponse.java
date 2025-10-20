package com.advantech.srm.api.vendor.dto;

import java.util.List;

public record CreationOptionsResponse(
    List<DropdownOptionDto> vendorTypes,
    List<DropdownOptionDto> partNumberCategories,
    List<DropdownOptionDto> countries,
    List<DropdownOptionDto> purchasingOrganizations,
    List<DropdownOptionDto> currencies,
    List<DropdownOptionDto> paymentTerms,
    List<DropdownOptionDto> incoterms,
    List<DropdownOptionDto> orderTypes) {
}

export interface DropdownOptionDTO {
  label: string
  value: string
}

export interface CreationOptions {
  vendorTypes: DropdownOptionDTO[]
  partNumberCategories: DropdownOptionDTO[]
  countries: DropdownOptionDTO[]
  purchasingOrganizations: DropdownOptionDTO[]
  currencies: DropdownOptionDTO[]
  paymentTerms: DropdownOptionDTO[]
  incoterms: DropdownOptionDTO[]
  orderTypes: DropdownOptionDTO[]
}

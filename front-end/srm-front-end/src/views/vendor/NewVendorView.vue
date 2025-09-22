<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  NCard,
  NTabs,
  NTabPane,
  NForm,
  NFormItem,
  NInput,
  NSelect,
  NSwitch,
  NGrid,
  NGi
} from 'naive-ui'
import { fetchCreationOptions } from '@/services/vendorService'
import type { DropdownOptionDTO, CreationOptions } from '@/types/vendor'

const activeTab = ref('purchasing')

const purchasingFormValue = ref({
  groupKey: '',
  vendorType: null,
  manufacturerCode: '',
  companyName: '',
  companyName2: '',
  companyName3: '',
  companyName4: '',
  companyAbbreviation: '',
  country: null,
  unifiedBusinessNumber: '',
  primaryContactPersonFirstName: '',
  primaryContactPersonLastName: '',
  email: '',
  companyPhoneAreaCode: '',
  companyPhoneNumber: '',
  companyPhoneExtension: '',
  mobilePhone: '',
  transactionMaterialType: null,
  foreignVendor: false,
  exemptFromEvaluation: false,
  purchasingOrganization: null,
  transactionCurrency: null,
  paymentTerms: null,
  paymentTermsRemark: '',
  incoterms: '',
  incoterms2: '',
  transactionOrderType: null
})

const creationOptions = ref<CreationOptions | null>(null)

const vendorTypeOptions = ref<DropdownOptionDTO[]>([])
const countryOptions = ref<DropdownOptionDTO[]>([])
const transactionMaterialTypeOptions = ref<DropdownOptionDTO[]>([])
const purchasingOrganizationOptions = ref<DropdownOptionDTO[]>([])
const transactionCurrencyOptions = ref<DropdownOptionDTO[]>([])
const paymentTermsOptions = ref<DropdownOptionDTO[]>([])
const transactionOrderTypeOptions = ref<DropdownOptionDTO[]>([])

const loadCreationOptions = async () => {
  try {
    const data = await fetchCreationOptions()
    creationOptions.value = data
    vendorTypeOptions.value = data.vendorTypes
    countryOptions.value = data.countries
    transactionMaterialTypeOptions.value = data.partNumberCategories
    purchasingOrganizationOptions.value = data.purchasingOrganizations
    transactionCurrencyOptions.value = data.currencies
    paymentTermsOptions.value = data.paymentTerms
    transactionOrderTypeOptions.value = data.orderTypes
  } catch (error) {
    console.error('Error loading creation options:', error)
  }
}

onMounted(() => {
  loadCreationOptions()
})

</script>

<template>
  <div class="p-4">
    <n-card title="新增供應商">
      <n-tabs v-model:value="activeTab" type="line" animated>
        <n-tab-pane name="purchasing" tab="採購">
          <div class="p-4">
            <n-form
              :model="purchasingFormValue"
              label-placement="left"
              :label-width="180"
              :style="{ maxWidth: '1200px' }"
            >
              <n-grid :cols="2" :x-gap="24">
                <n-gi>
                  <n-form-item label="GroupKey" path="groupKey">
                    <n-input v-model:value="purchasingFormValue.groupKey" placeholder="GroupKey" />
                  </n-form-item>
                  <n-form-item label="供應商類別" path="vendorType">
                    <n-select
                      v-model:value="purchasingFormValue.vendorType"
                      :options="vendorTypeOptions"
                      placeholder="請選擇供應商類別"
                      filterable
                    />
                  </n-form-item>
                  <n-form-item label="製造商代碼" path="manufacturerCode">
                    <n-input
                      v-model:value="purchasingFormValue.manufacturerCode"
                      placeholder="製造商代碼"
                    />
                  </n-form-item>
                  <n-form-item label="公司名稱" path="companyName">
                    <n-input v-model:value="purchasingFormValue.companyName" placeholder="公司名稱" />
                  </n-form-item>
                  <n-form-item label="公司名稱2" path="companyName2">
                    <n-input v-model:value="purchasingFormValue.companyName2" placeholder="公司名稱2" />
                  </n-form-item>
                  <n-form-item label="公司名稱3" path="companyName3">
                    <n-input v-model:value="purchasingFormValue.companyName3" placeholder="公司名稱3" />
                  </n-form-item>
                  <n-form-item label="公司名稱4" path="companyName4">
                    <n-input v-model:value="purchasingFormValue.companyName4" placeholder="公司名稱4" />
                  </n-form-item>
                  <n-form-item label="公司簡稱" path="companyAbbreviation">
                    <n-input
                      v-model:value="purchasingFormValue.companyAbbreviation"
                      placeholder="公司簡稱"
                    />
                  </n-form-item>
                  <n-form-item label="國別" path="country">
                    <n-select
                      v-model:value="purchasingFormValue.country"
                      :options="countryOptions"
                      placeholder="請選擇國別"
                      filterable
                    />
                  </n-form-item>
                  <n-form-item label="統一編號/統一社會信用代碼" path="unifiedBusinessNumber">
                    <n-input
                      v-model:value="purchasingFormValue.unifiedBusinessNumber"
                      placeholder="統一編號/統一社會信用代碼"
                    />
                  </n-form-item>
                  <n-form-item label="主要聯絡人">
                    <n-space>
                      <n-input
                        v-model:value="purchasingFormValue.primaryContactPersonFirstName"
                        placeholder="姓"
                        style="width: 150px"
                      />
                      <n-input
                        v-model:value="purchasingFormValue.primaryContactPersonLastName"
                        placeholder="名"
                        style="width: 150px"
                      />
                    </n-space>
                  </n-form-item>
                </n-gi>
                <n-gi>
                  <n-form-item label="電子郵箱" path="email">
                    <n-input v-model:value="purchasingFormValue.email" placeholder="電子郵箱" />
                  </n-form-item>
                  <n-form-item label="公司電話">
                    <n-space>
                      <n-input
                        v-model:value="purchasingFormValue.companyPhoneAreaCode"
                        placeholder="區碼"
                        style="width: 80px"
                      />
                      <n-input
                        v-model:value="purchasingFormValue.companyPhoneNumber"
                        placeholder="號碼"
                        style="width: 150px"
                      />
                      <n-input
                        v-model:value="purchasingFormValue.companyPhoneExtension"
                        placeholder="分機"
                        style="width: 80px"
                      />
                    </n-space>
                  </n-form-item>
                  <n-form-item label="行動電話" path="mobilePhone">
                    <n-input v-model:value="purchasingFormValue.mobilePhone" placeholder="行動電話" />
                  </n-form-item>
                  <n-form-item label="交易料號類別" path="transactionMaterialType">
                    <n-select
                      v-model:value="purchasingFormValue.transactionMaterialType"
                      :options="transactionMaterialTypeOptions"
                      placeholder="請選擇交易料號類別"
                      filterable
                    />
                  </n-form-item>
                  <n-form-item label="國外廠商" path="foreignVendor">
                    <n-switch v-model:value="purchasingFormValue.foreignVendor" />
                  </n-form-item>
                  <n-form-item label="豁免評鑑" path="exemptFromEvaluation">
                    <n-switch v-model:value="purchasingFormValue.exemptFromEvaluation" />
                  </n-form-item>
                  <n-form-item label="採購組織" path="purchasingOrganization">
                    <n-select
                      v-model:value="purchasingFormValue.purchasingOrganization"
                      :options="purchasingOrganizationOptions"
                      placeholder="請選擇採購組織"
                      filterable
                    />
                  </n-form-item>
                  <n-form-item label="交易幣別" path="transactionCurrency">
                    <n-select
                      v-model:value="purchasingFormValue.transactionCurrency"
                      :options="transactionCurrencyOptions"
                      placeholder="請選擇交易幣別"
                      filterable
                    />
                  </n-form-item>
                  <n-form-item label="付款條件" path="paymentTerms">
                    <n-select
                      v-model:value="purchasingFormValue.paymentTerms"
                      :options="paymentTermsOptions"
                      placeholder="請選擇付款條件"
                      filterable
                    />
                  </n-form-item>
                  <n-form-item label="付款條件備註" path="paymentTermsRemark">
                    <n-input
                      v-model:value="purchasingFormValue.paymentTermsRemark"
                      placeholder="付款條件備註"
                    />
                  </n-form-item>
                  <n-form-item label="國際貿易術語" path="incoterms">
                    <n-input v-model:value="purchasingFormValue.incoterms" placeholder="國際貿易術語" />
                  </n-form-item>
                  <n-form-item label="國際貿易術語2" path="incoterms2">
                    <n-input v-model:value="purchasingFormValue.incoterms2" placeholder="國際貿易術語2" />
                  </n-form-item>
                  <n-form-item label="交易訂單類型" path="transactionOrderType">
                    <n-select
                      v-model:value="purchasingFormValue.transactionOrderType"
                      :options="transactionOrderTypeOptions"
                      placeholder="請選擇交易訂單類型"
                      filterable
                    />
                  </n-form-item>
                </n-gi>
              </n-grid>
            </n-form>
          </div>
        </n-tab-pane>
        <n-tab-pane name="finance" tab="財會">
          <div class="p-4">
            <h2>財會資訊</h2>
            <p>這裡將放置財會相關的表單和資訊。</p>
          </div>
        </n-tab-pane>
        <n-tab-pane name="quality" tab="品質">
          <div class="p-4">
            <h2>品質資訊</h2>
            <p>這裡將放置品質相關的表單和資訊。</p>
          </div>
        </n-tab-pane>
      </n-tabs>
    </n-card>
  </div>
</template>

<style scoped>
/* Add any specific styles for NewVendorView here */
</style>
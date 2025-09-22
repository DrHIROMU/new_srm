<script setup lang="ts">
import { ref, h } from 'vue'
import { useRouter } from 'vue-router'
import {
  NGrid,
  NGi,
  NForm,
  NFormItem,
  NInput,
  NSelect,
  NButton,
  NSpace,
  NCard,
  NDataTable
} from 'naive-ui'

const formValue = ref({
  vendorNameOrCode: '',
  status: null,
  unifiedBusinessNumber: ''
})

const statusOptions = [
  { label: '已建立', value: 'created' },
  { label: '處理中', value: 'processing' },
  { label: '已完成', value: 'completed' }
]

const columns = [
  {
    title: '供應商名稱',
    key: 'vendorName'
  },
  {
    title: '國別',
    key: 'country'
  },
  {
    title: '統一編號',
    key: 'unifiedBusinessNumber'
  },
  {
    title: '採購組織',
    key: 'purchasingOrganization'
  },
  {
    title: '供應商類別',
    key: 'vendorCategory'
  },
  {
    title: '主要聯絡人',
    key: 'primaryContactPerson'
  },
  {
    title: 'E-mail',
    key: 'email'
  },
  {
    title: '公司電話',
    key: 'companyPhone'
  },
  {
    title: '動作',
    key: 'actions',
    render(row) {
      return h(
        NButton,
        {
          size: 'small',
          onClick: () => handleDelete(row)
        },
        { default: () => '刪除' }
      )
    }
  }
]

const data = ref([
  {
    key: 0,
    vendorName: '供應商A',
    country: '台灣',
    unifiedBusinessNumber: '12345678',
    purchasingOrganization: '採購部A',
    vendorCategory: '電子元件',
    primaryContactPerson: '陳先生',
    email: 'mr.chen@vendor-a.com',
    companyPhone: '02-12345678',
  },
  {
    key: 1,
    vendorName: '供應商B',
    country: '中國',
    unifiedBusinessNumber: '87654321',
    purchasingOrganization: '採購部B',
    vendorCategory: '機械設備',
    primaryContactPerson: '林小姐',
    email: 'ms.lin@vendor-b.com',
    companyPhone: '021-87654321',
  },
  {
    key: 2,
    vendorName: '供應商C',
    country: '日本',
    unifiedBusinessNumber: '11223344',
    purchasingOrganization: '採購部C',
    vendorCategory: '軟體服務',
    primaryContactPerson: '黃先生',
    email: 'mr.huang@vendor-c.com',
    companyPhone: '03-11223344',
  }
])

function handleSearch() {
  console.log('Search', formValue.value)
}

function handleReset() {
  formValue.value = {
    vendorNameOrCode: '',
    status: null,
    unifiedBusinessNumber: ''
  }
}

const router = useRouter()

function handleDelete(row) {
  console.log('Delete', row)
  // Implement actual delete logic here
}

function handleCreateNewVendor() {
  router.push('/vendor/new')
}
</script>

<template>
  <div class="p-4">
    <n-card title="邀請供應商">
      <div class="search-block mb-4 p-4 border border-gray-200 rounded">
        <n-form :model="formValue" label-placement="left" label-width="auto">
          <n-grid :cols="24" :x-gap="24">
            <n-gi :span="8">
              <n-form-item label="供應商名稱/代碼">
                <n-input
                  v-model:value="formValue.vendorNameOrCode"
                  placeholder="請輸入供應商名稱或代碼"
                />
              </n-form-item>
            </n-gi>
            <n-gi :span="8">
              <n-form-item label="建立狀態">
                <n-select
                  v-model:value="formValue.status"
                  :options="statusOptions"
                  placeholder="請選擇狀態"
                />
              </n-form-item>
            </n-gi>
            <n-gi :span="8">
              <n-form-item label="統一編號">
                <n-input
                  v-model:value="formValue.unifiedBusinessNumber"
                  placeholder="請輸入統一編號"
                />
              </n-form-item>
            </n-gi>
          </n-grid>
        </n-form>
        <n-space justify="end">
          <n-button type="primary" @click="handleSearch">查詢</n-button>
          <n-button @click="handleReset">重設</n-button>
        </n-space>
      </div>
      <div class="action-block mb-4">
        <n-space>
          <n-button>匯出報表</n-button>
          <n-button type="primary" @click="handleCreateNewVendor">新增供應商</n-button>
        </n-space>
      </div>
      <n-data-table :columns="columns" :data="data" :pagination="false" :bordered="false" />
    </n-card>
  </div>
</template>
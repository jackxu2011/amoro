
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
/-->

<template>
  <div class="detail-wrap">
    <div class="detail-content-wrap">
      <div class="content-wrap">
        <a-form ref="formRef" :model="formState" class="catalog-form">
          <a-form-item>
            <p class="header">{{$t('basic')}}</p>
          </a-form-item>
          <a-form-item :label="$t('name')" :name="['catalog', 'name']" :rules="[{ required: isEdit && isNewCatalog, validator: validatorName }]">
            <a-input v-if="isEdit && isNewCatalog" v-model:value="formState.catalog.name" />
            <span v-else class="config-value">{{formState.catalog.name}}</span>
          </a-form-item>
          <a-form-item :label="$t('type')" :name="['catalog', 'typeshow']">
            <a-select
              v-if="isEdit && isNewCatalog"
              v-model:value="formState.catalog.typeshow"
              :options="typwShowOptions"
              :placeholder="placeholder.selectPh"
              @change="changeTypeShow"
            />
            <span v-else>{{formState.catalog.typeshow}}</span>
          </a-form-item>
          <a-form-item :label="$t('metastore')" v-if="formState.catalog.typeshow === typeShowMap['External Catalog']" :name="['catalog', 'type']" :rules="[{ required: isEdit && isNewCatalog }]">
            <a-select
              v-if="isEdit && isNewCatalog"
              v-model:value="formState.catalog.type"
              :options="catalogTypeOps"
              :placeholder="placeholder.selectPh"
              @change="changeMetastore"
            />
            <span v-else>{{metastoreType}}</span>
          </a-form-item>
          <a-form-item :label="$t('tableFormat')" :name="['tableFormat']" :rules="[{ required: isEdit && isNewCatalog }]">
            <a-radio-group :disabled="!isEdit || !isNewCatalog" v-model:value="formState.tableFormat" name="radioGroup" @change="changeTableFormat">
              <a-radio v-for="item in formatOptions" :key="item" :value="item">{{tableFormatText[item]}}</a-radio>
            </a-radio-group>
          </a-form-item>
          <a-form-item :label="$t('optimizerGroup')" :name="['catalog', 'optimizerGroup']" :rules="[{ required: isEdit }]">
            <a-select
              v-if="isEdit"
              v-model:value="formState.catalog.optimizerGroup"
              :options="optimizerGroupList"
              :placeholder="placeholder.selectPh"
            />
            <span v-else>{{formState.catalog.optimizerGroup}}</span>
          </a-form-item>
          <a-form-item>
            <p class="header">{{$t('storageConfigName')}}</p>
          </a-form-item>
          <a-form-item label="Type" :name="['storageConfig', 'storage.type']" :rules="[{ required: isEdit }]">
            <a-select
              v-if="isEdit"
              v-model:value="formState.storageConfig['storage.type']"
              :placeholder="placeholder.selectPh"
              :options="storageConfigTypeOps"
            />
            <span v-else class="config-value">{{formState.storageConfig['storage.type']}}</span>
          </a-form-item>
          <a-form-item v-if="formState.storageConfig['storage.type'] === 'S3'" label="Endpoint" :name="['storageConfig', 'storage.s3.endpoint']" :rules="[{ required: false }]">
            <a-input v-if="isEdit" v-model:value="formState.storageConfig['storage.s3.endpoint']" />
            <span v-else class="config-value">{{formState.storageConfig['storage.s3.endpoint']}}</span>
          </a-form-item>
          <a-form-item v-if="formState.storageConfig['storage.type'] === 'S3'" label="Region" :name="['storageConfig', 'storage.s3.region']" :rules="[{ required: false }]">
            <a-input v-if="isEdit" v-model:value="formState.storageConfig['storage.s3.region']" />
            <span v-else class="config-value">{{formState.storageConfig['storage.s3.region']}}</span>
          </a-form-item>
          <div v-if="formState.storageConfig['storage.type'] === 'Hadoop'">
            <a-form-item
              v-for="config in formState.storageConfigArray"
              :key="config.label"
              :label="config.label"
              class="g-flex-ac">
              <a-upload
                v-if="isEdit"
                v-model:file-list="config.fileList"
                name="file"
                accept=".xml"
                :showUploadList="false"
                :action="uploadUrl"
                :disabled="config.uploadLoading"
                @change="(args) => uploadFile(args, config, 'STORAGE')"
              >
                <a-button type="primary" ghost :loading="config.uploadLoading" class="g-mr-12">{{ $t('upload') }}</a-button>
              </a-upload>
              <span v-if="config.isSuccess || config.fileName" class="config-value" :class="{ 'view-active': !!config.fileUrl }" @click="viewFileDetail(config.fileUrl)">{{ config.fileName }}</span>
            </a-form-item>
          </div>
          <a-form-item>
            <p class="header">{{$t('authenticationConfig')}}</p>
          </a-form-item>
          <a-form-item label="Type" :name="['authConfig', 'auth.type']" :rules="[{ required: isEdit }]">
            <a-select
              v-if="isEdit"
              v-model:value="formState.authConfig['auth.type']"
              :placeholder="placeholder.selectPh"
              :options="authTypeOptions"
            />
            <span v-else class="config-value">{{formState.authConfig['auth.type']}}</span>
          </a-form-item>
          <a-form-item v-if="formState.authConfig['auth.type'] === 'SIMPLE'" label="Hadoop Username" :name="['authConfig', 'auth.simple.hadoop_username']" :rules="[{ required: isEdit }]">
            <a-input v-if="isEdit" v-model:value="formState.authConfig['auth.simple.hadoop_username']" />
            <span v-else class="config-value">{{formState.authConfig['auth.simple.hadoop_username']}}</span>
          </a-form-item>
          <a-form-item v-if="formState.authConfig['auth.type'] === 'KERBEROS'" label="Kerberos Principal" :name="['authConfig', 'auth.kerberos.principal']" :rules="[{ required: isEdit }]">
            <a-input v-if="isEdit" v-model:value="formState.authConfig['auth.kerberos.principal']" />
            <span v-else class="config-value">{{formState.authConfig['auth.kerberos.principal']}}</span>
          </a-form-item>
          <div v-if="formState.authConfig['auth.type'] === 'KERBEROS'">
            <a-form-item
              v-for="config in formState.authConfigArray"
              :key="config.label"
              :label="config.label"
              class="g-flex-ac">
              <a-upload
                v-if="isEdit"
                v-model:file-list="config.fileList"
                name="file"
                :accept="config.key === 'auth.kerberos.keytab' ? '.keytab' : '.conf'"
                :showUploadList="false"
                :action="uploadUrl"
                :disabled="config.uploadLoading"
                @change="(args) => uploadFile(args, config)"
              >
                <a-button type="primary" ghost :loading="config.uploadLoading" class="g-mr-12">{{$t('upload')}}</a-button>
              </a-upload>
              <span v-if="config.isSuccess || config.fileName" class="config-value auth-filename" :class="{'view-active': !!config.fileUrl}" @click="viewFileDetail(config.fileUrl)" :title="config.fileName">{{config.fileName}}</span>
            </a-form-item>
          </div>
          <a-form-item v-if="formState.authConfig['auth.type'] === 'AK/SK'" label="Access Key" :name="['authConfig', 'auth.ak_sk.access_key']" :rules="[{ required: isEdit }]">
            <a-input v-if="isEdit" v-model:value="formState.authConfig['auth.ak_sk.access_key']" />
            <span v-else class="config-value">{{formState.authConfig['auth.ak_sk.access_key']}}</span>
          </a-form-item>
          <a-form-item v-if="formState.authConfig['auth.type'] === 'AK/SK'" label="Secret Key" :name="['authConfig', 'auth.ak_sk.secret_key']" :rules="[{ required: isEdit }]">
            <a-input v-if="isEdit" v-model:value="formState.authConfig['auth.ak_sk.secret_key']" />
            <span v-else class="config-value">{{formState.authConfig['auth.ak_sk.secret_key']}}</span>
          </a-form-item>
          <a-form-item>
            <p class="header">{{$t('properties')}}</p>
          </a-form-item>
          <a-form-item>
            <Properties :propertiesObj="formState.properties" :isEdit="isEdit" ref="propertiesRef" />
          </a-form-item>
          <a-form-item>
            <p class="header">{{$t('tableProperties')}}</p>
          </a-form-item>
          <a-form-item>
            <Properties :propertiesObj="formState.tableProperties" :isEdit="isEdit" ref="tablePropertiesRef" />
          </a-form-item>
        </a-form>
      </div>
    </div>
    <div v-if="isEdit" class="footer-btn">
      <a-button type="primary" @click="handleSave" class="save-btn g-mr-12">{{$t('save')}}</a-button>
      <a-button @click="handleCancle">{{$t('cancel')}}</a-button>
    </div>
    <div v-if="!isEdit" class="footer-btn">
      <a-button type="primary" @click="handleEdit" class="edit-btn g-mr-12">{{$t('edit')}}</a-button>
      <a-button @click="handleRemove" class="remove-btn">{{$t('remove')}}</a-button>
    </div>
    <u-loading v-if="loading" />
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { getCatalogsTypes, getCatalogsSetting, saveCatalogsSetting, checkCatalogStatus, delCatalog } from '@/services/setting.services'
import { ILableAndValue, ICatalogItem, IMap, IIOptimizeGroupItem } from '@/types/common.type'
import { Modal, message, UploadChangeParam } from 'ant-design-vue'
import { useI18n } from 'vue-i18n'
import Properties from './Properties.vue'
import { usePlaceholder } from '@/hooks/usePlaceholder'
import { useRoute } from 'vue-router'
import { getResourceGroupsListAPI } from '@/services/optimize.service'

interface IStorageConfigItem {
  label: string
  value: string
  key: string
  fileName: string
  fileUrl: string
  fileId: string
  fileList: string[]
  uploadLoading: boolean
  isSuccess: boolean
}

interface FormState {
  catalog: IMap<string | undefined>
  tableFormat: string
  storageConfig: IMap<string>
  authConfig: IMap<string>
  properties: IMap<string>
  tableProperties: IMap<string>
  storageConfigArray: IStorageConfigItem[]
  authConfigArray: IStorageConfigItem[]
}

const typeShowMap = { 'Internal Catalog': 'Internal Catalog', 'External Catalog': 'External Catalog' }

const props = defineProps<{ isEdit: boolean }>()
const emit = defineEmits<{
 (e: 'updateEdit', val: boolean, catalog?: ICatalogItem): void,
 (e: 'updateCatalogs'): void
}>()

const { t } = useI18n()
const route = useRoute()
const placeholder = reactive(usePlaceholder())
const metastoreType = ref<string>('')
const isEdit = computed(() => {
  return props.isEdit
})
const uploadUrl = computed(() => {
  return '/ams/v1/files'
})
const isNewCatalog = computed(() => {
  const catalog = (route.query?.catalogname || '').toString()
  return decodeURIComponent(catalog) === 'new catalog'
})
// Arctic Metastore supports Mixed Iceberg format only.
// Hive Metastore supports Iceberg and Mixed Hive format only.
// Hadoop and Custom support Iceberg format only.
const isHiveMetastore = computed(() => {
  return formState.catalog.type === 'hive'
})
const isArcticMetastore = computed(() => {
  return formState.catalog.type === 'ams'
})
const loading = ref<boolean>(false)
const formRef = ref()
const propertiesRef = ref()
const tablePropertiesRef = ref()
// ICEBERG  MIXED_HIVE  MIXED_ICEBERG
const tableFormatMap = {
  MIXED_HIVE: 'MIXED_HIVE',
  ICEBERG: 'ICEBERG',
  MIXED_ICEBERG: 'MIXED_ICEBERG',
  PAIMON: 'PAIMON'
}

const tableFormatText = {
  [tableFormatMap.ICEBERG]: 'Iceberg',
  [tableFormatMap.MIXED_HIVE]: 'Mixed Hive',
  [tableFormatMap.MIXED_ICEBERG]: 'Mixed Iceberg',
  [tableFormatMap.PAIMON]: 'Paimon'
}
const storeSupportFormat: {[prop:string]: string[]} = {
  ams: [tableFormatMap.MIXED_ICEBERG, tableFormatMap.ICEBERG],
  hive: [tableFormatMap.MIXED_HIVE, tableFormatMap.MIXED_ICEBERG, tableFormatMap.ICEBERG, tableFormatMap.PAIMON],
  hadoop: [tableFormatMap.MIXED_ICEBERG, tableFormatMap.ICEBERG, tableFormatMap.PAIMON],
  glue: [tableFormatMap.MIXED_ICEBERG, tableFormatMap.ICEBERG],
  custom: [tableFormatMap.MIXED_ICEBERG, tableFormatMap.ICEBERG]
}

const storageConfigFileNameMap = {
  'hadoop.core.site': 'core-site.xml',
  'hadoop.hdfs.site': 'hdfs-site.xml',
  'hive.site': 'hive-site.xml'
}
const newCatalogConfig = {
  storageConfig: {
    'hadoop.core.site': '',
    'hadoop.hdfs.site': ''
  },
  authConfig: {
    'auth.kerberos.keytab': '',
    'auth.kerberos.krb5': ''
  }
}
const typwShowOptions = ref([
  { label: typeShowMap['Internal Catalog'], value: typeShowMap['Internal Catalog'] },
  { label: typeShowMap['External Catalog'], value: typeShowMap['External Catalog'] }
])
const formState:FormState = reactive({
  catalog: {
    name: '',
    type: 'ams',
    typeshow: typeShowMap['Internal Catalog'],
    optimizerGroup: undefined
  },
  tableFormat: '',
  storageConfig: {},
  authConfig: {},
  properties: {},
  tableProperties: {},
  storageConfigArray: [],
  authConfigArray: []
})

const hadoopConfigTypeOps = reactive<ILableAndValue[]>([{
  label: 'SIMPLE',
  value: 'SIMPLE'
}, {
  label: 'KERBEROS',
  value: 'KERBEROS'
}])

const s3ConfigTypeOps = reactive<ILableAndValue[]>([{
  label: 'AK/SK',
  value: 'AK/SK'
}, {
  label: 'CUSTOM',
  value: 'CUSTOM'
}])

const storageConfigMap = {
  'hadoop.core.site': 'Hadoop core-site',
  'hadoop.hdfs.site': 'Hadoop hdfs-site',
  'hive.site': 'Hadoop hive-site'
}
const authConfigMap = {
  'auth.kerberos.keytab': 'Kerberos Keytab',
  'auth.kerberos.krb5': 'Kerberos Krb5'
}

const defaultPropertiesMap = {
  ams: ['warehouse'],
  hadoop: ['warehouse'],
  custom: ['catalog-impl'],
  glue: ['warehouse', 'lock-impl', 'lock.table'],
  PAIMON: ['warehouse']
}

watch(() => route.query,
  (value) => {
    value && initData()
  }, {
    immediate: true,
    deep: true
  }
)
const catalogTypeOps = reactive<ILableAndValue[]>([])
const optimizerGroupList = ref<ILableAndValue[]>([])
function initData() {
  getConfigInfo()
}
const getOptimizerGroupList = async() => {
  const res = await getResourceGroupsListAPI()
  const list = (res || []).map((item: IIOptimizeGroupItem) => ({ lable: item.resourceGroup.name, value: item.resourceGroup.name }))
  optimizerGroupList.value = list
}
async function getCatalogTypeOps() {
  const res = await getCatalogsTypes();
  (res || []).forEach(ele => {
    if (ele.value !== 'ams') {
      catalogTypeOps.push({
        label: ele.display,
        value: ele.value
      })
    }
  })
  getMetastoreType()
}
function getMetastoreType() {
  metastoreType.value = (catalogTypeOps.find(ele => ele.value === formState.catalog.type) || {}).label
}
async function getConfigInfo() {
  try {
    loading.value = true
    const { catalogname, type } = route.query
    if (!catalogname) { return }
    if (isNewCatalog.value) {
      formState.catalog.name = ''
      formState.catalog.type = type || 'ams'
      formState.catalog.optimizerGroup = undefined
      formState.tableFormat = tableFormatMap.MIXED_ICEBERG
      formState.authConfig = { ...newCatalogConfig.authConfig }
      formState.storageConfig = { ...newCatalogConfig.storageConfig }
      const keys = defaultPropertiesMap[formState.catalog.type] || []
      formState.properties = {}
      keys.forEach(key => {
        formState.properties[key] = ''
      })
      formState.tableProperties = {}
      formState.storageConfigArray.length = 0
      formState.authConfigArray.length = 0
    } else {
      const res = await getCatalogsSetting(catalogname)
      if (!res) { return }
      const { name, type, tableFormatList, storageConfig, authConfig, properties, tableProperties, optimizerGroup } = res
      formState.catalog.name = name
      formState.catalog.type = type
      formState.catalog.optimizerGroup = optimizerGroup
      formState.tableFormat = tableFormatList.join('')
      formState.authConfig = authConfig
      formState.storageConfig = storageConfig
      formState.properties = properties || {}
      formState.tableProperties = tableProperties || {}
      formState.storageConfigArray.length = 0
      formState.authConfigArray.length = 0
      getMetastoreType()
    }
    formState.catalog.typeshow = formState.catalog.type === 'ams' ? typeShowMap['Internal Catalog'] : typeShowMap['External Catalog']

    const { storageConfig, authConfig } = formState
    Object.keys(storageConfig).forEach(key => {
      const configArr = ['hadoop.core.site', 'hadoop.hdfs.site']
      if (isHiveMetastore.value) {
        configArr.push('hive.site')
      }
      if (configArr.includes(key)) {
        const item: IStorageConfigItem = {
          key,
          label: storageConfigMap[key],
          value: storageConfig[key]?.fileName,
          fileName: storageConfig[key]?.fileName,
          fileUrl: storageConfig[key]?.fileUrl,
          fileId: '',
          fileList: [],
          uploadLoading: false,
          isSuccess: false
        }
        formState.storageConfigArray.push(item)
      }
    })
    Object.keys(authConfig).forEach(key => {
      if (['auth.kerberos.keytab', 'auth.kerberos.krb5'].includes(key)) {
        const item: IStorageConfigItem = {
          key,
          label: authConfigMap[key],
          value: authConfig[key]?.fileName,
          fileName: authConfig[key]?.fileName,
          fileUrl: authConfig[key]?.fileUrl,
          fileId: '',
          fileList: [],
          uploadLoading: false,
          isSuccess: false
        }
        formState.authConfigArray.push(item)
      }
    })
  } catch (error) {
  } finally {
    loading.value = false
  }
}

const changeTypeShow = (val: string) => {
  if (val === typeShowMap['Internal Catalog']) {
    formState.catalog.type = 'ams'
  } else {
    formState.catalog.type = catalogTypeOps[0].value
  }
  changeMetastore()
}

const formatOptions = computed(() => {
  const type = formState.catalog.type
  return storeSupportFormat[type] || []
})

async function changeProperties() {
  const properties = await propertiesRef.value.getPropertiesWithoputValidation()
  const catalogKeys = defaultPropertiesMap[formState.catalog.type] || []
  catalogKeys.forEach(key => {
    if (key && !properties[key]) {
      properties[key] = ''
    }
  })
  const formatKeys = defaultPropertiesMap[formState.tableFormat] || []
  formatKeys.forEach(key => {
    if (key && !properties[key]) {
      properties[key] = ''
    }
  })
  for (const key in properties) {
    if (!properties[key] && !catalogKeys.includes(key) && !formatKeys.includes(key)) {
      delete properties[key]
    }
  }
  formState.properties = properties
}

const storageConfigTypeS3 = reactive<ILableAndValue[]>([{
  label: 'S3',
  value: 'S3'
}])

const storageConfigTypeHadoop = reactive<ILableAndValue[]>([{
  label: 'Hadoop',
  value: 'Hadoop'
}])

const storageConfigTypeHadoopS3 = reactive<ILableAndValue[]>([{
  label: 'Hadoop',
  value: 'Hadoop'
}, {
  label: 'S3',
  value: 'S3'
}])

const storageConfigTypeOps = computed(() => {
  const type = formState.catalog.type
  if (type === 'ams' || type === 'custom') {
    return storageConfigTypeHadoopS3
  } else if (type === 'glue') {
    return storageConfigTypeS3
  } else if (type === 'hive' || type === 'hadoop') {
    return storageConfigTypeHadoop
  } else {
    return null
  }
})

const authTypeOptions = computed(() => {
  const type = formState.storageConfig['storage.type']
  if (type === 'Hadoop') {
    return hadoopConfigTypeOps
  } else if (type === 'S3') {
    return s3ConfigTypeOps
  } else {
    return null
  }
})

async function changeMetastore() {
  formState.tableFormat = formatOptions.value[0]
  if (!isNewCatalog.value) { return }
  const index = formState.storageConfigArray.findIndex(item => item.key === 'hive.site')
  if (isHiveMetastore.value) {
    if (index > -1) {
      return
    }
    formState.storageConfigArray.push({
      key: 'hive.site',
      label: storageConfigMap['hive.site'],
      value: '',
      fileName: '',
      fileUrl: '',
      fileId: '',
      fileList: [],
      uploadLoading: false,
      isSuccess: false
    })
    formState.storageConfig['hive.site'] = ''
  } else {
    if (index > -1) {
      formState.storageConfigArray.splice(index, 1)
      delete formState.storageConfig['hive.site']
    }
  }
  await changeProperties()
}

async function changeTableFormat() {
  await changeProperties()
}

function handleEdit() {
  emit('updateEdit', true)
}
async function handleRemove() {
  const res = await checkCatalogStatus(formState.catalog.name)
  if (res) {
    deleteCatalogModal()
    return
  }
  Modal.confirm({
    title: t('cannotDeleteModalTitle'),
    content: t('cannotDeleteModalContent'),
    wrapClassName: 'not-delete-modal'
  })
}
async function validatorName(rule, value) {
  if (!value) {
    return Promise.reject(new Error(t('inputPlaceholder')))
  }
  if ((/^[a-zA-Z][\w-]*$/.test(value))) {
    return Promise.resolve()
  } else {
    return Promise.reject(new Error(t('invalidInput')))
  }
}
function getFileIdParams() {
  const { storageConfig, authConfig, storageConfigArray, authConfigArray } = formState
  Object.keys(authConfig).forEach(key => {
    if (['auth.kerberos.keytab', 'auth.kerberos.krb5'].includes(key)) {
      const id = (authConfigArray.find(item => item.key === key) || {}).fileId
      authConfig[key] = id
    }
  })
  Object.keys(storageConfig).forEach(key => {
    if (['hadoop.core.site', 'hadoop.hdfs.site', 'hive.site'].includes(key)) {
      const id = (storageConfigArray.find(item => item.key === key) || {}).fileId
      storageConfig[key] = id
    }
  })
}
function handleSave() {
  formRef.value
    .validateFields()
    .then(async() => {
      const { catalog, tableFormat, storageConfig, authConfig } = formState
      const properties = await propertiesRef.value.getProperties()
      const tableProperties = await tablePropertiesRef.value.getProperties()
      if (!properties) {
        return
      }
      if (!tableProperties) {
        return
      }
      loading.value = true
      const { typeshow, ...catalogParams } = catalog
      getFileIdParams()
      await saveCatalogsSetting({
        isCreate: isNewCatalog.value,
        ...catalogParams,
        tableFormatList: [tableFormat],
        storageConfig,
        authConfig,
        properties,
        tableProperties
      }).then(() => {
        message.success(`${t('save')} ${t('success')}`)
        emit('updateEdit', false, {
          catalogName: catalog.name,
          catalogType: catalog.type
        })
        getConfigInfo()
        formRef.value.resetFields()
      }).catch(() => {
        message.error(`${t('save')} ${t('failed')}`)
      }).finally (() => {
        loading.value = false
      })
    })
    .catch(() => {
    })
}
function handleCancle() {
  formRef.value.resetFields()
  emit('updateEdit', false)
  getConfigInfo()
}
async function deleteCatalogModal() {
  Modal.confirm({
    title: t('deleteCatalogModalTitle'),
    onOk: async() => {
      await delCatalog(formState.catalog.name)
      message.success(`${t('remove')} ${t('success')}`)
      emit('updateEdit', false, {})
    }
  })
}
function uploadFile(info: UploadChangeParam, config, type?) {
  try {
    if (info.file.status === 'uploading') {
      config.uploadLoading = true
    } else {
      config.uploadLoading = false
    }
    if (info.file.status === 'done') {
      const { code } = info.file.response
      if (code !== 200) {
        throw new Error('failed')
      }
      const { url, id } = info.file.response.result
      config.isSuccess = true
      config.fileName = type === 'STORAGE' ? storageConfigFileNameMap[config.key] : info.file.name
      config.fileUrl = url
      config.fileId = id
      message.success(`${info.file.name} ${t('uploaded')} ${t('success')}`)
    } else if (info.file.status === 'error') {
      config.isSuccess = false
      message.error(`${info.file.name} ${t('uploaded')} ${t('failed')}`)
    }
  } catch (error) {
    message.error(`${t('uploaded')} ${t('failed')}`)
  }
}
function viewFileDetail(url: string) {
  url && window.open(url)
}
onMounted(() => {
  getCatalogTypeOps()
  getOptimizerGroupList()
})

</script>

<style lang="less" scoped>
.detail-wrap {
  height: 100%;
  padding: 16px 0 16px 24px;
  display: flex;
  flex: 1;
  flex-direction: column;
  border: 1px solid #e8e8f0;
  border-left: 0;
  .detail-content-wrap {
    height: 100%;
    padding-right: 200px;
    overflow: auto;
  }
  .content-wrap {
    display: flex;
    flex: 1;
    overflow: auto;
    flex-direction: column;
    .ant-form-item {
      margin-bottom: 8px;
    }
    :deep(.ant-form-item-label) {
      > label {
        word-break: break-all;
        white-space: pre-wrap;
      }
      width: 280px;
      margin-right: 16px;
    }
    .header {
      font-size: 16px;
      font-weight: 600;
      color: #102048;
    }
    .view-active {
      color: @primary-color;
      cursor: pointer;
    }
    .config-value {
      word-break: break-all;
      &.auth-filename {
        max-width: 72%;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: inline-block;
        vertical-align: middle;
        margin-top: -4px;
      }
    }
  }
  .footer-btn {
    height: 44px;
    flex-shrink: 0;
    padding-top: 12px;
    background-color: #fff;
    .edit-btn, .save-btn {
      min-width: 60px;
    }
    .remove-btn {
      &:hover {
        background-color: #ff4d4f;
        border-color: transparent;
        color: #fff;
      }
    }
  }
}
</style>
<style lang="less">
.not-delete-modal {
  .ant-modal-confirm-btns .ant-btn:first-child {
    display: none;
  }
}
</style>

<template>
  <q-page>
    <div class="q-pa-md">
      <div class="row col-12 justify-end">
        <q-btn flat label="Upload" color="primary" @click="handleShowUploadDialog()"/>
      </div>
      <q-card class="my-card">
        <q-table title="Treats" :rows="rows" :columns="columns" row-key="flat" hide-pagination :rows-per-page-options="[0]" v-model:pagination="pagination" no-data-label="No products found. Upload xls with products">
          <template v-slot:top>
            <div class="col-11 q-table__title">Products</div>
          </template>
          <template v-slot:body="props">
            <q-tr :props="props">
              <q-td key="id" :props="props">
                {{ props.row.id }}
              </q-td>
              <q-td key="name" :props="props" :class="props.row.manjeeraInvalid ? 'bg-negative text-white' : 'bg-white text-black'">
                {{ props.row.name }}
              </q-td>
              <q-td key="category" :props="props" :class="props.row.boreInvalid ? 'bg-negative text-white' : 'bg-white text-black'">
                {{ props.row.category }}
              </q-td>
              <q-td key="image" :props="props" :class="props.row.boreInvalid ? 'bg-negative text-white' : 'bg-white text-black'">
                {{ props.row.image }}
              </q-td>
            </q-tr>
          </template>
        </q-table>
      </q-card>
    </div>
    <q-dialog v-model="showUploadDialog">
      <q-card style="min-width: 300px">
        <q-card-section>
          <q-uploader
            :url="urlPrefix + '/products/upload?uploadMode=file'"            
            label="Upload"
            field-name="xls"
            auto-upload
            @uploaded="handleUploadComplete"                  
            style="width: 100%"/>
        </q-card-section>        
        <q-card-actions align="right" class="text-primary">
          <q-btn flat :label="$t('close')" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>
import { defineComponent } from 'vue'
import { mapGetters } from 'vuex'
import { apiRegistry } from '../services/rest'
import { serviceRegistry } from '../services/readingservice'
import { ref } from 'vue'

export default defineComponent({
  name: 'PageProducts',
  data() {
    return {
      urlPrefix: "",
      showUploadDialog:false,
      showDateSelector:false,
      showAccept:false,
      selectedDate: "",
      pagination: {
         page: 1,
         rowsPerPage: 0 // 0 means all rows
      },      
      columns : [{
          name: 'id',
          required: true,
          label: 'Id',
          align: 'left',
          field: 'id',    
          sortable: true
        },
        { name: 'name', align: 'left', label: 'Name', field: 'name', sortable: true },
        { name: 'category', align: 'left', label: 'Category', field: 'category', sortable: true },
        { name: 'image', align: 'left', label: 'Image', field: 'image', sortable: true }
      ],
      rows:[]      
    }
  },
  mounted() {
    this.urlPrefix = apiRegistry.SERVICES_URL_PREFIX
    apiRegistry.getAllProducts().then(products => {
      this.rows = products      
    }).finally(() => {
      this.loading = false
    })
  },
  computed: {
    ...mapGetters(["token"])
  },
  methods: {
    handleShowUploadDialog(event) {
        this.showUploadDialog = true
    },
    handleUploadComplete(file, xhr) {
      const products = JSON.parse(file.xhr.response).products
      this.rows = products
      this.showUploadDialog = false
      this.showAccept = false
    }
  }
})
</script>

<style lang="sass" scoped>
.my-card
  width: 100%  
</style>
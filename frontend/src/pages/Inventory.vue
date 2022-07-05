<template>
  <q-page>
    <div class="q-pa-md">
      <div class="row col-12 justify-end">
        <q-btn flat label="Upload" color="primary" @click="handleShowUploadDialog()"/>
      </div>
      <q-card class="my-card">
        <q-table title="Treats" :rows="rows" :columns="columns" row-key="flat" hide-pagination :rows-per-page-options="[0]" v-model:pagination="pagination" no-data-label="Please upload seller sheet">
          <template v-slot:top>
            <div class="col-11 q-table__title">Inventory</div>
            <div class="col-1 q-table__title">
              <div class="row col-12 justify-end">
                <q-btn v-if="showAccept" icon="done_all" color="primary" @click="handleShowDatePicker" flat round dense/>
              </div>
            </div>
          </template>
          <template v-slot:body="props">
            <q-tr :props="props">
              <q-td key="name" :props="props">
                {{ props.row.name }}
              </q-td>
              <q-td key="price" :props="props" :class="props.row.manjeeraInvalid ? 'bg-negative text-white' : 'bg-white text-black'">
                {{ props.row.price }}
                <q-popup-edit v-model="props.row.manjeera">
                  <q-input v-model="props.row.manjeera" dense autofocus counter />
                </q-popup-edit>
              </q-td>
              <q-td key="quantity" :props="props" :class="props.row.boreInvalid ? 'bg-negative text-white' : 'bg-white text-black'">
                {{ props.row.quantity }}
                <q-popup-edit v-model="props.row.bore">
                  <q-input v-model="props.row.bore" dense autofocus counter />
                </q-popup-edit>
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
            :url="urlPrefix + '/inventory/scan?uploadMode=file'"            
            label="Upload"
            field-name="image"
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
import { ref } from 'vue'

export default defineComponent({
  name: 'PageInventory',
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
      columns : [
        { name: 'name', align: 'left', label: 'Name', field: 'name', sortable: true },
        { name: 'price', align: 'right', label: 'Price', field: 'price', sortable: true },
        { name: 'quantity', align: 'right', label: 'Quantity', field: 'quantity', sortable: true }
      ],
      rows:[]      
    }
  },
  mounted() {
    this.urlPrefix = apiRegistry.SERVICES_URL_PREFIX
    const today = new Date().toISOString().slice(0, 10).replaceAll("-","/")    
    this.selectedDate = ref(today)
  },
  computed: {
    ...mapGetters(["token"])
  },
  methods: {
    handleShowDatePicker() {
      this.showDateSelector = true
    },
    handleShowUploadDialog(event) {
        this.showUploadDialog = true
    },
    handleUploadComplete(file, xhr) {
      this.rows = JSON.parse(file.xhr.response).inventory
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
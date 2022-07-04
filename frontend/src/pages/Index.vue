<template>
  <q-page>
      <div class="q-pa-md">          
            <q-card class="my-card">
                <q-table title="Sellers" :rows="rows" :columns="columns" row-key="key" hide-pagination :rows-per-page-options="[0]" v-model:pagination="pagination" no-data-label="No data. Check network" :loading="loading">
                  <template v-slot:body="props">
                    <q-tr :props="props">
                      <q-td key="name" :props="props">
                        {{ props.row.name }}
                      </q-td>
                      <q-td key="address" :props="props">
                        {{ props.row.address }}
                      </q-td>
                      <q-td key="phone" :props="props">
                        {{ props.row.phone }}
                      </q-td>
                      <q-td key="actions" :props="props">
                        <q-btn-group rounded>
                          <q-btn icon="download" size="sm" flat @click="handleDownload(props.row.id)"/>                          
                        </q-btn-group>                        
                      </q-td>
                    </q-tr>                    
                  </template>
                </q-table>
            </q-card>
          </div>         
  </q-page>
</template>

<script>
import { defineComponent } from 'vue'
import { apiRegistry } from '../services/rest'
import { mapGetters } from "vuex";

export default defineComponent({
  name: 'PageIndex',
  data() {
    return {
      urlPrefix: "",
      //meterKeys: [],
      loading: false,
      pagination: {
         page: 1,
         rowsPerPage: 0 // 0 means all rows
      },
      columns : [
        // {
        //   name: 'id',
        //   required: true,
        //   label: 'Id',
        //   align: 'left',
        //   field: 'id',    
        //   sortable: false
        // },
        { name: 'name', align: 'left', label: 'Name' },  
        { name: 'address', align: 'left', label: 'Address' },  
        { name: 'phone', align: 'left', label: 'Phone' },  
        //{ name: 'email', align: 'left', label: 'Email' },  
        { name: 'actions', align: 'left', label: 'Actions' },  
    ],
    rows:[]      
    }
  },
  methods: {    
    handleDownload(sellerId) {
      apiRegistry.downloadSellerSheet({sellerId})
    },
    handleChecks(key) {
      apiRegistry.downloadMaintenanceXLS({token:this.token, key})
    }
  },
  computed: {
    ...mapGetters(["token"])
  },
  mounted() {    
    this.loading = true
    this.urlPrefix = apiRegistry.SERVICES_URL_PREFIX
    apiRegistry.getAllSellers().then(sellers => {
      this.rows = sellers      
    }).finally(() => {
      this.loading = false
    })
  }
})
</script>
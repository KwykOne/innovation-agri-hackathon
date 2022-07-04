<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn          
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="leftDrawerOpen = !leftDrawerOpen"
        />

        <q-toolbar-title>
          Seller portal
        </q-toolbar-title>
        
      </q-toolbar>
    </q-header>

    <q-drawer
      v-model="leftDrawerOpen"      
      bordered
      class="bg-grey-1"
    >
      <q-list>
        <q-item clickable to="/" @click="leftDrawerOpen = false">
          <q-item-section avatar>
            <q-icon name="storefront"/>
          </q-item-section>
          <q-item-section>
            <q-item-label>Sellers</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/products" @click="leftDrawerOpen = false">
          <q-item-section avatar>
            <q-icon name="shopping_cart"/>
          </q-item-section>
          <q-item-section>
            <q-item-label>Products</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable to="/inventory" @click="leftDrawerOpen = false">
          <q-item-section avatar>
            <q-icon name="inventory"/>
          </q-item-section>
          <q-item-section>
            <q-item-label>Inventory</q-item-label>
          </q-item-section>
        </q-item>
        <!--
        <EssentialLink
          v-for="link in essentialLinks"
          :key="link.title"
          v-bind="link"
        />
        -->
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { mapGetters } from "vuex";
import EssentialLink from 'components/EssentialLink.vue'

const linksList = [
  {
    title: 'Sellers',
    caption: '',
    icon: 'storefront',
    link: '/'
  },
  {
    title: 'Products',
    caption: '',
    icon: 'list',
    link: '/'
  },
  {
    title: 'Products',
    caption: '',
    icon: 'list',
    link: '/'
  }
];

import { defineComponent, ref } from 'vue'

export default defineComponent({
  name: 'MainLayout',

  components: {
    EssentialLink
  },
  computed: {
    ...mapGetters(["loginState"])
  },

  setup () {
    const leftDrawerOpen = ref(false)
    return {
      essentialLinks: linksList,
      leftDrawerOpen,
      toggleLeftDrawer () {
        leftDrawerOpen.value = !leftDrawerOpen.value
      }
    }
  }
})
</script>

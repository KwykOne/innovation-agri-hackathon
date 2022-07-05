<template>
  <router-view />
</template>
<script>

import { defineComponent } from 'vue';
import firebase from 'firebase/app'
import 'firebase/auth'

export default defineComponent({
  name: 'App',
  mounted() {
    // firebase.auth().setPersistence(firebase.auth.Auth.Persistence.LOCAL).then(() => {
    //   //console.log("Set persistence success")
    // }).catch((error) => {
    //   console.log(error)
    // })
  }
})
</script>

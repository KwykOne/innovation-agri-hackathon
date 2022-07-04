import { boot } from 'quasar/wrappers'
import firebase from "firebase/app"

// "async" is optional;
// more info on params: https://v2.quasar.dev/quasar-cli/boot-files
export default boot(async (/* { app, router, ... } */) => {
  // something to do
  const firebaseConfig = {
  }
  // if (!firebase.apps.length) {
  //   firebase.initializeApp(firebaseConfig)
  // }
})

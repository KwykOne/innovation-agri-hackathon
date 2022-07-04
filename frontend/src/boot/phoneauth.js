import { boot } from 'quasar/wrappers'
import firebase from "firebase/app"

// "async" is optional;
// more info on params: https://v2.quasar.dev/quasar-cli/boot-files
export default boot(async (/* { app, router, ... } */) => {
  // something to do
  const firebaseConfig = {
    apiKey: "AIzaSyCD928LXdMe1OevM9kyOCBl8IPJqh8GSQk",
    authDomain: "arca-owners-association.firebase.com",
    projectId: "arca-owners-association",
    //storageBucket: "your-firebase-storage-bucket",
    //messagingSenderId: "your-firebase-sender-id",
    appId: "1:108803807828:web:2c18c55f910239527babc1"
  }
  if (!firebase.apps.length) {
    firebase.initializeApp(firebaseConfig)
  }
})

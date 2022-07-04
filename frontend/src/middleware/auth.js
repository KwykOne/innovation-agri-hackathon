import firebase from 'firebase/app'
import 'firebase/auth'

export default function auth({next, router, store, to}) {
    const currentState = store.getters["loginState"]
    if(to.path == "/login") {
        if(currentState == "loggedin") {            
            router.push({path:"/"})            
        } else
            return next()        
    } else if(currentState === "loggedin") {
        return next()
    } else if (currentState == "unknown") {
        store.dispatch('updateNextPath', to.path)
        router.push({path:"/loading"})
    } else {
        store.dispatch('updateLoginState', "loggedout")
        router.push({path:"/login"})
    }   
    
}
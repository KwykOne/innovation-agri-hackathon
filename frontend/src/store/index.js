import { store } from 'quasar/wrappers'
import { createStore } from 'vuex'

// import example from './module-example'

/*
 * If not building with SSR mode, you can
 * directly export the Store instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Store instance.
 */

export default store(function (/* { ssrContext } */) {
  const Store = createStore({
    modules: {
      // example
    },
    state : {
      loginState: "unknown",
      token: "",
      nextPath: "/"    
    },
    getters: {
      loginState: state => state.loginState,
      token: state => state.token,
      nextPath: state => state.nextPath      
    },
    mutations: {
      setLoginState: (state, loginState) => {
        state.loginState = loginState
      },
      setToken: (state, token) => {
        state.token = token
      },
      setNextPath: (state, nextPath) => {
        state.nextPath = nextPath
      }
    },
    actions: {
      updateLoginState({commit}, loginState) {
        //console.log("Changing status", loginState)
        commit("setLoginState", loginState)
      },
      updateToken({commit}, token) {
        commit('setToken', token)
      },
      updateNextPath({commit}, nextPath) {
        commit('setNextPath', nextPath)
      }
    },    

    // enable strict mode (adds overhead!)
    // for dev mode and --debug builds only
    strict: process.env.DEBUGGING
  })

  return Store
})

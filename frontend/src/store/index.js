import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        isApplicationRejected: false,
    },
    mutations: {
        setApplicationRejected(state, status){
            state.isApplicationRejected = status;
        }
    },
    actions: {
        updateApplicationStatus({ commit }, status){
            commit('setApplicationRejected', status);
        }
    },
    getters: {
        isApplicationRejected: (state) =>{
            console.log('isApplicationRejected getter:', state.isApplicationRejected);
            return state.isApplicationRejected;
        }
    }
})

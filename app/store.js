import {persistReducer} from 'redux-persist';
import {combineReducers, createStore, legacy_createStore} from 'redux';
import {MMKVLoader} from 'react-native-mmkv-storage';
import farmReducer from './redux/farm-reducer';
import farmerReducer from './redux/farmer-reducer';

const storage = new MMKVLoader().initialize();

const appReducer = combineReducers({
    // Add reducers here
    farmReducer: farmReducer,
    farmerReducer: farmerReducer,
});

const rootReducer = (state: any, action: any) => {
    if (action.type === 'RESET') {
        console.log('🔥 resetting redux store');
        storage.removeItem('persist:root');
        return appReducer(undefined, action);
    }
    return appReducer(state, action);
};

const persistConfig = {
    key: 'root',
    storage,
    whitelist: [
        // whitelist reducers here -- to persist
        'farmerReducer',
        'farmReducer',
    ],
};
const persistedReducer = persistReducer(persistConfig, rootReducer);

const configurePersistedStore = legacy_createStore(persistedReducer);

export default configurePersistedStore;

/**
 * @format
 */
import React from 'react';
import 'react-native-gesture-handler';
import {ActivityIndicator, AppRegistry, Text, View} from 'react-native';
import {name as appName} from './app.json';
import {NavigationContainer} from '@react-navigation/native';
import AppStackScreens from './navigator';
import configurePersistedStore from './store';
import {Provider} from 'react-redux';
import {persistStore} from 'redux-persist';
import {PersistGate} from 'redux-persist/es/integration/react';
import {AppContext} from './ctx';

export const store = configurePersistedStore;
export const persistedStore = persistStore(store);

function Main() {
    return (
        <Provider store={store}>
            <PersistGate persistor={persistedStore} loading={null}>
                <NavigationContainer
                    fallback={
                        <View
                            style={{
                                backgroundColor: 'ivory',
                                alignItems: 'center',
                                justifyContent: 'center',
                                flex: 1,
                            }}>
                            <ActivityIndicator
                                color={'red'}
                                size={'small'}
                            />
                            <Text>Loading...</Text>
                        </View>
                    }>
                    <AppStackScreens />
                </NavigationContainer>
            </PersistGate>
        </Provider>
    )
}

AppRegistry.registerComponent(appName, () => Main);

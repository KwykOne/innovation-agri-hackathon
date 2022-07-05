import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import ScanOpScreen from './workflows/ScanOpScreen';
import App from './App';
import FarmInventoryScreen from './workflows/FarmInventoryScreen';
import FarmInventoryLedgerScreen from './workflows/FarmInventoryLedgerScreen';
import MeriDukaanScreen from './workflows/MeriDukaanScreen';

export const AppStack = createNativeStackNavigator();

const AppStackScreens = () => (
    <AppStack.Navigator
        screenOptions={({route, navigation}) => {
            return {
                headerTintColor: '#414141',
                headerStyle: {
                    backgroundColor: 'white',
                },
            };
        }}>
        <AppStack.Screen name={'App'} component={App} />
        <AppStack.Screen name={'ScanOpScreen'} component={ScanOpScreen} />
        <AppStack.Screen name={'FarmInventoryScreen'} component={FarmInventoryScreen} />
        <AppStack.Screen name={'FarmInventoryLedgerScreen'} component={FarmInventoryLedgerScreen} />
        <AppStack.Screen name={'MeriDukaanScreen'} component={MeriDukaanScreen} />
    </AppStack.Navigator>
);

export default AppStackScreens;

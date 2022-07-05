import React, {useState} from 'react';
import {useNavigation} from '@react-navigation/native';
import {StyleSheet, Text, TextInput, TouchableOpacity, View} from 'react-native';

function MeriDukaanScreen() {
    const navigation = useNavigation();
    const fetchInventoryLedger = () => {
        // todo: api call
    };
    React.useLayoutEffect(() => {
        navigation.setOptions({
            title: 'मेरी दुकान',
            headerCenter: () => null,
        });
    }, [navigation]);
    const [farmName, setFarmName] = useState('चिंटू जी का गोदाम');
    const [payment, setPayment] = useState('नकद, किसान क्रेडिट कार्ड, UPI');

    return (
        <View style={styles.container}>
            <Text style={{fontSize: 18, paddingTop: 10, paddingBottom: 8,}}>
                अपना दुकान का नाम दर्ज करें
            </Text>
            <TextInput
                value={farmName}
                onChangeText={setFarmName}
                placeholder={'उदाहरण के लिए - चिंटू'}
                style={{
                    paddingLeft: 10,
                    letterSpacing: 1,
                    fontSize: 20,
                    color: '#000',
                    backgroundColor: 'white',
                    elevation: 2,
                    borderWidth: 1,
                    borderRadius: 6,
                    borderColor: '#e1e1e1',
                }}
            />
            <Text style={{fontSize: 18, paddingTop: 20, paddingBottom: 8,}}>
                पैसे कैसे दिया जा सकता है
            </Text>
            <TextInput
                value={payment}
                onChangeText={setPayment}
                placeholder={'उदाहरण के लिए - नकद, किसान क्रेडिट कार्ड, UPI'}
                style={{
                    paddingLeft: 10,
                    letterSpacing: 1,
                    fontSize: 20,
                    color: '#000',
                    backgroundColor: 'white',
                    elevation: 2,
                    borderWidth: 1,
                    borderRadius: 6,
                    borderColor: '#e1e1e1',
                }}
            />
            <View style={{ flex: 1, alignItems: 'center', justifyContent: 'flex-end'}}>
                <TouchableOpacity
                    onPress={() => { navigation.goBack()}}
                    style={{
                        backgroundColor: 'coral',
                        paddingVertical: 12,
                        alignItems: 'center',
                        marginRight: 16,
                        borderRadius: 6,
                        elevation: 6,
                        width: '100%',
                    }}>
                    <Text style={{fontSize: 16, fontWeight: 'bold', color: '#000'}}>
                        आगे बढ़े
                    </Text>
                </TouchableOpacity>
            </View>
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        paddingHorizontal: 16,
        paddingVertical: 10,
        flex: 1,
        backgroundColor: 'ivory',
    },
});

export default MeriDukaanScreen;

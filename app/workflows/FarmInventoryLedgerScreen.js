import React, {useState} from 'react';
import {Alert, Button, Image, Linking, Modal, StyleSheet, Text, TextInput, TouchableOpacity, View} from 'react-native';
import {useNavigation} from '@react-navigation/native';

function FarmInventoryLedgerScreen() {
    const navigation = useNavigation();
    const fetchInventoryLedger = () => {
        // todo: api call
    };
    React.useLayoutEffect(() => {
        navigation.setOptions({
            title: 'खाता बही',
            headerCenter: () => null,
        });
    }, [navigation]);
    const [visible, setVisible] = useState(false);
    const [price, setPrice] = useState('200');
    const whatsappShare = () => {
        const msg = `tomato (Medium-RohiniGreen) 25kg ₹200\nPerish: 10days, Logistics: crate\nCold chain: yes, Del tat: 1`;
        let url =
            'whatsapp://send?text=' + msg;
        Linking.openURL(url)
            .then((data) => {
                console.log('WhatsApp Opened');
            })
            .catch(() => {
                Alert.alert('Make sure Whatsapp installed on your device');
            });
    };
    const ledgerEntry = ({productName, variant, packSize, grade, qty, op, open}) => {
        return (
            <View style={{borderBottomWidth: 1, borderBottomColor: '#e1e1e1', paddingTop: 10}}>
                <View style={{flexDirection: 'row'}}>
                    <View style={{flex: 2, alignItems: 'center'}}>
                        <Text>25 June</Text>
                        <Text>7:30 am</Text>
                    </View>
                    <View style={{ flex: 5,}}>
                        <Text style={{fontWeight: 'bold', color: '#000'}}>{productName}</Text>
                        <Text style={{fontSize: 12, marginBottom: 4}}>{variant}</Text>
                        <View style={{flexDirection: 'row', alignItems: 'center', paddingRight: 6}}>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingRight: 4}}>{packSize}</Text>
                            </View>
                            <View style={{ flex: 1.8}}>
                                <Text style={{fontSize: 12, paddingLeft: 4}}>Grade: {grade}</Text>
                            </View>
                        </View>
                    </View>
                    <View style={{flex: 1}}>
                        <Text style={{textAlign: 'center'}}>{qty}</Text>
                    </View>
                    <View style={{ flex: 1,}}>
                        <Image source={op === 'in' ? require(`../img/in.png`) : require('../img/out.png')} style={{ width: 14, height: 14, resizeMode: 'contain'}} />
                    </View>
                    <View style={{ flex: 1,}}>
                        <Text style={{textAlign: 'center'}}>{open}</Text>
                    </View>
                </View>
            </View>
        );
    };
    const header = () => {
        return (
            <View style={{flexDirection: 'row', borderBottomWidth: 1, borderBottomColor: '#e1e1e1', paddingVertical: 10}}>
                <View style={{flex: 2, alignItems: 'center'}}>
                    <Text>Time</Text>
                </View>
                <View style={{ flex: 5,}}>
                    <Text>Details</Text>
                </View>
                <View style={{flex: 1,}}>
                    <Text>qty</Text>
                </View>
                <View style={{flex: 1,}}>
                    <Text>op</Text>
                </View>
                <View style={{ flex: 1, alignItems: 'center'}}>
                    <Text>open</Text>
                </View>
            </View>
        );
    };
    return (
        <View style={styles.container}>
            {header()}
            {ledgerEntry({
                productName: 'tomato',
                variant: 'Medium-RohiniGreen',
                packSize: '25kg',
                grade: 'Medium',
                qty: 1,
                op: 'in',
                open: 0,
            })}
            {ledgerEntry({
                productName: 'banana',
                variant: 'Medium-RobustaYellow',
                packSize: '1bunch',
                grade: 'Medium',
                qty: 1,
                op: 'out',
                open: 2,
            })}
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'ivory',
    },
});

export default FarmInventoryLedgerScreen;

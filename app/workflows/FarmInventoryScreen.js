import React, {useState} from 'react';
import {Alert, Button, Image, Linking, Modal, StyleSheet, Text, TextInput, TouchableOpacity, View} from 'react-native';
import {useNavigation} from '@react-navigation/native';

function FarmInventoryScreen() {
    const navigation = useNavigation();
    const fetchInventory = () => {
        // todo: api call
    };
    React.useLayoutEffect(() => {
        navigation.setOptions({
            title: 'माल सूची',
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
    const priceChangeModal = () => (
        <Modal
            animationType="slide"
            transparent={true}
            visible={visible}
            onRequestClose={() => {
                setVisible(!visible);
            }}
        >
            <View style={[{flex: 1, alignItems: 'center', justifyContent: 'center' }, visible ? {backgroundColor: 'rgba(0,0,0,0.8)'} : {}]}>
                <View style={{ backgroundColor: 'ivory', borderRadius: 20, height: 320, width: 320, border: 'none', alignItems: 'center', justifyContent: 'center' }}>
                    <TextInput
                        autoFocus={true}
                        value={price}
                        onChangeText={setPrice}
                        placeholder={'उदाहरण के लिए - 224'}
                        style={{
                            textAlign: 'center',
                            paddingLeft: 10,
                            letterSpacing: 1,
                            fontSize: 20,
                            color: '#000',
                            backgroundColor: 'white',
                            elevation: 2,
                            borderWidth: 1,
                            borderRadius: 6,
                            borderColor: '#e1e1e1',
                            width: 220,
                        }}
                    />
                    <TouchableOpacity
                        onPress={() => {}}
                        style={{
                            marginTop: 20,
                            backgroundColor: '#ffd0af',
                            paddingVertical: 8,
                            alignItems: 'center',
                            marginHorizontal: 16,
                            borderRadius: 6,
                            elevation: 2,
                            width: 220,
                        }}>
                        <Text style={{fontSize: 16, fontWeight: 'bold', color: '#e56200'}}>
                            नया भाव पुष्टि करें
                        </Text>
                    </TouchableOpacity>
                </View>
                <TouchableOpacity
                    onPress={() => {setVisible(false)}}
                    style={{
                        marginTop: 20,
                        backgroundColor: '#fff',
                        paddingVertical: 8,
                        alignItems: 'center',
                        marginHorizontal: 16,
                        borderRadius: 6,
                        elevation: 2,
                        width: 220,
                    }}>
                    <Text style={{fontSize: 16, fontWeight: 'bold', color: '#000'}}>
                        कोई परिवर्तन नहीं
                    </Text>
                </TouchableOpacity>
            </View>
        </Modal>
    );
    const sku1 = () => {
        return (
            <View style={{borderBottomWidth: 1, borderBottomColor: '#e1e1e1', paddingTop: 10}}>
                <View style={{flexDirection: 'row'}}>
                    <View style={{flex: 2, alignItems: 'center'}}>
                        <Image
                            source={{ uri: 'https://5.imimg.com/data5/DN/RL/MY-42502830/green-tomato-500x500.jpg' }}
                            style={{ width: 58, height: 58, resizeMode: 'cover'}}
                        />
                    </View>
                    <View style={{ flex: 5,}}>
                        <Text style={{fontWeight: 'bold', color: '#000'}}>tomato - ₹200</Text>
                        <Text style={{fontSize: 12, marginBottom: 4}}>RohiniGreen</Text>
                        <View style={{flexDirection: 'row', alignItems: 'center', paddingRight: 6}}>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingRight: 4}}>25kg</Text>
                            </View>
                            <View style={{ flex: 1.8}}>
                                <Text style={{fontSize: 12, paddingLeft: 4}}>Grade: Medium</Text>
                            </View>
                        </View>
                        <View style={{flexDirection: 'row', alignItems: 'center'}}>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingRight: 4}}>Perish: 10 days</Text>
                            </View>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingLeft: 4}}>Logistics: crate</Text>
                            </View>
                        </View>
                        <View style={{flexDirection: 'row', alignItems: 'center'}}>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingRight: 4}}>Cold chain: yes</Text>
                            </View>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingLeft: 4}}>Del tat: 1</Text>
                            </View>
                        </View>
                    </View>
                    <View style={{flex: 1}}>
                        <Text style={{textAlign: 'center'}}>2</Text>
                    </View>
                    <View style={{ flex: 2,}}>
                        <Text style={{textAlign: 'center'}}>8</Text>
                    </View>
                </View>
                <View style={{flexDirection: 'row', marginVertical: 10}}>
                    <TouchableOpacity
                        onPress={() => { setVisible(true)}}
                        style={{
                            flex: 1,
                            backgroundColor: '#ffd0af',
                            paddingVertical: 8,
                            alignItems: 'center',
                            marginHorizontal: 16,
                            borderRadius: 6,
                            elevation: 2,
                        }}>
                        <Text style={{fontSize: 16, fontWeight: 'bold', color: '#e56200'}}>
                            भाव बदलें
                        </Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                        onPress={() => {
                            whatsappShare();
                        }}
                        style={{
                            flex: 1,
                            backgroundColor: '#adffbd',
                            paddingVertical: 8,
                            alignItems: 'center',
                            marginHorizontal: 16,
                            borderRadius: 6,
                            elevation: 2,
                        }}>
                        <Text style={{fontSize: 16, fontWeight: 'bold', color: '#189f31'}}>
                            शेयर करेंं
                        </Text>
                    </TouchableOpacity>
                </View>
            </View>
        );
    }
    const sku2 = () => {
        return (
            <View style={{borderBottomWidth: 1, borderBottomColor: '#e1e1e1', paddingTop: 10}}>
                <View style={{flexDirection: 'row', paddingTop: 10}}>
                    <View style={{flex: 2, alignItems: 'center'}}>
                        <Image
                            source={{ uri: 'https://www.bigbasket.com/media/uploads/p/m/10000025-2_3-fresho-banana-robusta.jpg' }}
                            style={{ width: 58, height: 58, resizeMode: 'cover'}}
                        />
                    </View>
                    <View style={{ flex: 5,}}>
                        <Text style={{fontWeight: 'bold', color: '#000'}}>banana - ₹150</Text>
                        <Text style={{fontSize: 12, marginBottom: 4}}>Robustayellow with brown spots</Text>
                        <View style={{flexDirection: 'row', alignItems: 'center', paddingRight: 6}}>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingRight: 4}}>1 bunch</Text>
                            </View>
                            <View style={{ flex: 1.8}}>
                                <Text style={{fontSize: 12, paddingLeft: 4}}>Grade: Large</Text>
                            </View>
                        </View>
                        <View style={{flexDirection: 'row', alignItems: 'center'}}>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingRight: 4}}>Perish: 1 days</Text>
                            </View>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingLeft: 4}}>Logistics: pallet</Text>
                            </View>
                        </View>
                        <View style={{flexDirection: 'row', alignItems: 'center'}}>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingRight: 4}}>Cold chain: no</Text>
                            </View>
                            <View style={{ flex: 1}}>
                                <Text style={{fontSize: 12, paddingLeft: 4}}>Del tat: 1</Text>
                            </View>
                        </View>
                    </View>
                    <View style={{flex: 1}}>
                        <Text style={{textAlign: 'center'}}>1</Text>
                    </View>
                    <View style={{ flex: 2,}}>
                        <Text style={{textAlign: 'center'}}>2</Text>
                    </View>
                </View>

                <View style={{flexDirection: 'row', marginVertical: 10}}>
                    <TouchableOpacity
                        onPress={() => { setVisible(true)}}
                        style={{
                            flex: 1,
                            backgroundColor: '#ffd0af',
                            paddingVertical: 8,
                            alignItems: 'center',
                            marginHorizontal: 16,
                            borderRadius: 6,
                            elevation: 2,
                        }}>
                        <Text style={{fontSize: 16, fontWeight: 'bold', color: '#e56200'}}>
                            भाव बदलें
                        </Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                        onPress={() => {
                            whatsappShare();
                        }}
                        style={{
                            flex: 1,
                            backgroundColor: '#adffbd',
                            paddingVertical: 8,
                            alignItems: 'center',
                            marginHorizontal: 16,
                            borderRadius: 6,
                            elevation: 2,
                        }}>
                        <Text style={{fontSize: 16, fontWeight: 'bold', color: '#189f31'}}>
                            शेयर करेंं
                        </Text>
                    </TouchableOpacity>
                </View>
            </View>
        );
    }
    const header = () => {
        return (
            <View style={{flexDirection: 'row', borderBottomWidth: 1, borderBottomColor: '#e1e1e1', paddingVertical: 10}}>
                <View style={{flex: 2, alignItems: 'center'}}>
                    <Text>Photo</Text>
                </View>
                <View style={{ flex: 5,}}>
                    <Text>Details</Text>
                </View>
                <View style={{flex: 1,}}>
                    <Text>qty</Text>
                </View>
                <View style={{ flex: 2}}>
                    <Text>Perish in</Text>
                </View>
            </View>
        );
    }
    return (
        <View style={styles.container}>
            {header()}
            {sku1()}
            {sku2()}
            {priceChangeModal()}
        </View>
    )
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'ivory',
    },
});

export default FarmInventoryScreen;

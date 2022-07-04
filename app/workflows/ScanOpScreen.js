import React, {useEffect, useLayoutEffect, useRef, useState} from 'react';
import {
    Alert,
    AppState, Image, Modal,
    SafeAreaView,
    ScrollView,
    StyleSheet,
    Text, TextInput, TouchableOpacity,
    useColorScheme,
    View,
} from 'react-native';
import {useIsFocused, useNavigation, useRoute} from '@react-navigation/native';
import {Colors} from 'react-native/Libraries/NewAppScreen';
import {Camera} from 'expo-camera';

function ScanOpScreen() {
    const appState = useRef();
    const [inward, setInward] = useState(false);
    const [isActive, setIsActive] = useState(appState.current);
    const [scanned, setScanned] = useState(false);
    const [sku, setSku] = useState(null);
    const isFocused = useIsFocused();
    const navigation = useNavigation();
    const route = useRoute();
    const [visible, setVisible] = useState(false);
    const [price, setPrice] = useState(0);
    useEffect(() => {
        if (route.params) {
            if ('inward' in route.params) {
                setInward(route.params.inward);
            }
        }
    }, []);
    useEffect(() => {
        if (price > 0) {
            setSku({...sku, price: price});
        }
    }, [price]);
    React.useLayoutEffect(() => {
        navigation.setOptions({
            title: 'स्कैन करें',
            headerCenter: () => null,
        });
    }, [navigation]);
    useLayoutEffect(() => {
        const subscription = AppState.addEventListener(
            'change',
            nextAppState => {
                appState.current = nextAppState;

                setIsActive(appState.current === 'active');
            },
        );

        return () => {
            if (subscription?.remove) {
                subscription?.remove();
            }
        };
    }, []);

    const backgroundStyle = {
        backgroundColor: 'ivory',
    };
    const [hasPermission, setHasPermission] = useState(null);
    useEffect(() => {
        (async () => {
            const {status} = await Camera.requestCameraPermissionsAsync();
            setHasPermission(status === 'granted');
        })();
    }, []);

    if (hasPermission === null) {
        return <View />;
    }
    if (hasPermission === false) {
        return <Text>No access to camera</Text>;
    }
    const handleInward = () => {
        // todo: implement
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
                    <Text style={{fontSize: 18, paddingTop: 20, paddingBottom: 8,}}>
                        भाव दर्ज करें
                    </Text>
                    <TextInput
                        keyboardType={'number-pad'}
                        autoFocus={true}
                        value={price}
                        onChangeText={setPrice}
                        placeholder={'उदाहरण के लिए - 224'}
                        style={{
                            textAlign: 'center',
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
                        onPress={() => {setVisible(false)}}
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
                            भाव पुष्टि करें
                        </Text>
                    </TouchableOpacity>
                </View>
                <TouchableOpacity
                    onPress={() => {
                        setVisible(false);
                    }}
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
    //@ts-ignore
    const handleBarCodeScanned = ({type, data}) => {
        setScanned(true);
        console.log(type, data);
        const terms = data.split(', ');
        // const _sku = {
        //     skuId: terms[0],
        //     productName: terms[1],
        //     picUrl: terms[2],
        //     packSize: terms[3],
        //     description: terms[4],
        //     grade: terms[5],
        //     variety: terms[6],
        //     variant: terms[7],
        //     persishability: terms[8],
        //     logistics: terms[9],
        //     coldChainReq: terms[10],
        //     ideaDelTat: terms[11],
        // };
        const _sku = {
            'मात्रा': 1,
            'पहचान': terms[0],
            'नाम': terms[1],
            'तस्वीर': terms[2],
            'आकार': terms[3],
            'विवरण': terms[4],
            'श्रेणी': terms[5],
            'विविधता': terms[6],
            'प्रकार': terms[7],
            'भंगुरता': terms[8],
            'यात्रा': terms[9],
            'ठंडा': terms[10],
            'डिलीवरी समय': terms[11],
        };
        setSku(_sku);
        if (inward) {
            setVisible(true);
        }
        // navigation.navigate('RideTracking');
    };
    const startOver = () => {
        setSku(null);
        setScanned(false);
    };
    return (
        <SafeAreaView style={[backgroundStyle, { flex: 1}]}>
            <View style={{flex: 8}}>
                <ScrollView
                    contentInsetAdjustmentBehavior="automatic"
                    style={[backgroundStyle, {flex: 1}]}>
                    <View style={{alignItems: 'center', paddingVertical: 10, flex: 1}}>
                        {isActive && isFocused && !scanned && (
                            <Camera
                                style={styles.camera}
                                type={0}
                                barCodeScannerSettings={{
                                    barCodeTypes: ['qr'],
                                }}
                                onBarCodeScanned={
                                    scanned ? undefined : handleBarCodeScanned
                                }
                            />
                        )}
                    </View>
                    {sku !== null && (
                        <View style={{ paddingHorizontal: 16, paddingBottom: 10}}>
                            <Text style={{fontSize: 18, fontWeight: 'bold', color: 'black', paddingBottom: 10, borderBottomWidth: 1, borderBottomColor: '#e1e1e1'}}>अंदर हुआ</Text>
                            {Object.keys(sku).map(k => (
                                <View key={k} style={{ flexDirection: 'row', paddingVertical: 12, borderBottomWidth: 1, borderBottomColor: '#e1e1e1'}}>
                                    <View style={{ width: 94}}>
                                        <Text>{k}: </Text>
                                    </View>
                                    <View>
                                        {k === 'तस्वीर' ? (
                                            <Image source={{ uri: sku[k]}} style={{ width: 80, height: 80, resizeMode: 'cover'}} />
                                        ) : (
                                            <Text>
                                                {sku[k]}
                                            </Text>
                                        )}
                                    </View>
                                </View>
                            ))}
                        </View>
                    )}
                </ScrollView>
            </View>
            <View style={{ flex: 1}}>
                {sku !== null && (
                    <View style={{flexDirection: 'row', alignItems: 'center', justifyContent: 'center'}}>
                        <TouchableOpacity
                            onPress={startOver}
                            style={{
                                backgroundColor: 'coral',
                                paddingVertical: 12,
                                alignItems: 'center',
                                marginRight: 16,
                                borderRadius: 6,
                                elevation: 6,
                                width: 148,
                            }}>
                            <Text style={{fontSize: 16, fontWeight: 'bold', color: '#000'}}>और एक</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            onPress={() => navigation.goBack()}
                            style={{
                                backgroundColor: 'lightgreen',
                                paddingVertical: 12,
                                alignItems: 'center',
                                borderRadius: 6,
                                elevation: 6,
                                width: 148,
                            }}>
                            <Text style={{fontSize: 16, fontWeight: 'bold', color: '#000'}}>हो गया</Text>
                        </TouchableOpacity>
                    </View>
                )}
            </View>
            {inward && priceChangeModal()}
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'ivory',
        paddingTop: 20,
    },
    camera: {
        height: 300,
        width: 300,
    },
});

export default ScanOpScreen;

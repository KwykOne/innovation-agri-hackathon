import React, {useContext, useState} from 'react';
import {Text, TextInput, TouchableOpacity, View} from 'react-native';
import {useDispatch} from 'react-redux';
import {addFarmer} from '../redux/actions';
import {API_HOST} from '../constants';
import {AppContext} from '../ctx';

function Login(props: {onLogin: () => void}) {
    const [phone, setPhone] = useState('');
    const [farmerName, setFarmerName] = useState('चिंटू');
    const [showOtp, setShowOtp] = useState(false);
    const [otp, setOtp] = useState('2245');
    const dispatch = useDispatch();
    const handleLogin = async () => {
        const payload = {phone, farmerName};
        console.log(JSON.stringify(payload));
        const res = await fetch(API_HOST + '/login', {
            method: 'POST',
            body: JSON.stringify(payload),
            headers: {'Content-Type': 'application/json'},
        });
        const data = await res.json();
        console.log('login response', data.data.farmer);
        dispatch(addFarmer(data.data.farmer));
        props.onLogin();
    };
    return (
        <View style={{flex: 1, backgroundColor: 'ivory', padding: 20}}>
            <Text style={{fontSize: 32, fontWeight: 'bold', color: 'black'}}>रजिस्टर करें</Text>
            <Text style={{fontSize: 18, paddingTop: 10, paddingBottom: 8,}}>
                अपना नाम दर्ज करें
            </Text>
            <TextInput
                autoFocus={true}
                value={farmerName}
                onChangeText={setFarmerName}
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
                अपना मोबाइल नंबर दर्ज करें
            </Text>
            <TextInput
                autoFocus={true}
                value={phone}
                onChangeText={setPhone}
                keyboardType={'phone-pad'}
                placeholder={'उदाहरण के लिए - 9876543210'}
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
                ओटीपी स्वतः पता लगा रहे हे ...
            </Text>
            {showOtp && (
                <TextInput
                    autoFocus={true}
                    value={otp}
                    onChangeText={setOtp}
                    keyboardType={'number-pad'}
                    placeholder={'उदाहरण के लिए 2245'}
                    style={{
                        marginTop: 12,
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
            )}
            <View style={{ paddingVertical: 20}}>
                <TouchableOpacity
                    onPress={() => {
                        if (!showOtp) {
                            setShowOtp(true);
                        }
                        console.log(showOtp)
                        console.log(otp.length === 4)
                        console.log(otp.length)
                        if (showOtp && otp.length === 4) {
                            handleLogin().then(_ => {});
                        }
                    }}
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

export default Login;

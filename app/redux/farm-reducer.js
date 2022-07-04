//initial state
import {ADD_FARMER} from './action-types';

const initialState = {
    farmId: 0,
    farmName: '',
};

const farmReducer = (
    state = initialState,
    action: any,
) => {
    switch (action.type) {
        case ADD_FARMER:
            return {
                ...state,
                ...action.data,
            };
        default:
            return state;
    }
};

export default farmReducer;

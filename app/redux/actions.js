import {ADD_FARM, ADD_FARMER} from './action-types';

export const addFarm = (data) => ({
    type: ADD_FARM,
    data,
});

export const addFarmer = (data) => ({
    type: ADD_FARMER,
    data,
});

import '../App.css'

import { useForm } from 'react-hook-form';
import { useState } from 'react';

function SearchForm() {
    const { register, handleSubmit, watch, formState: { errors } } = useForm();
    const [searchResults, setSearchResults] = useState({});

    const makeQuery = data => {
        let items = []
        for (var key in data) {
            if (data[key] !== null && data[key] != "")
                items.push(`${key}:${data[key]};`);
        }
        return items.join("");
    }

    const onSubmit = async(data) => {
        const query = makeQuery(data);
        const res = await fetch("http://localhost:5000/en/search?q=" + query, {
            method: "GET",
        });
        console.log('results', res.body);

        setSearchResults(res);
    };
    return ( <
            >
            <
            div className = "Search p-4" >
            <
            form onSubmit = { handleSubmit(onSubmit) } >
            <
            div className = 'row' >
            <
            div className = 'col-12 col-md-6' >
            <
            div className = 'pb-3' >
            <
            div > < label > कीवर्ड(search terms) < /label></div >
            <
            input {...register("terms") }
            /> {
                errors.terms && < span className = 'text-danger' > Invalid input < /span>} <
                    /div>

                <
                div className = 'pb-3' >
                    <
                    div > < label > उत्पाद श्रेणी(Category) < /label></div >
                    <
                    input {...register("type") }
                /> {
                    errors.type && < span className = 'text-danger' > Invalid input < /span>} <
                        /div>

                    <
                    div className = 'pb-3' >
                        <
                        div > < label > उत्पाद उप - श्रेणी(subcategory) < /label></div >
                        <
                        input {...register("subtype") }
                    /> {
                        errors.subtype && < span className = 'text-danger' > Invalid input < /span>} <
                            /div>

                        <
                        div className = 'pb-3' >
                            <
                            div > < label > अंतर्वस्तु(ingredients) < /label></div >
                            <
                            input {...register("ingredients") }
                        /> {
                            errors.ingredients && < span className = 'text-danger' > Invalid input < /span>} <
                                /div> <
                                /div>

                            <
                            div className = 'col-12 col-md-6' >
                                <
                                div className = 'PurposeSection mb-4' >
                                <
                                h5 className = 'border-bottom mb-3' > उत्पाद उद्देश्य(Purpose) < /h5> <
                                div className = 'pb-3' >
                                <
                                div > < label > गतिविधि(Product Action) < /label></div >
                                <
                                input {...register("action") }
                            /> {
                                errors.action && < span className = 'text-danger' > Invalid input < /span>} <
                                    /div>

                                <
                                div className = 'pb-3' >
                                    <
                                    div > < label > यह किस पर कार्य करता है(What this product acts on ? ) < /label></div >
                                    <
                                    input {...register("on") }
                                /> {
                                    errors.on && < span className = 'text-danger' > Invalid input < /span>} <
                                        /div> <
                                        /div>

                                    <
                                    div className = 'pb-3' >
                                        <
                                        div > < label > इस उत्पाद का उपयोग किस पर किया जाता है ? (What this product is used on - crop name / farm type ? ) < /label></div >
                                        <
                                        input {...register("subject") }
                                    /> {
                                        errors.subject && < span className = 'text-danger' > Invalid input < /span>} <
                                            /div>

                                        <
                                        div className = 'pb-3' >
                                            <
                                            div > < label > उत्पाद का उपयोग किस चरण में किया जाता है ? (What stage is the product used - harvest / plantation / growth etc. ? ) < /label></div >
                                            <
                                            input {...register("during") }
                                        /> {
                                            errors.during && < span className = 'text-danger' > Invalid input < /span>} <
                                                /div> <
                                                /div> <
                                                /div> <
                                                input className = 'btn btn-primary'
                                            type = "submit" / >
                                                <
                                                /form> <
                                                /div>

                                            <
                                            div className = "p-4 SearchResults" >
                                                <
                                                div className = 'pb-3' > < strong > कृषि उत्पाद परिणाम < /strong></div > {
                                                    searchResults & ({ searchResults })
                                                } <
                                                /div> <
                                                />

                                        );
                                    }

                                    export default SearchForm;
function validateReadings(prevReadings, currentRows) {
    var isInvalid = false
    for(const row of currentRows) {
        const flat = row["flat"]
        row["manjeeraInvalid"] = true
        row["boreInvalid"] = true
        if(flat in prevReadings) {
            if(prevReadings[flat].manjeera <= row.manjeera) {
                row["manjeeraInvalid"] = false
            }
            if(prevReadings[flat].bore <= row.bore) {
                row["boreInvalid"] = false
            }
        }
        isInvalid = isInvalid || row["manjeeraInvalid"] || row["boreInvalid"]
    }
    return isInvalid
}

const serviceRegistry = {validateReadings}
export {serviceRegistry}

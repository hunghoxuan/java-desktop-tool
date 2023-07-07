/*
This error can happen for several reasons.   
As the error states, Interchange member details are not defined this is used   for interchange calculations. 
After defining services and channels for any Card   Products, we need to define interchange details based on bin country,   settlement currency and client region we need to define acquirer bin,   forwarding member ID.
To resolve this issue, we need to goto CIS_INTERCHANGE_DETAILS   and create a record for that particular card organization with above parameters   taking into consideration client regions and bin countries.
*/

SELECT * FROM BW3.CIS_INTERCHANGE_DETAILS WHERE 
institution_number = '&institution_number' 
and CARD_ORGANIZATION = '&card_organization' 
and SETTLEMENT_CURRENCY = '&currency'
and SERVICE_TYPE = '&service_type'
and CLIENT_REGION = '&client_region';
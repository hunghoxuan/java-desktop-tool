update svc_Client_service
set effective_date = '99991231'
where institution_number = '00000108'
and client_number in ('98110103', '98110075')
and service_id in ('102','201','408');
commit;
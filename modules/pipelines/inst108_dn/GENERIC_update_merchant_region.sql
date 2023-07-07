update cis_client_details
set client_region = '000'
where institution_number = '00000108';
--
update cis_client_links
set settlement_method = '003'
where institution_number = '00000108';
commit;
